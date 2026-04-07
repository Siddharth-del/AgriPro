package com.SmartAgriculture.Cropp.dtos.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class IrrigationDTO {
    @NotNull
    private Integer soilMoisture;
}
