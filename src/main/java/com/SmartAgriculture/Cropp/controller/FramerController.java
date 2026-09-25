package com.SmartAgriculture.Cropp.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SmartAgriculture.Cropp.dtos.FarmerProfileRequest;
import com.SmartAgriculture.Cropp.dtos.FarmerProfileResponse;
import com.SmartAgriculture.Cropp.service.FarmerProfileService;
import com.SmartAgriculture.Cropp.service.alert.Crop.CropRecommendationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/farmer")
@RequiredArgsConstructor
public class FramerController {

    private final CropRecommendationService cropRecommendationService;

    private final FarmerProfileService farmerProfileService;

    private final ChatClient chatClient;
    // public FramerController(ChatClient chatClient){
    // this.chatClient=chatClient;
    // }

    private static final String SYSTEM_PROMPT = """
            You are AgriPro, an AI-powered Agriculture Assistant designed to help farmers.

            ```
                Your role:
                - Answer questions related to agriculture, farming, crops, plants, soil, fertilizers,
                  irrigation, pests, plant diseases, weather conditions affecting crops, and farm management.
                - Provide accurate, practical, and easy-to-understand answers that are useful to farmers.
                - When discussing plant diseases or crop problems, explain the likely cause, symptoms,
                  prevention, and recommended treatment when appropriate.

                Important rules:
                1. Only answer questions that are directly related to agriculture, farming, crops,
                   plants, soil, or related agricultural problems.
                2. If a question is unrelated to agriculture, politely refuse and state that you can
                   only assist with agriculture-related questions.
                3. Do not make up facts, diagnoses, treatments, or recommendations.
                4. If the available information is insufficient to provide a reliable answer,
                   clearly state what additional information is needed.
                5. Prioritize accuracy, safety, and practical guidance for farmers.
                6. Keep responses clear, concise, and easy for farmers to understand.
                """;

    @PostMapping("/profile")
    public ResponseEntity<FarmerProfileResponse> createProfile(@Valid @RequestBody FarmerProfileRequest request) {
        FarmerProfileResponse response = farmerProfileService.createProfile(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<FarmerProfileResponse> getProfile() {
        FarmerProfileResponse profile = farmerProfileService.getProfile();
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<FarmerProfileResponse> updateProfile(@Valid @RequestBody FarmerProfileRequest request) {
        FarmerProfileResponse updatedProfile = farmerProfileService.updateProfile(request);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile() {
        String deletedProfile = farmerProfileService.deleteProfile();
        return new ResponseEntity<>(deletedProfile, HttpStatus.OK);
    }

    @PostMapping("/assistant")
    public ChatClientResponse FarmerAssistant(@RequestBody String message) {
        ChatClientResponse chatClientResponse = chatClient
                .prompt()
                .user(message)
                .call()
                .chatClientResponse();
        return chatClientResponse;
    }

}
