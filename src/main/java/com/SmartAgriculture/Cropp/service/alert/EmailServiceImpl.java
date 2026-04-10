package com.SmartAgriculture.Cropp.service.alert;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = "Welcome to AgriPro — Smart Agriculture Platform";
        String body = """
                Dear %s,

                Welcome to AgriPro — Smart Agriculture System!

                We are delighted to have you on board. Your account has been
                successfully created and is ready to use.

                With AgriPro, you can:

                  ✅  Get AI-Powered Crop Recommendations
                  ✅  Detect Plant Diseases via Image Analysis
                  ✅  Receive Smart Irrigation Alerts
                  ✅  Access Real-Time Field Monitoring
                  ✅  Get Expert AI Agricultural Advisory

                If you have any questions or need assistance, please do not
                hesitate to reach out to our support team.

                We wish you a productive and fruitful harvest season.

                Warm Regards,
                ─────────────────────────────────────────────
                AgriPro Support Team
                Smart Agriculture IoT Platform
                Powered by ESP32 | OpenWeatherMap | Spring AI
                ─────────────────────────────────────────────
                """.formatted(username);
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendAlertEmail(String toEmail, String title, String message) {
        String subject = "⚠️ AgriPro Alert: " + title;
        String body = """
                Dear Farmer,

                This is an automated alert from the AgriPro Smart Agriculture System.

                ──────────────────────────────────────────────
                ALERT NOTIFICATION
                ──────────────────────────────────────────────

                  Title   : %s
                  Details : %s

                ──────────────────────────────────────────────

                Please review the above alert and take the necessary action
                at the earliest to avoid any potential crop loss or damage.

                If you believe this alert was triggered in error, please
                verify your sensor connections and field conditions.

                This is a system-generated message. Please do not reply
                directly to this email.

                Regards,
                ─────────────────────────────────────────────
                AgriPro Alert System
                Smart Agriculture IoT Platform
                Powered by ESP32 | OpenWeatherMap | Spring AI
                ─────────────────────────────────────────────
                """.formatted(title, message);
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendIrrigationAlert(String toEmail, String city,
                                     double soilMoisture, double temperature,
                                     double humidity) {

        String urgencyLevel  = soilMoisture < 15 ? "CRITICAL" : "WARNING";
        String urgencySymbol = soilMoisture < 15 ? "🔴" : "🟡";

        String subject = urgencySymbol + " [" + urgencyLevel + "] Irrigation Alert — " + city + " | AgriPro";

        String body = """
                Dear Farmer,

                AgriPro's IoT monitoring system has detected that your field's
                soil moisture level has dropped below the safe threshold.
                Immediate attention is required to protect your crops.

                ══════════════════════════════════════════════
                  %s  IRRIGATION ALERT — %s
                ══════════════════════════════════════════════

                  Severity      : %s
                  Location      : %s
                  Detected At   : (Timestamp provided by server)

                ──────────────────────────────────────────────
                  CURRENT FIELD CONDITIONS
                ──────────────────────────────────────────────

                  🌱 Soil Moisture  : %.1f%%   ⚠️  Threshold: 30%%
                  🌡  Temperature    : %.1f°C
                  💧 Humidity       : %.1f%%

                ──────────────────────────────────────────────
                  RECOMMENDED ACTION
                ──────────────────────────────────────────────

                  1. Begin irrigation of affected fields immediately.
                  2. Check and inspect your irrigation pipeline for blockages.
                  3. Monitor moisture levels for the next 1–2 hours.
                  4. If moisture does not recover, consult your agronomist.

                ──────────────────────────────────────────────
                  ℹ️  NOTE
                ──────────────────────────────────────────────

                  • This alert was triggered automatically by your ESP32
                    soil moisture sensor.
                  • A cooldown of 10 minutes is applied to avoid duplicate
                    alerts for the same field condition.
                  • Ensure your sensor is properly inserted into the soil
                    for accurate readings.

                ══════════════════════════════════════════════

                This is an automated message from AgriPro IoT System.
                Please do not reply to this email.

                ─────────────────────────────────────────────
                AgriPro Smart Agriculture Platform
                Powered by ESP32 | OpenWeatherMap | Spring AI
                © 2025 AgriPro. All rights reserved.
                ─────────────────────────────────────────────
                """.formatted(
                        urgencySymbol, city,
                        urgencyLevel, city,
                        soilMoisture, temperature, humidity
                );

        sendEmail(toEmail, subject, body);
    }
}