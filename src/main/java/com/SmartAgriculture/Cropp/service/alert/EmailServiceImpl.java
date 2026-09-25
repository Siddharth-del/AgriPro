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
            DateTimeFormatter.ofPattern("dd MMM yyyy 'को' hh:mm a");

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
        String subject = "AgriPro में आपका स्वागत है, " + username + "!";
        String body = """
                नमस्ते %s,

                AgriPro में आपका स्वागत है — हमें बेहद खुशी है कि आप हमारे साथ जुड़े।

                आपका AgriPro खाता पूरी तरह तैयार है।
                हमने यह प्लेटफॉर्म इसलिए बनाया है ताकि खेती का प्रबंधन
                आसान हो जाए — ताकि आप कम चिंता में और ज़्यादा उगाने
                पर ध्यान दे सकें।

                अभी आप ये काम कर सकते हैं:

                  - अपनी मिट्टी और मौसम के अनुसार फसल की सिफारिश पाएं
                  - पौधे की बीमारी जाँचने के लिए फोटो अपलोड करें
                  - अपना ESP32 सेंसर जोड़ें और खेत की लाइव निगरानी करें
                  - नमी की सीमा तय करें और फसल को नुकसान से पहले अलर्ट पाएं
                  - हमारे AI सलाहकार से अपने खेत के बारे में कुछ भी पूछें

                शुरुआत करने के लिए, अपनी किसान प्रोफ़ाइल पूरी करें और
                सेंसर डिवाइस को जोड़ें। इसमें बस कुछ मिनट लगते हैं।

                अगर कोई परेशानी हो या कोई सवाल हो, तो बेझिझक संपर्क
                करें — हम आपकी मदद के लिए हमेशा तैयार हैं।

                इस मौसम में आपको शुभकामनाएं।

                सादर,
                AgriPro टीम
                """.formatted(username);
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendAlertEmail(String toEmail, String title, String message) {
        String subject = "ध्यान दें: " + title;
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String body = """
                नमस्ते,

                हम आपको बताना चाहते हैं कि आपके AgriPro निगरानी
                सिस्टम ने %s पर कुछ ऐसा पाया है जिस पर
                आपका ध्यान ज़रूरी है।

                क्या हुआ:
                %s

                विवरण:
                %s

                जब भी संभव हो, अपने खेत की जाँच करें।
                अगर सेंसर की रीडिंग गलत लग रही हो, तो देखें कि
                सेंसर की जाँच मिट्टी में सही तरह लगी है और
                डिवाइस का इंटरनेट कनेक्शन ठीक है।

                अगर यह बार-बार हो रहा हो, तो हमसे संपर्क करें —
                हम इसे सुलझाने में आपकी मदद करेंगे।

                आपका ख्याल रखें,
                AgriPro टीम
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
                ? "अत्यंत आवश्यक: " + city + " में आपके खेत को अभी पानी चाहिए"
                : "ध्यान दें: " + city + " में मिट्टी की नमी कम हो रही है";

        String opening = isCritical
                ? """
                  आपकी मिट्टी की नमी घटकर %.1f%% हो गई है, जो बेहद
                  कम है। इस स्तर पर आपकी फसलों को गंभीर नुकसान हो
                  सकता है। कृपया जल्द से जल्द सिंचाई शुरू करें।
                  """.formatted(soilMoisture)
                : """
                  आपकी मिट्टी की नमी %.1f%% है, जो सुरक्षित सीमा
                  30%% से कम है। अभी आपातकाल नहीं है, लेकिन फसलों
                  को जल्द पानी की ज़रूरत होगी।
                  """.formatted(soilMoisture);

        String body = """
                नमस्ते,

                आपके AgriPro सेंसर ने %s में %s पर
                एक ऐसी रीडिंग दर्ज की है जो हमें लगता है
                आपको जाननी चाहिए।

                %s
                सेंसर अभी यह दिखा रहा है:

                  मिट्टी की नमी  : %.1f%%  (सुरक्षित स्तर 30%% से ऊपर)
                  तापमान         : %.1f°C
                  हवा में नमी    : %.1f%%

                अब क्या करें:

                  1. सिंचाई सिस्टम चालू करें और खेत को पानी दें।
                  2. पाइप और वॉल्व की जाँच करें कि सब सही काम कर रहा है।
                  3. अगले एक घंटे में नमी का स्तर देखते रहें।
                  4. अगर सुधार न हो, तो अपने कृषि विशेषज्ञ से बात करें।

                अगर रीडिंग गलत लग रही हो, तो यह सुनिश्चित करें कि
                सेंसर की जाँच पूरी तरह मिट्टी में लगी है और
                आपके ESP32 डिवाइस का WiFi कनेक्शन स्थिर है।

                अगर स्थिति में सुधार नहीं हुआ तो हम फिर अलर्ट भेजेंगे,
                लेकिन कम से कम 10 मिनट का इंतज़ार करेंगे ताकि
                आपका इनबॉक्स संदेशों से न भर जाए।

                ध्यान रखें, और आपकी फसलें ठीक रहेंगी।

                AgriPro टीम

                ---
                यह अलर्ट आपके ESP32 फील्ड सेंसर द्वारा स्वचालित रूप से
                भेजा गया है। कृपया इस ईमेल का जवाब न दें।
                """.formatted(
                        city, timestamp,
                        opening,
                        soilMoisture, temperature, humidity
                );

        sendEmail(toEmail, subject, body);
    }

    @Override
    public boolean sendOtp(String toEmail, String subject, String body) {
          try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to: {}", toEmail);
            return  true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}