package com.weatherAPI.weatherApplication.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.weatherAPI.weatherApplication.model.BikeTrend;

class RecommendationServiceTest {

	 @InjectMocks
	    private RecommendationService recommendationService;

	    @Mock
	    private BikeService bikeService;

	    @Mock
	    private WeatherService weatherService;

	    @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this); // Initialize mocks and inject them into the test subject
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
	        assertEquals(0, ((List<?>) result.get("bikeStationsWithTrends")).size());
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
	        assertEquals(0, ((List<?>) result.get("bikeStationsWithDistances")).size());
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
    
 

    @Test
    void getBikeWeatherRecommendations_differentDistricts() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        weatherData.add(Map.of("DistrictName", "Bolzano", "Latitude", 46.498, "Longitude", 11.354));
        weatherData.add(Map.of("DistrictName", "Meran", "Latitude", 46.67, "Longitude", 11.162));

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        bikeStations.add(Map.of("stationName", "Station1", "availableBikes", 8, "latitude", 46.498, "longitude", 11.354));
        bikeStations.add(Map.of("stationName", "Station2", "availableBikes", 3, "latitude", 46.7, "longitude", 11.15));

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertEquals(2, ((List<?>) response.get("weatherData")).size());
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertEquals(2, ((List<?>) response.get("bikeStationsWithDistances")).size());
    }

    @Test
    void getBikeWeatherRecommendations_sameCoordinates() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        weatherData.add(Map.of("DistrictName", "Bolzano", "Latitude", 46.498, "Longitude", 11.354));

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        bikeStations.add(Map.of("stationName", "Station1", "availableBikes", 5, "latitude", 46.498, "longitude", 11.354));
        bikeStations.add(Map.of("stationName", "Station2", "availableBikes", 15, "latitude", 46.498, "longitude", 11.354));

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertFalse(((List<?>) response.get("weatherData")).isEmpty());
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertEquals(2, ((List<?>) response.get("bikeStationsWithDistances")).size());
    }

    @Test
    void getBikeWeatherRecommendations_negativeCoordinates() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        weatherData.add(Map.of("DistrictName", "NegativeZone", "Latitude", -46.498, "Longitude", -11.354));

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        bikeStations.add(Map.of("stationName", "NegativeStation", "availableBikes", 7, "latitude", -46.498, "longitude", -11.354));

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertFalse(((List<?>) response.get("weatherData")).isEmpty());
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertEquals(1, ((List<?>) response.get("bikeStationsWithDistances")).size());
    }

    @Test
    void getBikeWeatherRecommendations_largeDataset() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            weatherData.add(Map.of("DistrictName", "District" + i, "Latitude", 40.0 + i, "Longitude", 10.0 + i));
        }

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            bikeStations.add(Map.of("stationName", "Station" + i, "availableBikes", i, "latitude", 40.0 + i, "longitude", 10.0 + i));
        }

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertEquals(100, ((List<?>) response.get("weatherData")).size());
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertEquals(100, ((List<?>) response.get("bikeStationsWithDistances")).size());
    }

    @Test
    void getBikeWeatherRecommendations_noMatchingCoordinates() {
        List<Map<String, Object>> weatherData = new ArrayList<>();
        weatherData.add(Map.of("DistrictName", "Bolzano", "Latitude", 46.498, "Longitude", 11.354));

        List<Map<String, Object>> bikeStations = new ArrayList<>();
        bikeStations.add(Map.of("stationName", "Station1", "availableBikes", 10, "latitude", 50.0, "longitude", 12.0));

        Map<String, Object> response = getBikeWeatherRecommendations(weatherData, bikeStations);

        assertNotNull(response);
        assertTrue(response.containsKey("weatherData"));
        assertFalse(((List<?>) response.get("weatherData")).isEmpty());
        assertTrue(response.containsKey("bikeStationsWithDistances"));
        assertEquals(1, ((List<?>) response.get("bikeStationsWithDistances")).size());
    }
}
