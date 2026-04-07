package com.SmartAgriculture.Cropp.dtos.sensor;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SensorAutoRequest {

    @NotBlank(message = "city is required")
    private String city;

    @NotNull(message = "soilMoisture is required")
    @DecimalMin(value = "0.0", message = "Must be >= 0")
    @DecimalMax(value = "100.0", message = "Must be <= 100")
    private Double soilMoisture;

    private String deviceId;
}
