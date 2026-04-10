package com.SmartAgriculture.Cropp.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalFarmers;
    private long totalAdmins;
    private long totalCropRecommendations;
    private long totalDiseaseDetections;
    private long totalSensorReadings;
}
