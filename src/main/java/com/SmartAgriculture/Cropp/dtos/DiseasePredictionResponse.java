package com.SmartAgriculture.Cropp.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiseasePredictionResponse {
     private String diseaseName;
    private Double confidence;
}
