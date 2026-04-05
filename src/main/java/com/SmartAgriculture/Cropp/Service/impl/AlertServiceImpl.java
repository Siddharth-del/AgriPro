package com.SmartAgriculture.Cropp.Service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.SmartAgriculture.Cropp.Service.AlertService;
import com.SmartAgriculture.Cropp.Service.EmailService;
import com.SmartAgriculture.Cropp.Service.WeatherService;
import com.SmartAgriculture.Cropp.dtos.AlertResponse;
import com.SmartAgriculture.Cropp.dtos.SoilDataRequest;
import com.SmartAgriculture.Cropp.dtos.WeatherData;
import com.SmartAgriculture.Cropp.dtos.WeatherResponse;
import com.SmartAgriculture.Cropp.utils.AuthUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final WeatherService weatherService;
    private final EmailService emailService;
    private final AuthUtil authUtil;

    @Value("${irrigation.moisture.threshold:30}")
    private double moistureThreshold;

    @Value("${irrigation.email.cooldown.minutes:10}")
    private long cooldownMinutes;

    // Tracks last email time per user — prevents spam
    private final Map<String, LocalDateTime> lastEmailMap = new ConcurrentHashMap<>();

    @Override
    public AlertResponse processAlert(String city, SoilDataRequest request) {

        double soilMoisture = request.getSoilMoisture();

        // Fetch live weather
        WeatherResponse weather = weatherService.getWeatherCity(city);
        double temperature  = weather.getMain().getTemp();
        double humidity     = weather.getMain().getHumidity();

        // Log all readings
        log.info("====== Irrigation Check ======");
        log.info("City          : {}", city);
        log.info("Soil Moisture : {}%", soilMoisture);
        log.info("Temperature   : {}°C", temperature);
        log.info("Humidity      : {}%", humidity);
        log.info("==============================");

        // Primary rule: moisture < threshold → irrigation needed
        boolean needsIrrigation = soilMoisture < moistureThreshold;

        if (!needsIrrigation) {
            log.info("Result: No irrigation needed.");
            return AlertResponse.builder()
                    .message("No Irrigation Needed")
                    .irrigationRequired(false)
                    .soilMoisture(soilMoisture)
                    .temperature(temperature)
                    .humidity(humidity)
                    .city(city)
                    .build();
        }

        // Secondary factor logging
        log.warn("Result: Irrigation REQUIRED! Moisture {}% < threshold {}%",
                soilMoisture, moistureThreshold);
        if (temperature > 35 && humidity < 40) {
            log.warn("High temp ({}°C) + Low humidity ({}%) — urgent situation!", temperature, humidity);
        }

        // Try to send email (respects cooldown)
        String userEmail = authUtil.loggedInEmail();
        boolean emailSent = sendEmailIfCooldownPassed(userEmail, city, soilMoisture, temperature, humidity);

        String message = emailSent
                ? "Irrigation Required - Email Sent"
                : "Irrigation Required - Email Skipped (Cooldown Active)";

        return AlertResponse.builder()
                .message(message)
                .irrigationRequired(true)
                .soilMoisture(soilMoisture)
                .temperature(temperature)
                .humidity(humidity)
                .city(city)
                .build();
    }

    private boolean sendEmailIfCooldownPassed(String email, String city,
                                               double moisture, double temp, double humidity) {
        LocalDateTime now      = LocalDateTime.now();
        LocalDateTime lastSent = lastEmailMap.get(email);

        if (lastSent != null && lastSent.plusMinutes(cooldownMinutes).isAfter(now)) {
            log.info("Cooldown active for {}. Next email allowed after: {}",
                    email, lastSent.plusMinutes(cooldownMinutes));
            return false;
        }

        emailService.sendIrrigationAlert(email, city, moisture, temp, humidity);
        lastEmailMap.put(email, now);
        log.info("Email sent to {} at {}", email, now);
        return true;
    }
}
