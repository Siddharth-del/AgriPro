package com.SmartAgriculture.Cropp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
 public class FarmerProfileResponse{
    private Long profileId;
    private String fullname;
    private String contactNumber;
    private String address;
    private String village;
    private String district;
    private String state;
    private String pincode;
    private String farmLocation;

    private Long userId;
    private String username;
    private String email;
 }