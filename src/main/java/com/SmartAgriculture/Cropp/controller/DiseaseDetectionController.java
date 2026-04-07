package com.SmartAgriculture.Cropp.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.SmartAgriculture.Cropp.dtos.disease.DiseaseDetectionResponse;
import com.SmartAgriculture.Cropp.service.disease.DiseaseDetectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DiseaseDetectionController {

    private final DiseaseDetectionService diseaseDetectionService;

    @PostMapping("/disease/detect")
    public ResponseEntity<DiseaseDetectionResponse> detectDisease(
            @RequestParam("image") MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        DiseaseDetectionResponse response = diseaseDetectionService.detectdisease(image);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disease/{diseaseName}")
    public ResponseEntity<List<DiseaseDetectionResponse>> getDiseaseByName(@PathVariable String diseaseName) {
        List<DiseaseDetectionResponse> Diseases = diseaseDetectionService.getDiseaseByName(diseaseName);
        return new ResponseEntity<>(Diseases, HttpStatus.OK);
    }
}