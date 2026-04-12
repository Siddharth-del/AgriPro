package com.SmartAgriculture.Cropp.service.sensor;

import com.SmartAgriculture.Cropp.dtos.*;
import com.SmartAgriculture.Cropp.dtos.sensor.SensorAutoRequest;
import com.SmartAgriculture.Cropp.dtos.sensor.SensorLatestResponse;
import com.SmartAgriculture.Cropp.model.AppRole;
import com.SmartAgriculture.Cropp.model.SensorData;
import com.SmartAgriculture.Cropp.model.User;
import com.SmartAgriculture.Cropp.repository.SensorDataRepository;
import com.SmartAgriculture.Cropp.repository.UserRepository;
import com.SmartAgriculture.Cropp.service.WeatherService;
import com.SmartAgriculture.Cropp.service.alert.EmailService;
import com.SmartAgriculture.Cropp.utils.AuthUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final WeatherService weatherService;
    private final EmailService emailService;
    private final SensorDataRepository sensorDataRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Value("${irrigation.moisture.threshold:30}")
    private double threshold;

    @Value("${irrigation.email.cooldown.minutes:10}")
    private long cooldownMinutes;

    // ── ADD THIS: injected from application.properties ──
    @Value("${iot.device.api.key}")
    private String validDeviceKey;

    private final Map<String, LocalDateTime> lastAlertMap = new ConcurrentHashMap<>();

    @Transactional
    public String processAndAlert(SensorAutoRequest request, String deviceKey) {
        double moisture = request.getSoilMoisture();
        String city = request.getCity();

        // Find the user who owns this specific device key
        User user = userRepository.findByDeviceApiKey(deviceKey)
                .orElseThrow(() -> new RuntimeException("No user found for this device key"));

        double temp = 0, humidity = 0;
        try {
            WeatherResponse weather = weatherService.getWeatherCity(city);
            temp = weather.getMain().getTemp();
            humidity = weather.getMain().getHumidity();
        } catch (Exception e) {
            log.warn("Weather fetch failed for city '{}': {}", city, e.getMessage());
        }

        SensorData data = new SensorData();
        data.setDeviceId(request.getDeviceId());
        data.setSoilMoisture(moisture);
        data.setTemperature(temp);
        data.setHumidity(humidity);
        data.setUser(user);
        sensorDataRepository.save(data);

        String status = classifyMoisture(moisture);
        log.info("Sensor saved: device={}, user={}, moisture={}%, status={}",
                request.getDeviceId(), user.getUsername(), moisture, status);

        if (!status.equals("OK")) {
            sendAlertToOwnerOnly(user.getEmail(), city, moisture, temp, humidity);
        }

        return status;
    }

    @Async
    public void processWeatherAndAlert(String city, double moisture, SensorData data) {
        try {
            WeatherResponse weather = weatherService.getWeatherCity(city);
            double temp = weather.getMain().getTemp();
            double humidity = weather.getMain().getHumidity();

            data.setTemperature(temp);
            data.setHumidity(humidity);
            sensorDataRepository.save(data);

            sendAlertsToFarmers(city, moisture, temp, humidity);
        } catch (Exception e) {
            log.error("Background processing failed: {}", e.getMessage());
        }
    }

    private void sendAlertsToFarmers(String city, double moisture, double temp, double humidity) {
        List<User> farmers = userRepository.findByRole(AppRole.ROLE_FARMER);

        if (farmers.isEmpty()) {
            log.warn("No farmers registered — no alerts sent");
            return;
        }

        int sent = 0, skipped = 0;

        for (User farmer : farmers) {
            String email = farmer.getEmail();
            if (email == null || email.isBlank())
                continue;

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime last = lastAlertMap.get(email);

            if (last != null && last.plusMinutes(cooldownMinutes).isAfter(now)) {
                skipped++;
                continue;
            }

            try {
                emailService.sendIrrigationAlert(email, city, moisture, temp, humidity);
                lastAlertMap.put(email, now);
                log.info("Alert sent to: {}", email);
                sent++;
            } catch (Exception e) {
                log.error("Failed to send alert to {}: {}", email, e.getMessage());
            }
        }

        log.info("Alert summary: sent={}, skipped={}, total={}", sent, skipped, farmers.size());
    }

    public SensorLatestResponse getLatestReading(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return sensorDataRepository
                .findTopByUserAndDeviceIdNotNullOrderByCreatedAtDesc(user) // <-- changed
                .map(data -> SensorLatestResponse.builder()
                        .soilMoisture(data.getSoilMoisture())
                        .temperature(data.getTemperature())
                        .humidity(data.getHumidity())
                        .status(classifyMoisture(data.getSoilMoisture()))
                        .emailStatus("—")
                        .recordedAt(data.getCreatedAt())
                        .deviceId(data.getDeviceId())
                        .build())
                .orElseGet(() -> SensorLatestResponse.builder()
                        .status("No Data")
                        .emailStatus("None")
                        .recordedAt(LocalDateTime.now())
                        .build());
    }

    public List<SensorLatestResponse> getHistory(int limit) {
        return sensorDataRepository.findLatestAll(Pageable.ofSize(limit))
                .stream()
                .map(data -> SensorLatestResponse.builder()
                        .soilMoisture(data.getSoilMoisture())
                        .temperature(data.getTemperature())
                        .humidity(data.getHumidity())
                        .status(classifyMoisture(data.getSoilMoisture()))
                        .recordedAt(data.getCreatedAt())
                        .deviceId(data.getDeviceId())
                        .build())
                .toList();
    }

    private String classifyMoisture(Double moisture) {
        if (moisture == null)
            return "Unknown";
        if (moisture >= threshold)
            return "OK";
        if (moisture >= threshold * 0.6)
            return "WARNING";
        return "CRITICAL";
    }

    public void resetSoilMoisture() {
        User user = authUtil.loggedInUser();

        SensorData sensorData = sensorDataRepository
                .findTopByUserAndDeviceIdNotNullOrderByCreatedAtDesc(user)
                .orElseGet(() -> {
                    SensorData data = new SensorData();
                    data.setUser(user);
                    return data;
                });

        sensorData.setSoilMoisture(0.0);
        sensorDataRepository.save(sensorData);
    }

    private void sendAlertToOwnerOnly(String email, String city,
            double moisture, double temp, double humidity) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last = lastAlertMap.get(email);

        if (last != null && last.plusMinutes(cooldownMinutes).isAfter(now)) {
            log.info("Alert skipped (cooldown): {}", email);
            return;
        }

        try {
            emailService.sendIrrigationAlert(email, city, moisture, temp, humidity);
            lastAlertMap.put(email, now);
            log.info("Alert sent to device owner: {}", email);
        } catch (Exception e) {
            log.error("Failed to send alert to {}: {}", email, e.getMessage());
        }
    }
}
