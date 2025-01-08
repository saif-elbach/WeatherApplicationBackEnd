package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.service.WeatherForecastService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weather")
public class WeatherForecastController {

    private final WeatherForecastService weatherForecastService;

    public WeatherForecastController(WeatherForecastService weatherForecastService) {
        this.weatherForecastService = weatherForecastService;
    }

    @GetMapping("/forecast")
    public List<Map<String, Object>> getWeatherForecastForDay(@RequestParam String day) {
        List<Map<String, Object>> weatherForecasts = weatherForecastService.getWeatherForecastForAllStations(day);

        if (weatherForecasts.isEmpty()) {
            return List.of(Map.of("message", "No weather data found for the given day."));
        }

        return weatherForecasts;
    }
}
