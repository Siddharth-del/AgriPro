package com.SmartAgriculture.Cropp.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseDetectionResponse {
    private Long diseaseId;
    private List<DiseasePredictionResponse> predicted;
    private String fertilizerSuggestion;
    private String pesticideSuggestion;

    private String explanation;

    private LocalDateTime createdAt;
}
