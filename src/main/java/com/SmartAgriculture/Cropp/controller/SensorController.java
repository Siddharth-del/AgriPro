package com.SmartAgriculture.Cropp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.SmartAgriculture.Cropp.dtos.sensor.SensorAutoRequest;
import com.SmartAgriculture.Cropp.dtos.sensor.SensorLatestResponse;
import com.SmartAgriculture.Cropp.service.sensor.SensorService;

@Slf4j
@RestController
@RequestMapping("/api/sensor")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;
     @PreAuthorize("isAuthenticated()")
    @PostMapping("/data")
    public ResponseEntity<String> receiveData(@Valid @RequestBody SensorAutoRequest request) {
        log.info("ESP32 data: city={}, moisture={}%, device={}",
                request.getCity(), request.getSoilMoisture(), request.getDeviceId());
        return ResponseEntity.ok(sensorService.processAndAlert(request));
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorLatestResponse> getLatest() {
        return ResponseEntity.ok(sensorService.getLatestReading());
    }
}
