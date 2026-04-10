package com.SmartAgriculture.Cropp.dtos.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    @NotBlank(message = "Role is required")
    private String role; // e.g. "ROLE_FARMER", "ROLE_ADMIN"
}
