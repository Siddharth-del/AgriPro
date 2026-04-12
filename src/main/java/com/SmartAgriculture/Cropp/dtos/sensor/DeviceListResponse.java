package com.SmartAgriculture.Cropp.dtos.sensor;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeviceListResponse {
    private Long    id;
    private String  deviceId;
    private String  deviceLabel;
    private String  city;
    private boolean active;
    private String  registeredAt;
    private String  lastSeenAt;

    // Latest reading summary — shown in device list card
    private Double  lastMoisture;
    private String  lastStatus;
    private String  lastRecordedAt;
}