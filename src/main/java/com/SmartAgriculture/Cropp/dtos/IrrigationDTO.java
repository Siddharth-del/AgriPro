package com.SmartAgriculture.Cropp.dtos;

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
