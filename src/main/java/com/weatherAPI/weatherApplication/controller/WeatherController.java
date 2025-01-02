package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.UserPreferences;
import com.weatherAPI.weatherApplication.model.WeatherData;
import com.weatherAPI.weatherApplication.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from the React app

public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather")
    public WeatherData[] getWeather() {
        return weatherService.getWeatherData();
    }

    @GetMapping("/weather-trends")
    public List<Map<String, Object>> getWeatherTrends() {
        return weatherService.getWeatherTrends();
    }

    @PostMapping("/recommendations")
    public List<Map<String, Object>> getRecommendations(@RequestBody UserPreferences userPreferences) {
        return weatherService.getRecommendations(userPreferences);
    }
    
    @GetMapping("/instructions")
    public Map<String, Object> getInstructions() {
        Map<String, Object> exampleInput = Map.of(
            "preferredWeather", "Sunny",
            "minTemperature", -5,
            "maxTemperature", 15
        );

        return Map.of(
            "message", "Welcome to the Weather Recommendation API!",
            "exampleInput", exampleInput,
            "note", "All fields are mandatory. Please provide 'preferredWeather', 'minTemperature', and 'maxTemperature'.",
            "acceptableWeatherOptions", List.of("Sunny", "Cloudy", "Partly Cloudy", "Very Cloudy")
        );
    }



}
