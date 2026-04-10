package com.SmartAgriculture.Cropp.dtos.sensor;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SensorAutoRequest {

    private String deviceId;
    private Double soilMoisture;
    private String city;
    private String username;
}
