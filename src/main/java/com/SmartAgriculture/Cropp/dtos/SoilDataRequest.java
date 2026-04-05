package com.SmartAgriculture.Cropp.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SoilDataRequest {

    @NotNull(message = "soilMoisture is required")
    @DecimalMin(value = "0.0", message = "Must be >= 0")
    @DecimalMax(value = "100.0", message = "Must be <= 100")
    private Double soilMoisture;
}
