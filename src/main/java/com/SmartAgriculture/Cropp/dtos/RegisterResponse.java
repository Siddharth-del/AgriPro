package com.SmartAgriculture.Cropp.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterResponse {
    private Long userId;
    private String username;
    private String email;
    private String password;
    private String role;
    private LocalDateTime createdAt;
}
