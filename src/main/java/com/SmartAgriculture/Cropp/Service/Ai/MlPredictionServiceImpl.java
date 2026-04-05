package com.SmartAgriculture.Cropp.Service.Ai;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.SmartAgriculture.Cropp.dtos.CropRecommendationResponse;
import com.SmartAgriculture.Cropp.dtos.DiseasePredictionResponse;
import com.SmartAgriculture.Cropp.model.SensorData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MlPredictionServiceImpl implements MlPredictionService {

    private final RestTemplate restTemplate;

    @Value("${ml.crop.api.url}")
    private String cropApiUrl;

    @Value("${ml.disease.api.url}")
    private String diseaseApiUrl;

    @Override
    public CropRecommendationResponse predictCrop(SensorData data) {
        Map<String, Object> request = new HashMap<>();
        request.put("nitrogen", data.getNitrogen());
        request.put("phosphorus", data.getPhosphorus());
        request.put("potassium", data.getPotassium());
        request.put("temperature", data.getTemperature());
        request.put("humidity", data.getHumidity());
        request.put("ph", data.getPh());
        request.put("rainfall", data.getRainfall());

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(cropApiUrl, request, Map.class);
            Map<String, Object> body = response.getBody();

            String predictedCrop = (String) body.get("prediction");
            double confidence = ((Number) body.get("confidence")).doubleValue();

            CropRecommendationResponse cropResponse = new CropRecommendationResponse();
            cropResponse.setCropName(predictedCrop);
            cropResponse.setCropConfidence(confidence);

            return cropResponse;

        } catch (Exception e) {
            throw new RuntimeException("ML prediction failed", e);
        }
    }

    @Override
    public DiseasePredictionResponse detectDisease(File imageFile) {
        if (!imageFile.exists() || !imageFile.canRead()) {
            throw new RuntimeException("File not found or unreadable: " + imageFile.getAbsolutePath());
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(imageFile));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<DiseasePredictionResponse> response = restTemplate.postForEntity(diseaseApiUrl,
                    requestEntity, DiseasePredictionResponse.class);

            if (response.getBody() == null) {
                throw new RuntimeException("ML disease detection returned null body");
            }

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("ML disease detection failed: " + e.getMessage(), e);
        }
    }

}
