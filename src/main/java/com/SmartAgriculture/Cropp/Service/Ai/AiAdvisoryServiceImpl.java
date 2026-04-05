package com.SmartAgriculture.Cropp.Service.Ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.SmartAgriculture.Cropp.dtos.AdvisoryResponse;
import com.SmartAgriculture.Cropp.model.SensorData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiAdvisoryServiceImpl implements AiAdvisoryService {

    private final ChatClient chatClient;

    @Override
    public AdvisoryResponse generateDiseaseAdvisory(String diseaseName) {

        String safeDisease = sanitize(diseaseName);

        String prompt = """
                You are a certified agronomist providing practical, field-level advice to Indian farmers.

                Disease: %s

                Provide:
                - fertilizerRecommendation (specific product or nutrient focus)
                - pesticideRecommendation (specific active ingredient)
                - explanation (clear and practical)

                Respond ONLY in valid JSON matching this structure:
                {
                  "fertilizerRecommendation": "",
                  "pesticideRecommendation": "",
                  "explanation": ""
                }
                """.formatted(safeDisease);

        return callAi(prompt);
    }
    @Override
    public AdvisoryResponse generateHindiDiseaseAdvisory(String diseaseName) {

        String safeDisease = sanitize(diseaseName);

        String prompt = """
                You are a certified agronomist providing practical, field-level advice to Indian farmers.

                Disease: %s

                Provide:
                - fertilizerRecommendation (specific product or nutrient focus)
                - pesticideRecommendation (specific active ingredient)
                - explanation (clear and practical)

                Respond ONLY in valid JSON matching this structure and in Hindi Language(Farmer-friendly):
                {
                  "fertilizerRecommendation": "",
                  "pesticideRecommendation": "",
                  "explanation": ""
                }
                """.formatted(safeDisease);

        return callAi(prompt);
    }

    @Override
    public AdvisoryResponse generateCropAdvisory(String cropName,
                                                 SensorData sensorData) {

        String safeCrop = sanitize(cropName);

        String prompt = """
                You are a certified agronomist providing practical, field-level advice to Indian farmers.
                

                Crop: %s
                Temperature: %.2f °C
                Humidity: %.2f %%
                Nitrogen: %.2f
                Phosphorus: %.2f
                Potassium: %.2f
                Soil Moisture: %.2f

                Provide:
                - fertilizerRecommendation (specific product or nutrient focus)
                - pesticideRecommendation (if preventive needed)
                - explanation (clear and practical)

                Respond ONLY in valid JSON matching this structure:
                {
                  "fertilizerRecommendation": "",
                  "pesticideRecommendation": "",
                  "explanation": ""
                }
                """.formatted(
                safeCrop,
                sensorData.getTemperature(),
                sensorData.getHumidity(),
                sensorData.getNitrogen(),
                sensorData.getPhosphorus(),
                sensorData.getPotassium(),
                sensorData.getRainfall()
        );

        return callAi(prompt);
    }

    private AdvisoryResponse callAi(String prompt) {
        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(AdvisoryResponse.class);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI advisory service temporarily unavailable"
            );
        }
    }


     @Override
     public AdvisoryResponse generateHindiCropAdvisory(String cropName,
                                                 SensorData sensorData) {

        String safeCrop = sanitize(cropName);

        String prompt = """
                You are a certified agronomist providing practical, field-level advice to Indian farmers.
                

                Crop: %s
                Temperature: %.2f °C
                Humidity: %.2f %%
                Nitrogen: %.2f
                Phosphorus: %.2f
                Potassium: %.2f
                Soil Moisture: %.2f

                Provide:
                - fertilizerRecommendation (specific product or nutrient focus)
                - pesticideRecommendation (if preventive needed)
                - explanation (clear and practical)

                Respond ONLY in valid JSON matching this structure and in Hindi Language(Farmer-friendly):
                {
                  "fertilizerRecommendation": "",
                  "pesticideRecommendation": "",
                  "explanation": ""
                }
                """.formatted(
                safeCrop,
                sensorData.getTemperature(),
                sensorData.getHumidity(),
                sensorData.getNitrogen(),
                sensorData.getPhosphorus(),
                sensorData.getPotassium(),
                sensorData.getRainfall()
        );

        return callAi(prompt);
    }

    


    private String sanitize(String input) {
        if (input == null) return "";
        return input.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }



}


