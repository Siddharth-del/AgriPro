package com.SmartAgriculture.Cropp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SmartAgriculture.Cropp.dtos.WeatherResponse;
import com.SmartAgriculture.Cropp.service.WeatherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;
   

    @GetMapping("/weather/city/{city}")
    public ResponseEntity<WeatherResponse> getWeatherCity(@PathVariable String city) {
        WeatherResponse response = weatherService.getWeatherCity(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

  
}