package com.SmartAgriculture.Cropp.service.alert;

import com.SmartAgriculture.Cropp.service.WeatherService;
import com.SmartAgriculture.Cropp.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl {

    private final EmailService emailService;
    private final WeatherService weatherService;
    private final AuthUtil authUtil;           // ← gets the logged-in user

    @Value("${irrigation.moisture.threshold:30}")
    private double threshold;

    @Value("${irrigation.email.cooldown.minutes:10}")
    private long cooldownMinutes;

    private final Map<String, LocalDateTime> lastEmailMap = new ConcurrentHashMap<>();

    public String processAlert(String city, double moisture) {
        // Only email the currently logged-in user — not all users
        String email = authUtil.loggedInEmail();

        var weather = weatherService.getWeatherCity(city);
        double temp     = weather.getMain().getTemp();
        double humidity = weather.getMain().getHumidity();

        if (moisture >= threshold) {
            return "No Irrigation Required";
        }

        // Check cooldown per user email
        LocalDateTime now  = LocalDateTime.now();
        LocalDateTime last = lastEmailMap.get(email);
        if (last != null && last.plusMinutes(cooldownMinutes).isAfter(now)) {
            return "Cooldown";
        }

        try {
            emailService.sendIrrigationAlert(email, city, moisture, temp, humidity);
            lastEmailMap.put(email, now);
            log.info("Irrigation alert sent to {}", email);
            return "Email Sent";
        } catch (Exception e) {
            log.error("Email failed for {}: {}", email, e.getMessage());
            return "Failed";
        }
    }
}