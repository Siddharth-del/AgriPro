package com.SmartAgriculture.Cropp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device_credentials")
@Data
@NoArgsConstructor
public class DeviceCredential {

    @Id
    private String deviceId;

    private String username;

    private String password;
}