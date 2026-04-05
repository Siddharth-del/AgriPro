package com.SmartAgriculture.Cropp.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.SmartAgriculture.Cropp.dtos.WeatherResponse;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String weatherApiKey;

    public WeatherService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public WeatherResponse getWeatherCity(String city) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("q", city + ",IN")   
                .queryParam("appid", weatherApiKey)
                .queryParam("units", "metric")
                .toUriString();

        try {
            ResponseEntity<WeatherResponse> response =
                    restTemplate.getForEntity(url, WeatherResponse.class);

            return response.getBody();

        } catch (HttpClientErrorException e) {
            throw new RuntimeException(
                    "Weather API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()
            );
        }
    }
}