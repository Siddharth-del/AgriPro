package com.SmartAgriculture.Cropp.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "crop_recommendations")
public class CropRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cropId;
    private String cropName;
    private Double confidenceScore;
    @Column(columnDefinition = "TEXT")
    private String explanation;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_id", nullable = true)
    private SensorData sensorData;
    


     

    // Add
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name="user_id",nullable = false)
    // private User user;
}
