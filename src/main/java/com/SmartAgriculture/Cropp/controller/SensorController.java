package com.SmartAgriculture.Cropp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.SmartAgriculture.Cropp.dtos.sensor.SensorAutoRequest;
import com.SmartAgriculture.Cropp.dtos.sensor.SensorLatestResponse;
import com.SmartAgriculture.Cropp.model.User;
import com.SmartAgriculture.Cropp.repository.UserRepository;
import com.SmartAgriculture.Cropp.service.sensor.SensorService;

@Slf4j
@RestController
@RequestMapping("/api/sensor")
@RequiredArgsConstructor
public class SensorController {

    @Value("${iot.device.api.key}")
    private String validDeviceKey;

    private final SensorService sensorService;
    private final UserRepository userRepository;

    @PostMapping("/data")
    public ResponseEntity<String> receiveData(
            @RequestHeader(value = "X-Device-Api-Key", required = false) String deviceKey,
            @Valid @RequestBody SensorAutoRequest request) {

        if (deviceKey == null || deviceKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing device API key");
        }

        // Key validation now happens inside service (user lookup by key)
        log.info("ESP32 data: city={}, moisture={}%, device={}",
                request.getCity(), request.getSoilMoisture(), request.getDeviceId());

        String result = sensorService.processAndAlert(request, deviceKey); // <-- pass key
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/latest")
    public ResponseEntity<SensorLatestResponse> getLatest(
            @AuthenticationPrincipal UserDetails userDetails) { // <-- added
        return ResponseEntity.ok(sensorService.getLatestReading(userDetails.getUsername()));
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetSoilMoisture() {
        sensorService.resetSoilMoisture();
        return new ResponseEntity<>("Soil moisture reset successfully", HttpStatus.OK);
    }

   // @PreAuthorize("isAuthenticated()")
    @PostMapping("/register-key")
    public ResponseEntity<String> registerDeviceKey(
            @RequestParam String deviceKey,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check key not already taken by someone else
        boolean taken = userRepository.findByDeviceApiKey(deviceKey)
                .filter(u -> !u.getUserId().equals(user.getUserId()))
                .isPresent();

        if (taken) {
            return ResponseEntity.badRequest().body("Device key already registered");
        }

        user.setDeviceApiKey(deviceKey);
        userRepository.save(user);
        return ResponseEntity.ok("Device key registered: " + deviceKey);
    }
}