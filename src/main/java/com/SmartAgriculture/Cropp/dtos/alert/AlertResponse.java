package com.SmartAgriculture.Cropp.dtos.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {
    private String message;
    private boolean irrigationRequired;
    private double soilMoisture;
    private double temperature;
    private double humidity;
    private String city;
     private String emailStatus;
}
