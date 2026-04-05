package com.SmartAgriculture.Cropp.Service.Ai;

import com.SmartAgriculture.Cropp.dtos.AdvisoryResponse;
import com.SmartAgriculture.Cropp.model.SensorData;

public interface AiAdvisoryService {
        AdvisoryResponse generateCropAdvisory(String cropName, SensorData sensorData);

        AdvisoryResponse generateDiseaseAdvisory(String diseaseName);

        public AdvisoryResponse generateHindiCropAdvisory(String cropName, SensorData sensorData);
        public AdvisoryResponse generateHindiDiseaseAdvisory(String diseaseName);
}
