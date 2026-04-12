package com.SmartAgriculture.Cropp.service.alert;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy 'at' hh:mm a");

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = "Welcome to AgriPro, " + username + "!";
        String body = """
                Hi %s,

                Welcome aboard — we're really glad you're here.

                Your AgriPro account is all set up and ready to go.
                We built this platform to make farm management simpler,
                so you can spend less time worrying and more time growing.

                Here's what you can do right away:

                  - Get crop recommendations based on your soil and weather
                  - Upload a photo to check if your plant has a disease
                  - Connect your ESP32 sensor and track your field live
                  - Set moisture thresholds and get alerts before crops suffer
                  - Ask our AI advisor anything about your farm

                To get started, complete your farmer profile and link
                your sensor device. It only takes a few minutes.

                If you run into any trouble or have questions, just reach
                out — we're happy to help.

                Good luck with the season ahead.

                Warm regards,
                The AgriPro Team
                """.formatted(username);
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendAlertEmail(String toEmail, String title, String message) {
        String subject = "Action needed: " + title;
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String body = """
                Hi,

                We wanted to let you know that your AgriPro monitoring
                system flagged something on %s that needs your attention.

                What happened:
                %s

                Details:
                %s

                Please take a look at your field when you get a chance.
                If something looks off with your sensor or the reading
                seems incorrect, check that the probe is properly placed
                in the soil and the device has a stable connection.

                If this keeps happening, feel free to contact us and
                we'll help you sort it out.

                Take care,
                The AgriPro Team
                """.formatted(timestamp, title, message);
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendIrrigationAlert(String toEmail, String city,
                                     double soilMoisture, double temperature,
                                     double humidity) {

        boolean isCritical = soilMoisture < 15;
        String timestamp   = LocalDateTime.now().format(FORMATTER);

        String subject = isCritical
                ? "Urgent: Your field in " + city + " needs water now"
                : "Heads up: Soil moisture is getting low in " + city;

        String opening = isCritical
                ? """
                  Your soil moisture has dropped to %.1f%%, which is critically
                  low. At this level, your crops are at real risk of stress and
                  damage. Please start irrigating as soon as possible.
                  """.formatted(soilMoisture)
                : """
                  Your soil moisture is at %.1f%%, which is below the safe
                  threshold of 30%%. It's not an emergency yet, but your crops
                  will need water soon to stay healthy.
                  """.formatted(soilMoisture);

        String body = """
                Hi,

                Your AgriPro sensor picked up a reading in %s on %s
                that we think you should know about.

                %s
                Here's what the sensor is currently showing:

                  Soil moisture  : %.1f%%  (safe level is above 30%%)
                  Temperature    : %.1f°C
                  Air humidity   : %.1f%%

                What to do next:

                  1. Turn on your irrigation system and water the field.
                  2. Check that your pipes and valves are clear and working.
                  3. Keep an eye on the moisture level over the next hour.
                  4. If it doesn't recover, consider calling your agronomist.

                A couple of things worth checking if the reading looks wrong:
                make sure the sensor probe is fully inserted into the soil
                and that your ESP32 device has a stable WiFi connection.

                We'll send you another alert if the situation doesn't improve,
                but we'll wait at least 10 minutes so we don't flood your inbox.

                Stay on top of it and your crops will be fine.

                The AgriPro Team

                ---
                This alert was sent automatically by your ESP32 field sensor.
                Please do not reply to this email.
                """.formatted(
                        city, timestamp,
                        opening,
                        soilMoisture, temperature, humidity
                );

        sendEmail(toEmail, subject, body);
    }
}