package com.SmartAgriculture.Cropp.service.alert;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
    void sendWelcomeEmail(String toEmail, String username);
    void sendAlertEmail(String toEmail, String title, String message);
    void sendIrrigationAlert(String toEmail, String city,
                              double soilMoisture, double temperature, double humidity);
}