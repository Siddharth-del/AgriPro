package com.SmartAgriculture.Cropp.controller;

import com.SmartAgriculture.Cropp.dtos.sensor.DeviceCredentialRequest;
import com.SmartAgriculture.Cropp.model.DeviceCredential;
import com.SmartAgriculture.Cropp.repository.DeviceCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceCredentialController {

    private final DeviceCredentialRepository repo;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody DeviceCredentialRequest req,
            @AuthenticationPrincipal UserDetails user) {

        DeviceCredential dc = new DeviceCredential();
        dc.setDeviceId(req.getDeviceId());
        dc.setUsername(user.getUsername()); // secure source
        dc.setPassword(req.getPassword());

        repo.save(dc);

        return ResponseEntity.ok("Device registered");
    }

    @GetMapping("/credentials")
    public ResponseEntity<?> getCredentials(@RequestParam String deviceId) {

        return repo.findById(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}