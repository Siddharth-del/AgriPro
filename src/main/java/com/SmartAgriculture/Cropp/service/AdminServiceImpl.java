package com.SmartAgriculture.Cropp.service;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.SmartAgriculture.Cropp.dtos.FarmerProfileResponse;
import com.SmartAgriculture.Cropp.exception.ResourceNotFoundException;
import com.SmartAgriculture.Cropp.model.FarmerProfile;
import com.SmartAgriculture.Cropp.repository.FarmerProfileRepository;
import com.SmartAgriculture.Cropp.repository.UserRepository;
import com.SmartAgriculture.Cropp.utils.AuthUtil;

import io.jsonwebtoken.lang.Collections;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminServiceImpl implements AdminService {
    private final FarmerProfileRepository farmerProfileRepository;
    private final AuthUtil authUtils;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public String deleteFarmer(Long userId) {
        if (!farmerProfileRepository.existsByUser_UserId(userId)) {
            throw new ResourceNotFoundException("Farmer", "User", userId);
        }

        //farmerProfileRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
        return "Profile deleted Successfully !";
    }

    @Override
    public List<FarmerProfileResponse> getAllFarmers() {
        List<FarmerProfile> farmers = farmerProfileRepository.findAll();
        if (farmers.isEmpty()) {
           return Collections.emptyList();
        }
        List<FarmerProfileResponse> response = farmers.stream().map(this::mapToResponse).toList();
        return response;
    }

    @Override
    public FarmerProfileResponse getFarmerById(Long userId) {
        FarmerProfile profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("farmer", "user", userId));

        return mapToResponse(profile);
    }

    private FarmerProfileResponse mapToResponse(FarmerProfile profile) {
        FarmerProfileResponse response = modelMapper.map(profile, FarmerProfileResponse.class);
        response.setUserId(profile.getUser().getUserId());
        response.setEmail(profile.getUser().getEmail());
        response.setUsername(profile.getUser().getUsername());
        return response;
    }

}
