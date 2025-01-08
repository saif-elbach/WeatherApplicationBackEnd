package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.service.RecommendationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Endpoint to get distances from a weather station to nearby bike stations.
     * 
     * Example: /api/weather-to-bike-distances?weatherStationName=Pustertal
     *
     * @param weatherStationName The name of the weather station.
     * @return Distances to nearby bike stations.
     */
    @GetMapping("/weather-to-bike-distances")
    public Map<String, Object> getDistancesToBikeStations(@RequestParam String weatherStationName) {
        return recommendationService.getDistancesToBikeStations(weatherStationName);
    }
}