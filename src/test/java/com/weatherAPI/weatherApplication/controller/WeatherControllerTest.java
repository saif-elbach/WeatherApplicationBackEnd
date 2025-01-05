package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.UserPreferences;
import com.weatherAPI.weatherApplication.model.WeatherData;
import com.weatherAPI.weatherApplication.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWeather_success() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        when(weatherService.getWeatherData()).thenReturn(new WeatherData[]{mockWeatherData});

        WeatherData[] response = weatherController.getWeather();

        assertEquals(1, response.length);
        assertEquals("Test District", response[0].getDistrictName());
    }

    @Test
    void getWeatherTrends_success() {
        List<Map<String, Object>> mockTrends = List.of(
            Map.of("trend", "Rising temperatures", "district", "Test District")
        );

        when(weatherService.getWeatherTrends()).thenReturn(mockTrends);

        List<Map<String, Object>> response = weatherController.getWeatherTrends();

        assertEquals(1, response.size());
        assertEquals("Rising temperatures", response.get(0).get("trend"));
        assertEquals("Test District", response.get(0).get("district"));
    }

    @Test
    void getRecommendations_success() {
        UserPreferences mockPreferences = new UserPreferences();
        mockPreferences.setPreferredWeather("Sunny");
        mockPreferences.setMinTemperature(10);
        mockPreferences.setMaxTemperature(30);

        List<Map<String, Object>> mockRecommendations = List.of(
            Map.of("district", "Test District", "recommendation", "Sunny and warm")
        );

        when(weatherService.getRecommendations(mockPreferences)).thenReturn(mockRecommendations);

        List<Map<String, Object>> response = weatherController.getRecommendations(mockPreferences);

        assertEquals(1, response.size());
        assertEquals("Test District", response.get(0).get("district"));
        assertEquals("Sunny and warm", response.get(0).get("recommendation"));
    }

    @Test
    void getRecommendations_invalidPreferences() {
        UserPreferences invalidPreferences = new UserPreferences();

        when(weatherService.getRecommendations(invalidPreferences))
            .thenThrow(new IllegalArgumentException("Invalid preferences"));

        assertThrows(IllegalArgumentException.class, () -> {
            weatherController.getRecommendations(invalidPreferences);
        });
    }

    @Test
    void getInstructions_success() {
        Map<String, Object> response = weatherController.getInstructions();

        assertEquals("Welcome to the Weather Recommendation API!", response.get("message"));
        assertEquals(
            Map.of("preferredWeather", "Sunny", "minTemperature", -5, "maxTemperature", 15),
            response.get("exampleInput")
        );
        assertEquals(
            List.of("Sunny", "Cloudy", "Partly Cloudy", "Very Cloudy"),
            response.get("acceptableWeatherOptions")
        );
    }

    @Test
    void getWeather_failure() {
        when(weatherService.getWeatherData()).thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class, () -> {
            weatherController.getWeather();
        });
    }
}
