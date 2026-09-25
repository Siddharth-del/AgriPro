package com.SmartAgriculture.Cropp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SmartAgriculture.Cropp.dtos.FarmerProfileResponse;
import com.SmartAgriculture.Cropp.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    
    private final AdminService adminService;
    
   @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/farmers")
    public ResponseEntity<List<FarmerProfileResponse>> getAllFarmers(){
        List<FarmerProfileResponse> response=adminService.getAllFarmers();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/farmers/{userId}")
    public ResponseEntity<FarmerProfileResponse> getFarmerById(@PathVariable Long userId){
        FarmerProfileResponse response=adminService.getFarmerById(userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @DeleteMapping("/farmers/{userId}")
    public ResponseEntity<String>  deleteFarmer(@PathVariable Long userId){
        String response=adminService.deleteFarmer(userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
