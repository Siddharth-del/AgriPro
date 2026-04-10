package com.SmartAgriculture.Cropp.service.ai;

import com.SmartAgriculture.Cropp.dtos.ai.AdvisoryResponse;
import com.SmartAgriculture.Cropp.model.SensorData;

public interface AiAdvisoryService {
        AdvisoryResponse generateCropAdvisory(String cropName, SensorData sensorData);

        AdvisoryResponse generateDiseaseAdvisory(String diseaseName);

        public AdvisoryResponse generateHindiCropAdvisory(String cropName, SensorData sensorData);
        public AdvisoryResponse generateHindiDiseaseAdvisory(String diseaseName);
}
