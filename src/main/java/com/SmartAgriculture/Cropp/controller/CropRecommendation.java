package com.SmartAgriculture.Cropp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SmartAgriculture.Cropp.dtos.crop.CropRecommendationResponse;
import com.SmartAgriculture.Cropp.dtos.crop.CropRequestDTO;
import com.SmartAgriculture.Cropp.service.alert.Crop.CropRecommendationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ml")
@RequiredArgsConstructor
public class CropRecommendation {

    private final CropRecommendationService cropRecommendationService;

    @PostMapping("/recommend-crop")
    public ResponseEntity<CropRecommendationResponse> recommendCrop(@Valid @RequestBody CropRequestDTO request) {
        CropRecommendationResponse response = cropRecommendationService.recommendCrop(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/crop/{cropName}")
    public ResponseEntity<List<CropRecommendationResponse>> getCropByName(@PathVariable String cropName){
         List<CropRecommendationResponse> crop = cropRecommendationService.getCropByName(cropName);
         return new ResponseEntity<>(crop, HttpStatus.OK);
    }
    @GetMapping("/crops")
    public ResponseEntity<List<CropRecommendationResponse>> getAllCrops(){
         List<CropRecommendationResponse> crops=cropRecommendationService.getAllCrop();
         return new ResponseEntity<>(crops,HttpStatus.OK);
    }
}
