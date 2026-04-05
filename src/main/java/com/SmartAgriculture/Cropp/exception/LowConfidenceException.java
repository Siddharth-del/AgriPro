package com.SmartAgriculture.Cropp.exception;

import lombok.Getter;

@Getter
public class LowConfidenceException extends RuntimeException {
    
    private final double confidence;
    private final String prediction;
    private final String suggestion;
    
    public LowConfidenceException(String message, double confidence, String prediction, String suggestion) {
        super(message);
        this.confidence = confidence;
        this.prediction = prediction;
        this.suggestion = suggestion;
    }
}