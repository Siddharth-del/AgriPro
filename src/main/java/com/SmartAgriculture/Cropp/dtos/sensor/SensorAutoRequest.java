package com.SmartAgriculture.Cropp.dtos.sensor;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SensorAutoRequest {

    @NotBlank(message = "city is required")
    private String city;

    @NotNull(message = "soilMoisture is required")
    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private Double soilMoisture;

    private String deviceId;

}