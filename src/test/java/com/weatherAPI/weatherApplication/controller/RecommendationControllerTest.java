package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.service.BikeService;
import com.weatherAPI.weatherApplication.service.WeatherService;
import com.weatherAPI.weatherApplication.model.BikeTrend;
import com.weatherAPI.weatherApplication.model.WeatherData;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RecommendationControllerTest {

    @Mock
    private BikeService bikeService;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private RecommendationController recommendationController;

    public RecommendationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // Test 1: Validate the behavior when weather data and bike stations are available
    @Test
    void getBikeWeatherRecommendations_withValidData() {
        LocalDateTime now = LocalDateTime.now();
        List<BikeTrend> mockBikeTrends = List.of(
            new BikeTrend("Station1", 10, now, 46.498, 11.354),
            new BikeTrend("Station2", 5, now, 46.67, 11.162)
        );

        List<Map<String, Object>> mockBikeStations = List.of(
            Map.of("stationName", "Station1", "availableBikes", 10, "latitude", 46.498, "longitude", 11.354),
            Map.of("stationName", "Station2", "availableBikes", 5, "latitude", 46.67, "longitude", 11.162)
        );

        WeatherData[] mockWeatherData = new WeatherData[]{
            new WeatherData("Bolzano", List.of(new WeatherData.Forecast("Clear", 25, 15))),
            new WeatherData("Meran", List.of(new WeatherData.Forecast("Sunny", 28, 18)))
        };

        when(bikeService.getTrends()).thenReturn(mockBikeTrends);
        when(bikeService.getBikeStations()).thenReturn(mockBikeStations);
        when(weatherService.getWeatherData()).thenReturn(mockWeatherData);

        Map<String, Object> response = recommendationController.getBikeWeatherRecommendations();

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertEquals(2, ((List<?>) response.get("weatherData")).size());
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertTrue(response.containsKey("bikeStationsWithTrends"));
    }

    // Test 2: Validate the behavior when weather data is empty
    @Test
    void getBikeWeatherRecommendations_withEmptyWeatherData() {
        WeatherData[] mockWeatherData = new WeatherData[] {};  // Empty array for weather data

        LocalDateTime now = LocalDateTime.now();
        List<BikeTrend> mockBikeTrends = List.of(
            new BikeTrend("Station1", 10, now, 46.498, 11.354)
        );

        List<Map<String, Object>> mockBikeStations = List.of(
            Map.of("stationName", "Station1", "availableBikes", 10, "latitude", 46.498, "longitude", 11.354)
        );

        when(bikeService.getTrends()).thenReturn(mockBikeTrends);
        when(bikeService.getBikeStations()).thenReturn(mockBikeStations);
        when(weatherService.getWeatherData()).thenReturn(mockWeatherData);

        Map<String, Object> response = recommendationController.getBikeWeatherRecommendations();

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertTrue(((List<?>) response.get("weatherData")).isEmpty());
    }

    // Test 3: Validate the behavior when requesting distances to bike stations from a weather station
    @Test
    void getDistancesToBikeStations_withValidData() {
        WeatherData[] mockWeatherData = new WeatherData[]{
            new WeatherData("Bolzano", List.of(new WeatherData.Forecast("Clear", 25, 15)))
        };

        List<Map<String, Object>> mockBikeStations = List.of(
            Map.of("stationName", "Station1", "availableBikes", 10, "latitude", 46.498, "longitude", 11.354),
            Map.of("stationName", "Station2", "availableBikes", 5, "latitude", 46.67, "longitude", 11.162)
        );

        when(bikeService.getBikeStations()).thenReturn(mockBikeStations);
        when(weatherService.getWeatherData()).thenReturn(mockWeatherData);

        Map<String, Object> response = recommendationController.getDistancesToBikeStations("Bolzano");

        assertNotNull(response);
        assertTrue(response.containsKey("weatherStationName"));
        assertEquals("Bolzano", response.get("weatherStationName"));
        assertTrue(response.containsKey("distancesToBikeStations"));
        assertEquals(2, ((List<?>) response.get("distancesToBikeStations")).size());
    }

    // Test 4: Handle case where a weather station is not found
    @Test
    void getDistancesToBikeStations_stationNotFound() {
        WeatherData[] mockWeatherData = new WeatherData[]{
            new WeatherData("Bolzano", List.of(new WeatherData.Forecast("Clear", 25, 15)))
        };

        List<Map<String, Object>> mockBikeStations = List.of(
            Map.of("stationName", "Station1", "availableBikes", 10, "latitude", 46.498, "longitude", 11.354)
        );

        when(bikeService.getBikeStations()).thenReturn(mockBikeStations);
        when(weatherService.getWeatherData()).thenReturn(mockWeatherData);

        Map<String, Object> response = recommendationController.getDistancesToBikeStations("NonExistingStation");

        assertNotNull(response);
        assertTrue(response.containsKey("error"));
        assertEquals("Weather station not found", response.get("error"));
    }

    // Test 5: Validate the behavior when no bike stations exist
    @Test
    void getBikeWeatherRecommendations_noBikeStations() {
        WeatherData[] mockWeatherData = new WeatherData[]{
            new WeatherData("Bolzano", List.of(new WeatherData.Forecast("Clear", 25, 15)))
        };

        List<BikeTrend> mockBikeTrends = List.of();
        List<Map<String, Object>> mockBikeStations = List.of();

        when(bikeService.getTrends()).thenReturn(mockBikeTrends);
        when(bikeService.getBikeStations()).thenReturn(mockBikeStations);
        when(weatherService.getWeatherData()).thenReturn(mockWeatherData);

        Map<String, Object> response = recommendationController.getBikeWeatherRecommendations();

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertTrue(((List<?>) response.get("weatherData")).isEmpty());
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertTrue(((List<?>) response.get("bikeStationsWithDistances")).isEmpty());
    }

}
