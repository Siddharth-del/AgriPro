package com.SmartAgriculture.Cropp.service.ai;

import java.io.File;

import com.SmartAgriculture.Cropp.dtos.crop.CropRecommendationResponse;
import com.SmartAgriculture.Cropp.dtos.disease.DiseasePredictionResponse;
import com.SmartAgriculture.Cropp.model.SensorData;

public interface MlPredictionService {
     CropRecommendationResponse predictCrop(SensorData data);

    DiseasePredictionResponse detectDisease(File  image);
}
