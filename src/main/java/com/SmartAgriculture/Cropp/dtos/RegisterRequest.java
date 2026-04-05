package com.SmartAgriculture.Cropp.dtos;

import java.time.LocalDateTime;

import com.SmartAgriculture.Cropp.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {
    @NotBlank(message = "user is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

   @NotBlank(message = "Role is required")
    private Role role;

    private LocalDateTime createdAt;
}
