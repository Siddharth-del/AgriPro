package com.SmartAgriculture.Cropp.controller;

import com.SmartAgriculture.Cropp.dtos.sensor.SoilDataRequest;
import com.SmartAgriculture.Cropp.service.alert.AlertServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
public class AlertController {

    private final AlertServiceImpl alertService;

    @PostMapping("/city/{city}")
    public ResponseEntity<String> sendAlert(
            @PathVariable String city,
            @Valid @RequestBody SoilDataRequest request) {
        String result = alertService.processAlert(city, request.getSoilMoisture());
        return ResponseEntity.ok(result);
    }
}