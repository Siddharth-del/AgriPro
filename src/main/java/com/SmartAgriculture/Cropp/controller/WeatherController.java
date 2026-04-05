package com.SmartAgriculture.Cropp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SmartAgriculture.Cropp.Service.IrrigationService;
import com.SmartAgriculture.Cropp.Service.WeatherService;
import com.SmartAgriculture.Cropp.dtos.IrrigationDTO;
import com.SmartAgriculture.Cropp.dtos.WeatherResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;
    private final IrrigationService irrigationService;

    @GetMapping("/weather/city/{city}")
    public ResponseEntity<WeatherResponse> getWeatherCity(@PathVariable String city) {
        WeatherResponse response = weatherService.getWeatherCity(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/alert/city/{city}")
    public ResponseEntity<String> getAlert(
            @Valid @RequestBody IrrigationDTO irrigationDTO,
            @PathVariable String city) {

        log.info("Received → city: {}, soilMoisture: {}%",
                city, irrigationDTO.getSoilMoisture());

        String response = irrigationService.getIrrigationAlert(irrigationDTO, city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}