package com.SmartAgriculture.Cropp.dtos.sensor;

import lombok.Data;

@Data
public class DeviceCredentialRequest {
    private String deviceId;
    private String password;
}