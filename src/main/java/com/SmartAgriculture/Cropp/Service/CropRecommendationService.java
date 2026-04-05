package com.SmartAgriculture.Cropp.Service;

import java.util.List;

import com.SmartAgriculture.Cropp.dtos.CropRecommendationResponse;
import com.SmartAgriculture.Cropp.dtos.CropRequestDTO;

public interface CropRecommendationService {
    CropRecommendationResponse recommendCrop(CropRequestDTO request);
       List<CropRecommendationResponse> getCropByName(String name);
        public List<CropRecommendationResponse> getAllCrop();
}
