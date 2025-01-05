package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.BikeTrend;
import com.weatherAPI.weatherApplication.model.WeatherData;
import com.weatherAPI.weatherApplication.model.WeatherData.Forecast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private BikeService bikeService;

    @Mock
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

 


    @Test
    void testGetBikeWeatherRecommendations_NoBikeData() {
        List<Map<String, Object>> mockWeatherData = new ArrayList<>();
        Map<String, Object> weatherMap = new HashMap<>();
        weatherMap.put("DistrictName", "District1");
        weatherMap.put("AverageMaxTemp", 25.0);
        weatherMap.put("AverageMinTemp", 15.0);
        mockWeatherData.add(weatherMap);

        when(weatherService.getWeatherTrends()).thenReturn(mockWeatherData);
        when(bikeService.getTrends()).thenReturn(Collections.emptyList());

        Map<String, Object> result = recommendationService.getBikeWeatherRecommendations();
        assertEquals(0, ((List<?>) result.getOrDefault("data", Collections.emptyList())).size());
    }

    @Test
    void testGetBikeWeatherRecommendations_NoWeatherData() {
        when(weatherService.getWeatherTrends()).thenReturn(Collections.emptyList());

        List<BikeTrend> mockBikeTrends = new ArrayList<>();
        BikeTrend trend = new BikeTrend();
        trend.setStationName("Station1");
        trend.setAvailableBikes(5);
        trend.setLatitude(46.0);
        trend.setLongitude(11.0);
        mockBikeTrends.add(trend);

        when(bikeService.getTrends()).thenReturn(mockBikeTrends);

        Map<String, Object> result = recommendationService.getBikeWeatherRecommendations();
        assertEquals(0, ((List<?>) result.getOrDefault("data", Collections.emptyList())).size());
    }




    @Test
    void testGetBikeWeatherRecommendations_NoMatchingDistrictAndStation() {
        List<Map<String, Object>> mockWeatherData = new ArrayList<>();
        Map<String, Object> weatherMap = new HashMap<>();
        weatherMap.put("DistrictName", "District1");
        weatherMap.put("AverageMaxTemp", 22.0);
        weatherMap.put("AverageMinTemp", 12.0);
        mockWeatherData.add(weatherMap);

        List<BikeTrend> mockBikeTrends = new ArrayList<>();
        BikeTrend trend = new BikeTrend();
        trend.setStationName("District2");
        trend.setAvailableBikes(8);
        trend.setLatitude(46.0);
        trend.setLongitude(11.0);
        mockBikeTrends.add(trend);

        when(weatherService.getWeatherTrends()).thenReturn(mockWeatherData);
        when(bikeService.getTrends()).thenReturn(mockBikeTrends);

        Map<String, Object> result = recommendationService.getBikeWeatherRecommendations();
        assertEquals(0, ((List<?>) result.getOrDefault("data", Collections.emptyList())).size());
    }

   
}
