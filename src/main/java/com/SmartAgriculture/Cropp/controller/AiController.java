package com.SmartAgriculture.Cropp.controller;

import com.SmartAgriculture.Cropp.Service.Ai.AiAdvisoryService;
import com.SmartAgriculture.Cropp.dtos.AdvisoryResponse;
import com.SmartAgriculture.Cropp.model.SensorData;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAdvisoryService aiAdvisoryService;

    @PostMapping("/disease")
    public AdvisoryResponse diseaseAdvisory(@RequestParam String disease,@RequestParam String lang) {
        if("Hindi".equalsIgnoreCase(lang)){
            return aiAdvisoryService.generateHindiDiseaseAdvisory(disease);
        }
        return aiAdvisoryService.generateDiseaseAdvisory(disease);
    }

    @PostMapping("/crop")
    public AdvisoryResponse cropAdvisory(@RequestParam String crop,
                                         @RequestBody SensorData sensorData,@RequestParam String lang) {
                                            if("Hindi".equalsIgnoreCase(lang)){
                                                return aiAdvisoryService.generateHindiCropAdvisory(crop, sensorData);
                                            }
         else  return aiAdvisoryService.generateCropAdvisory(crop, sensorData);
    }
}
