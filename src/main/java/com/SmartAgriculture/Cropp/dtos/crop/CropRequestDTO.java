package com.SmartAgriculture.Cropp.dtos.crop;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CropRequestDTO {

    // @NotNull
    // @DecimalMin("0.0")
    // @DecimalMax("60.0")
    // private Double temperature;

    // @NotNull
    // @DecimalMin("0.0")
    // @DecimalMax("100.0")
    // private Double humidity;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("2000.0")
    private Double rainfall;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("14.0")
    private Double ph;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("300.0")
    private Double nitrogen;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("300.0")
    private Double phosphorus;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("300.0")
    private Double potassium;

    @Size(max = 100)
    private String location;

    // private String lang;
}
