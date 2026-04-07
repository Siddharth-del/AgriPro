package com.SmartAgriculture.Cropp.service.sensor;

import com.SmartAgriculture.Cropp.dtos.*;
import com.SmartAgriculture.Cropp.dtos.sensor.SensorAutoRequest;
import com.SmartAgriculture.Cropp.dtos.sensor.SensorLatestResponse;
import com.SmartAgriculture.Cropp.model.SensorData;
import com.SmartAgriculture.Cropp.repository.SensorDataRepository;
import com.SmartAgriculture.Cropp.repository.UserRepository;
import com.SmartAgriculture.Cropp.service.WeatherService;
import com.SmartAgriculture.Cropp.service.alert.EmailService;
import com.SmartAgriculture.Cropp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final WeatherService       weatherService;
    private final EmailService         emailService;
    private final SensorDataRepository sensorDataRepository;
    private final UserRepository       userRepository;

    @Value("${irrigation.moisture.threshold:30}")
    private double threshold;

    @Value("${irrigation.email.cooldown.minutes:10}")
    private long cooldownMinutes;

    private final Map<String, LocalDateTime> lastEmailMap = new ConcurrentHashMap<>();
    private volatile SensorLatestResponse    latestReading = null;

    public String processAndAlert(SensorAutoRequest request) {
        double moisture = request.getSoilMoisture();
        String city     = request.getCity();

        WeatherResponse weather;
        try {
            weather = weatherService.getWeatherCity(city);
        } catch (Exception e) {
            log.error("Weather fetch failed for {}: {}", city, e.getMessage());
            return "Error: Weather fetch failed for " + city;
        }

        double temp     = weather.getMain().getTemp();
        double humidity = weather.getMain().getHumidity();

        SensorData data = new SensorData();
        data.setDeviceId(request.getDeviceId());
        data.setHumidity(humidity);
        data.setTemperature(temp);
        data.setNitrogen(moisture);
        sensorDataRepository.save(data);

        String status;
        String emailStatus = "Not Needed";

        if (moisture >= threshold) {
            status = "OK";
        } else if (moisture >= threshold * 0.6) {
            status      = "WARNING";
            emailStatus = sendToAllFarmers(city, moisture, temp, humidity);
        } else {
            status      = "CRITICAL";
            emailStatus = sendToAllFarmers(city, moisture, temp, humidity);
        }

        latestReading = SensorLatestResponse.builder()
                .soilMoisture(moisture)
                .temperature(temp)
                .humidity(humidity)
                .city(city)
                .status(status)
                .emailStatus(emailStatus)
                .recordedAt(LocalDateTime.now())
                .deviceId(request.getDeviceId())
                .build();

        log.info("ESP32 reading processed: moisture={}%, status={}, email={}", moisture, status, emailStatus);
        return status + " | Email: " + emailStatus;
    }

    public SensorLatestResponse getLatestReading() {
        if (latestReading == null) {
            return SensorLatestResponse.builder()
                    .status("No Data")
                    .emailStatus("None")
                    .recordedAt(LocalDateTime.now())
                    .build();
        }
        return latestReading;
    }

    public List<SensorLatestResponse> getHistory(int limit) {
        return latestReading == null ? List.of() : List.of(latestReading);
    }

    private String sendToAllFarmers(String city, double moisture, double temp, double humidity) {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.warn("No users found to send irrigation alert");
            return "No Users Found";
        }

        int sent = 0;
        int skipped = 0;

        for (User user : users) {
            String email = user.getEmail();
            if (email == null || email.isBlank()) continue;

            LocalDateTime now  = LocalDateTime.now();
            LocalDateTime last = lastEmailMap.get(email);

            if (last != null && last.plusMinutes(cooldownMinutes).isAfter(now)) {
                skipped++;
                continue;
            }

            try {
                emailService.sendIrrigationAlert(email, city, moisture, temp, humidity);
                lastEmailMap.put(email, now);
                log.info("Irrigation alert sent to {}", email);
                sent++;
            } catch (Exception e) {
                log.error("Email failed for {}: {}", email, e.getMessage());
            }
        }

        if (sent > 0)     return "Sent (" + sent + " user" + (sent > 1 ? "s" : "") + ")";
        if (skipped > 0)  return "Cooldown";
        return "Failed";
    }
}
