package com.SmartAgriculture.Cropp.dtos;

import java.util.List;

import lombok.Data;

@Data
public class WeatherResponse {
   
    private Main main;
    private Wind wind;
    private List<Weather> weather;
     private Coord coord;
    private Sys sys;
    private long dt;
    private String name;

    @Data
    public static class Main {
        private double temp;
        private int humidity;
        private int pressure;
    }

    @Data
    public static class Wind {
        private double speed;
    }

    @Data
    public static class Weather {
        private String main;
        private String description;
    }
    @Data
    public static class Coord{
        private double lon;
        private double lat;
    }
    @Data
    public static class Sys {
      private String country;
        
    }
}