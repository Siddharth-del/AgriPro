package com.SmartAgriculture.Cropp.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.SmartAgriculture.Cropp.dtos.IrrigationDTO;
import com.SmartAgriculture.Cropp.dtos.WeatherResponse;
import com.SmartAgriculture.Cropp.utils.AuthUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrrigationServiceImpl implements IrrigationService {

    private final EmailService emailService;
    private final WeatherService weatherService;
    private final AuthUtil authUtil;

    @Value("${irrigation.moisture.threshold:30}")
    private double moistureThreshold;

    @Value("${irrigation.email.cooldown.minutes:10}")
    private long cooldownMinutes;

    private final Map<String, LocalDateTime> lastEmailMap = new ConcurrentHashMap<>();

    @Override
    public String getIrrigationAlert(IrrigationDTO irrigationDTO, String city) {

        // ── Step 1: Fetch weather ──────────────────────────────────
        WeatherResponse weatherResponse;
        try {
            weatherResponse = weatherService.getWeatherCity(city);
        } catch (Exception e) {
            log.error("Weather API failed for '{}': {}", city, e.getMessage());
            return "Error: Could not fetch weather for " + city
                    + ". Check WEATHER_API_KEY in application.properties.";
        }

        double soilMoisture = irrigationDTO.getSoilMoisture();
        double temperature  = weatherResponse.getMain().getTemp();
        double humidity     = weatherResponse.getMain().getHumidity();

        // ── Step 2: Log all readings ───────────────────────────────
        log.info("====== Irrigation Check ======");
        log.info("City          : {}", city);
        log.info("Soil Moisture : {}%", soilMoisture);
        log.info("Temperature   : {}°C", temperature);
        log.info("Humidity      : {}%", humidity);
        log.info("==============================");

        // ── Step 3: Primary decision ───────────────────────────────
        if (soilMoisture >= moistureThreshold) {
            log.info("No irrigation needed. Moisture={}%", soilMoisture);
            return "No Irrigation Needed";
        }

        log.warn("Irrigation REQUIRED! {}% < threshold {}%",
                soilMoisture, moistureThreshold);

        if (temperature > 35 && humidity < 40) {
            log.warn("High temp + Low humidity — urgent situation!");
        }

        // ── Step 4: Get logged-in user email safely ────────────────
        String userEmail = null;
        try {
            userEmail = authUtil.loggedInUser().getEmail();
            log.info("User email: {}", userEmail);
        } catch (Exception e) {
            log.warn("No authenticated user — email skipped. Send request with JWT token.");
        }

        if (userEmail == null) {
            return "Irrigation Required - No Authenticated User (Send JWT Token)";
        }

        // ── Step 5: Send email with cooldown ──────────────────────
        boolean sent = sendEmailIfCooldownPassed(
                userEmail, city, soilMoisture, temperature, humidity);

        return sent
                ? "Irrigation Required - Email Sent"
                : "Irrigation Required - Email Skipped (Cooldown Active)";
    }

    private boolean sendEmailIfCooldownPassed(String email, String city,
                                               double moisture, double temp,
                                               double humidity) {
        LocalDateTime now      = LocalDateTime.now();
        LocalDateTime lastSent = lastEmailMap.get(email);

        if (lastSent != null && lastSent.plusMinutes(cooldownMinutes).isAfter(now)) {
            log.info("Cooldown active for {}. Next allowed: {}",
                    email, lastSent.plusMinutes(cooldownMinutes));
            return false;
        }

        try {
            emailService.sendIrrigationAlert(email, city, moisture, temp, humidity);
            lastEmailMap.put(email, now);
            log.info("Irrigation alert email sent to {} at {}", email, now);
            return true;
        } catch (Exception e) {
            log.error("Email failed for {}: {}", email, e.getMessage());
            return false;
        }
    }
}