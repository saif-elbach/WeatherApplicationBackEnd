package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/bike-weather-recommendations")
    public Map<String, Object> getBikeWeatherRecommendations() {
        return recommendationService.getBikeWeatherRecommendations();
    }

    @GetMapping("/weather-to-bike-distances")
    public Map<String, Object> getDistancesToBikeStations(@RequestParam String weatherStationName) {
        return recommendationService.getDistancesToBikeStations(weatherStationName);
    }
}
