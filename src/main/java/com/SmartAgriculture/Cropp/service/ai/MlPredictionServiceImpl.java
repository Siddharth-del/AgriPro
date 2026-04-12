package com.SmartAgriculture.Cropp.service.ai;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.SmartAgriculture.Cropp.dtos.crop.CropRecommendationResponse;
import com.SmartAgriculture.Cropp.dtos.disease.DiseasePredictionResponse;
import com.SmartAgriculture.Cropp.exception.InvalidImageException;
import com.SmartAgriculture.Cropp.model.SensorData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MlPredictionServiceImpl implements MlPredictionService {

    private static final double MIN_CONFIDENCE_THRESHOLD = 0.60;

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

            CropRecommendationResponse cropResponse = new CropRecommendationResponse();
            cropResponse.setCropName((String) body.get("prediction"));
            cropResponse.setCropConfidence(((Number) body.get("confidence")).doubleValue());

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

            ResponseEntity<DiseasePredictionResponse> response = restTemplate.postForEntity(
                    diseaseApiUrl, requestEntity, DiseasePredictionResponse.class);

            if (response.getBody() == null) {
                throw new RuntimeException("ML disease detection returned null body");
            }

            DiseasePredictionResponse result = response.getBody();

            if (result.getConfidence() < MIN_CONFIDENCE_THRESHOLD) {
                throw new InvalidImageException(
                        "The uploaded image does not appear to be a plant leaf.",
                        "Please upload a clear close-up photo of a plant leaf.");
            }

            return result;
        } catch (InvalidImageException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("ML disease detection failed: " + e.getMessage(), e);
        }
    }
}