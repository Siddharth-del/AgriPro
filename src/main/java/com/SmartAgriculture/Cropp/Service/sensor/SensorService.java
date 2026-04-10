package com.SmartAgriculture.Cropp.service.sensor;

import com.SmartAgriculture.Cropp.dtos.*;
import com.SmartAgriculture.Cropp.dtos.sensor.SensorAutoRequest;
import com.SmartAgriculture.Cropp.dtos.sensor.SensorLatestResponse;
import com.SmartAgriculture.Cropp.model.SensorData;
import com.SmartAgriculture.Cropp.model.User;
import com.SmartAgriculture.Cropp.repository.SensorDataRepository;
import com.SmartAgriculture.Cropp.repository.UserRepository;
import com.SmartAgriculture.Cropp.service.WeatherService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final WeatherService weatherService;
    private final SensorDataRepository sensorDataRepository;
    private final UserRepository userRepository;

    @Value("${irrigation.moisture.threshold:30}")
    private double threshold;

    @Transactional
    public String processAndAlert(SensorAutoRequest request) {
        double moisture = request.getSoilMoisture();
        String city     = request.getCity();

        User deviceUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUsername()));

        double temp = 0, humidity = 0;
        try {
            WeatherResponse weather = weatherService.getWeatherCity(city);
            temp     = weather.getMain().getTemp();
            humidity = weather.getMain().getHumidity();
        } catch (Exception e) {
            log.warn("Weather fetch failed for city={}: {}", city, e.getMessage());
        }

        SensorData data = new SensorData();
        data.setDeviceId(request.getDeviceId());
        data.setSoilMoisture(moisture);
        data.setTemperature(temp);
        data.setHumidity(humidity);
        data.setUser(deviceUser);
        sensorDataRepository.save(data);

        String status = classifyMoisture(moisture);
        log.info("Sensor saved: user={}, moisture={}%, status={}", deviceUser.getUsername(), moisture, status);

        return status;
    }

    public SensorLatestResponse getLatestReading() {
        return sensorDataRepository.findTopOrderByCreatedAtDesc()
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
        if (moisture == null)            return "Unknown";
        if (moisture >= threshold)       return "OK";
        if (moisture >= threshold * 0.6) return "WARNING";
        return "CRITICAL";
    }
}