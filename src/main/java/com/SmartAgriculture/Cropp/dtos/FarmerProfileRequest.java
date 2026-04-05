package com.SmartAgriculture.Cropp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FarmerProfileRequest {
    @NotBlank(message = "FullName is required ")
    @Size(min = 3, max = 20, message = "Full name is must be Minimum 3 Characters")
    private String fullname;

    @NotBlank(message = "Contact Number is required")
    private String contactNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Village is required")
    private String village;

    @NotBlank(message = "Disrict is required")
    private String district;

    @NotBlank(message = "state is required")
    private String state;

    @NotBlank(message = "pincode is required")
    private String pincode;
    @NotBlank(message = "Farm Location is required")
    private String farmLocation;
}
