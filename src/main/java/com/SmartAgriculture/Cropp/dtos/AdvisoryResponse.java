package com.SmartAgriculture.Cropp.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdvisoryResponse {

    private String fertilizerRecommendation;
    private String pesticideRecommendation;
    @Column(columnDefinition = "TEXT")
    private String explanation;

}
