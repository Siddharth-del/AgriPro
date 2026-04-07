package com.SmartAgriculture.Cropp.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.SmartAgriculture.Cropp.dtos.FarmerProfileRequest;
import com.SmartAgriculture.Cropp.dtos.FarmerProfileResponse;
import com.SmartAgriculture.Cropp.exception.APIException;
import com.SmartAgriculture.Cropp.exception.ResourceNotFoundException;
import com.SmartAgriculture.Cropp.model.FarmerProfile;
import com.SmartAgriculture.Cropp.model.User;
import com.SmartAgriculture.Cropp.repository.FarmerProfileRepository;
import com.SmartAgriculture.Cropp.service.alert.EmailService;
import com.SmartAgriculture.Cropp.utils.AuthUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FarmerProfileServiceImpl implements FarmerProfileService {

    private final FarmerProfileRepository farmerProfileRepository;
    private final AuthUtil authUtils;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public FarmerProfileResponse createProfile(FarmerProfileRequest request) {

        User user = authUtils.loggedInUser();
        if (farmerProfileRepository.existsByUser(user)) {
            throw new APIException("Profile is already exist ");
        }
        if (farmerProfileRepository.existsByContactNumber(request.getContactNumber())) {
            throw new APIException("Contact number is already exist ");
        }
        FarmerProfile profile = new FarmerProfile();
        profile.setFullname(request.getFullname());
        profile.setContactNumber(request.getContactNumber());
        profile.setAddress(request.getAddress());
        profile.setVillage(request.getVillage());

        profile.setDistrict(request.getDistrict());
        profile.setPincode(request.getPincode());
        profile.setFarmLocation(request.getFarmLocation());
        profile.setState(request.getState());
        profile.setUser(user);

        FarmerProfile saved = farmerProfileRepository.save(profile);
         try {
               emailService.sendWelcomeEmail(user.getEmail(),user.getUsername()); 
        } catch (Exception e) {
            System.out.println("Email Sending failed: "+e.getMessage());
        }
        saved.getCreatedAt();

        return mapResponse(saved);
    }

    @Override
    @Transactional
    public FarmerProfileResponse getProfile() {
        User user = authUtils.loggedInUser();

        FarmerProfile profile = farmerProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("FarmerProfile", "user", user.getUserId()));

        return mapResponse(profile);
    }

    @Override
    @Transactional
    public FarmerProfileResponse updateProfile(FarmerProfileRequest request) {
        User user = authUtils.loggedInUser();
        FarmerProfile profile = farmerProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("FarmerProfile", "user", user.getUserId()));
        if (!profile.getContactNumber().equals(request.getContactNumber())
                && farmerProfileRepository.existsByContactNumber(request.getContactNumber())) {
            throw new APIException("Contact number is already register ");
        }

        profile.setFullname(request.getFullname());
        profile.setContactNumber(request.getContactNumber());
        profile.setDistrict(request.getDistrict());
        profile.setState(request.getState());
        profile.setFarmLocation(request.getFarmLocation());
        profile.setPincode(request.getPincode());
        profile.setVillage(request.getVillage());
        profile.setUpdatedAt(LocalDateTime.now());
        profile.setAddress(request.getAddress());
        FarmerProfile updated = farmerProfileRepository.save(profile);
        return mapResponse(updated);
    }

    @Override
    @Transactional
    public String deleteProfile() {
        User user = authUtils.loggedInUser();

        if (!farmerProfileRepository.existsByUser(user)) {
            throw new ResourceNotFoundException("Profile", "User", user.getUserId());
        }

        farmerProfileRepository.deleteByUser(user);
        return "Profile deleted Successfully !";
    }

   

    private FarmerProfileResponse mapResponse(FarmerProfile profile) {
        FarmerProfileResponse response = modelMapper.map(profile, FarmerProfileResponse.class);
        response.setUserId(profile.getUser().getUserId());
        response.setEmail(profile.getUser().getEmail());
        response.setUsername(profile.getUser().getUsername());
        return response;
    }

}
