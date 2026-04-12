package com.SmartAgriculture.Cropp.dtos.sensor;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SensorLatestResponse {
    private Double        soilMoisture;
    private Double        temperature;
    private Double        humidity;
    private String        city;
    private String        status;
    private String        emailStatus;
    private LocalDateTime recordedAt;
    private String        deviceId;
}
