package com.SmartAgriculture.Cropp.Service;

import com.SmartAgriculture.Cropp.dtos.FarmerProfileRequest;
import com.SmartAgriculture.Cropp.dtos.FarmerProfileResponse;

public interface FarmerProfileService {
    FarmerProfileResponse createProfile(FarmerProfileRequest request);

    FarmerProfileResponse getProfile();

    FarmerProfileResponse updateProfile(FarmerProfileRequest request);

    String deleteProfile();

    

}
