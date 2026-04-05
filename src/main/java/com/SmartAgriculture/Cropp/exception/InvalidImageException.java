package com.SmartAgriculture.Cropp.exception;

import lombok.Getter;

@Getter
public class InvalidImageException extends RuntimeException {
    
    private final String suggestion;
    
    public InvalidImageException(String message, String suggestion) {
        super(message);
        this.suggestion = suggestion;
    }
}