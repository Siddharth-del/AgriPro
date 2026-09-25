package com.SmartAgriculture.Cropp.service.alert.Crop;

import java.util.List;

import com.SmartAgriculture.Cropp.dtos.crop.CropRecommendationResponse;
import com.SmartAgriculture.Cropp.dtos.crop.CropRequestDTO;

public interface CropRecommendationService {
    CropRecommendationResponse recommendCrop(CropRequestDTO request);
       List<CropRecommendationResponse> getCropByName(String name);
        public List<CropRecommendationResponse> getAllCrop();
}
