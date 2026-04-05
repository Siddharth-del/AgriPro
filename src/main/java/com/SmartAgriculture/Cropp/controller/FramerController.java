package com.SmartAgriculture.Cropp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SmartAgriculture.Cropp.Service.CropRecommendationService;
import com.SmartAgriculture.Cropp.Service.FarmerProfileService;
import com.SmartAgriculture.Cropp.dtos.FarmerProfileRequest;
import com.SmartAgriculture.Cropp.dtos.FarmerProfileResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/farmer")
@RequiredArgsConstructor
public class FramerController {

    private final CropRecommendationService cropRecommendationService;

    private final FarmerProfileService farmerProfileService;

    @PostMapping("/profile")
    public ResponseEntity<FarmerProfileResponse> createProfile(@Valid @RequestBody FarmerProfileRequest request) {
        FarmerProfileResponse response = farmerProfileService.createProfile(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<FarmerProfileResponse> getProfile(){
        FarmerProfileResponse profile=farmerProfileService.getProfile();
        return new ResponseEntity<>(profile,HttpStatus.OK);
    }
    @PutMapping("/profile")
    public ResponseEntity<FarmerProfileResponse> updateProfile(@Valid @RequestBody FarmerProfileRequest request){
         FarmerProfileResponse updatedProfile=farmerProfileService.updateProfile(request);
         return new  ResponseEntity<>(updatedProfile,HttpStatus.OK);
    }
    
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(){
        String deletedProfile=farmerProfileService.deleteProfile();
        return new ResponseEntity<>(deletedProfile,HttpStatus.OK);
    }

}
