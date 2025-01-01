package com.weatherAPI.weatherApplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class RecommendationControllerTest {

    private RecommendationController recommendationController;

    @BeforeEach
    void setUp() {
        recommendationController = new RecommendationController();
    }

    @Test
    void getBikeWeatherRecommendations_nonnull() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        weatherData.add(Map.of("DistrictName", "Bolzano", "Latitude", 46.498, "Longitude", 11.354));

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        bikeStations.add(Map.of("stationName", "Station1", "availableBikes", 10, "latitude", 46.498, "longitude", 11.354));

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertTrue(response.containsKey("bikeStationsWithDistances"));
    }

    @Test
    void getBikeWeatherRecommendations_noBikeStations() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        weatherData.add(Map.of("DistrictName", "Bolzano", "Latitude", 46.498, "Longitude", 11.354));

        List<Map<String, Object>> bikeStations = new ArrayList<>();  

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertFalse(((List<?>) response.get("weatherData")).isEmpty());  
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertTrue(((List<?>) response.get("bikeStationsWithDistances")).isEmpty()); 
    }

    @Test
    void getBikeWeatherRecommendations_noWeatherStations() {
        List<Map<String, Object>> weatherData = new ArrayList<>();  

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        bikeStations.add(Map.of("stationName", "Station1", "availableBikes", 10, "latitude", 46.498, "longitude", 11.354));

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertTrue(((List<?>) response.get("weatherData")).isEmpty());  
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertFalse(((List<?>) response.get("bikeStationsWithDistances")).isEmpty());  
    }

    @Test
    void getBikeWeatherRecommendations_noWeatherAndNoBikeStations() {
        List<Map<String, Object>> weatherData = new ArrayList<>();  
        List<Map<String, Object>> bikeStations = new ArrayList<>();  

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertTrue(((List<?>) response.get("weatherData")).isEmpty());  
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertTrue(((List<?>) response.get("bikeStationsWithDistances")).isEmpty());  
    }
    
    @Test
    void getBikeWeatherRecommendations_noAvailableBikes() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        weatherData.add(Map.of("DistrictName", "Bolzano", "Latitude", 46.498, "Longitude", 11.354));

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        bikeStations.add(Map.of("stationName", "Station1", "availableBikes", 0, "latitude", 46.498, "longitude", 11.354));

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertFalse(((List<?>) response.get("weatherData")).isEmpty());  
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertFalse(((List<?>) response.get("bikeStationsWithDistances")).isEmpty());  
        assertEquals(0, ((Map<?, ?>) ((List<?>) response.get("bikeStationsWithDistances")).get(0)).get("availableBikes"));  
    }

    @Test
    void getBikeWeatherRecommendations_multipleWeatherStations() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        weatherData.add(Map.of("DistrictName", "Bolzano", "Latitude", 46.498, "Longitude", 11.354));
        weatherData.add(Map.of("DistrictName", "Meran", "Latitude", 46.67, "Longitude", 11.162));

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        bikeStations.add(Map.of("stationName", "Station1", "availableBikes", 10, "latitude", 46.498, "longitude", 11.354));
        bikeStations.add(Map.of("stationName", "Station2", "availableBikes", 5, "latitude", 46.67, "longitude", 11.162));

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertFalse(((List<?>) response.get("weatherData")).isEmpty());  
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertFalse(((List<?>) response.get("bikeStationsWithDistances")).isEmpty());  
        assertEquals(2, ((List<?>) response.get("weatherData")).size());  
        assertEquals(2, ((List<?>) response.get("bikeStationsWithDistances")).size());  
    }

    private Map<String, Object> getBikeWeatherRecommendations(List<Map<String, Object>> weatherData, List<Map<String, Object>> bikeStations) {
        List<Map<String, Object>> bikeStationsWithDistances = new ArrayList<>();
        for (Map<String, Object> bikeStation : bikeStations) {
            bikeStationsWithDistances.add(Map.of(
                "stationName", bikeStation.get("stationName"),
                "availableBikes", bikeStation.get("availableBikes"),
                "distancesToWeatherStations", weatherData
            ));
        }

        return Map.of(
            "weatherData", weatherData,
            "bikeStationsWithDistances", bikeStationsWithDistances
        );
    }
}
