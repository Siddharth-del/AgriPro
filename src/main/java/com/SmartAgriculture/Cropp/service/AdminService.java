package com.SmartAgriculture.Cropp.service;

import java.util.List;

import com.SmartAgriculture.Cropp.dtos.FarmerProfileResponse;

public interface AdminService {
    String deleteFarmer(Long userId);
    List<FarmerProfileResponse> getAllFarmers();
    FarmerProfileResponse getFarmerById(Long userId);
    
}
