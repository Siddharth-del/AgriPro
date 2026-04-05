package com.SmartAgriculture.Cropp.Service.Ai;

import java.io.File;

import com.SmartAgriculture.Cropp.dtos.CropRecommendationResponse;
import com.SmartAgriculture.Cropp.dtos.DiseasePredictionResponse;
import com.SmartAgriculture.Cropp.model.SensorData;

public interface MlPredictionService {
     CropRecommendationResponse predictCrop(SensorData data);

    DiseasePredictionResponse detectDisease(File  image);
}
