package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.WeatherData;
import com.weatherAPI.weatherApplication.model.UserPreferences;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    public WeatherServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWeatherData_success() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        WeatherData[] weatherData = weatherService.getWeatherData();

        assertEquals(1, weatherData.length);
        assertEquals("Test District", weatherData[0].getDistrictName());
    }

    @Test
    void getWeatherTrends_success() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");
        
        WeatherData.Forecast forecast1 = new WeatherData.Forecast();
        forecast1.setMaxTemp(25);
        forecast1.setMinTemp(15);
        forecast1.setWeatherDescription("Clear sky");

        WeatherData.Forecast forecast2 = new WeatherData.Forecast();
        forecast2.setMaxTemp(28);
        forecast2.setMinTemp(18);
        forecast2.setWeatherDescription("Partly cloudy");

        mockWeatherData.setForecast(List.of(forecast1, forecast2));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        List<Map<String, Object>> trends = weatherService.getWeatherTrends();

        assertEquals(1, trends.size());
        assertEquals("Test District", trends.get(0).get("DistrictName"));
        assertEquals(26.5, trends.get(0).get("AverageMaxTemp")); 
        assertEquals(16.5, trends.get(0).get("AverageMinTemp"));
    }

    @Test
    void filterWeatherTrends_success() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        WeatherData.Forecast forecast1 = new WeatherData.Forecast();
        forecast1.setMaxTemp(25);
        forecast1.setMinTemp(15);
        
        WeatherData.Forecast forecast2 = new WeatherData.Forecast();
        forecast2.setMaxTemp(28);
        forecast2.setMinTemp(18);

        mockWeatherData.setForecast(List.of(forecast1, forecast2));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        UserPreferences preferences = new UserPreferences();
        preferences.setMinTemperature(16);
        preferences.setMaxTemperature(27);

        List<Map<String, Object>> filteredTrends = weatherService.filterWeatherTrends(preferences);

        assertEquals(1, filteredTrends.size());
        assertEquals("Test District", filteredTrends.get(0).get("DistrictName"));
        assertEquals(16.5, filteredTrends.get(0).get("AverageMinTemp"));
        assertEquals(26.5, filteredTrends.get(0).get("AverageMaxTemp"));
    }

    @Test
    void getRecommendations_success() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        WeatherData.Forecast forecast = new WeatherData.Forecast();
        forecast.setMaxTemp(25);
        forecast.setMinTemp(18);
        forecast.setWeatherDescription("Clear sky");

        mockWeatherData.setForecast(List.of(forecast));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        UserPreferences preferences = new UserPreferences();
        preferences.setPreferredWeather("Clear sky");
        preferences.setMinTemperature(15);
        preferences.setMaxTemperature(30);

        List<Map<String, Object>> recommendations = weatherService.getRecommendations(preferences);

        assertEquals(1, recommendations.size());
        assertEquals("Test District", recommendations.get(0).get("DistrictName"));
        assertEquals("Clear sky", recommendations.get(0).get("WeatherDesc"));
        assertEquals("18 to 25", recommendations.get(0).get("TemperatureRange"));
    }

    @Test
    void getRecommendations_noMatch() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        WeatherData.Forecast forecast = new WeatherData.Forecast();
        forecast.setMaxTemp(25);
        forecast.setMinTemp(18);
        forecast.setWeatherDescription("Cloudy");

        mockWeatherData.setForecast(List.of(forecast));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        UserPreferences preferences = new UserPreferences();
        preferences.setPreferredWeather("Clear sky");
        preferences.setMinTemperature(15);
        preferences.setMaxTemperature(30);

        List<Map<String, Object>> recommendations = weatherService.getRecommendations(preferences);

        assertEquals(1, recommendations.size());
        assertEquals("No matching recommendations found for your preferences.", recommendations.get(0).get("message"));
    }


    
    @Test
    void validatePreferences_invalid() {
        UserPreferences preferences = new UserPreferences();
        preferences.setMinTemperature(30);
        preferences.setMaxTemperature(25);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            weatherService.filterWeatherTrends(preferences);
        });

        assertEquals("Minimum temperature cannot be greater than maximum temperature.", exception.getMessage());
    }

    @Test
    void getWeatherData_emptyResponse() {
        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{});

        WeatherData[] weatherData = weatherService.getWeatherData();

        assertEquals(0, weatherData.length);
    }
    
   

    @Test
    void filterWeatherTrends_noResults() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        WeatherData.Forecast forecast1 = new WeatherData.Forecast();
        forecast1.setMaxTemp(32);
        forecast1.setMinTemp(22);

        WeatherData.Forecast forecast2 = new WeatherData.Forecast();
        forecast2.setMaxTemp(35);
        forecast2.setMinTemp(25);

        mockWeatherData.setForecast(List.of(forecast1, forecast2));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        UserPreferences preferences = new UserPreferences();
        preferences.setMinTemperature(20);
        preferences.setMaxTemperature(30);

        List<Map<String, Object>> filteredTrends = weatherService.filterWeatherTrends(preferences);

        assertEquals(0, filteredTrends.size());
    }

    @Test
    void getRecommendations_withBoundaryTemperature() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        WeatherData.Forecast forecast = new WeatherData.Forecast();
        forecast.setMaxTemp(30);
        forecast.setMinTemp(20);
        forecast.setWeatherDescription("Clear sky");

        mockWeatherData.setForecast(List.of(forecast));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        UserPreferences preferences = new UserPreferences();
        preferences.setPreferredWeather("Clear sky");
        preferences.setMinTemperature(20);
        preferences.setMaxTemperature(30);

        List<Map<String, Object>> recommendations = weatherService.getRecommendations(preferences);

        assertEquals(1, recommendations.size());
        assertEquals("Test District", recommendations.get(0).get("DistrictName"));
        assertEquals("Clear sky", recommendations.get(0).get("WeatherDesc"));
        assertEquals("20 to 30", recommendations.get(0).get("TemperatureRange"));
    }

    @Test
    void getWeatherData_multipleDistricts() {
        WeatherData mockWeatherData1 = new WeatherData();
        mockWeatherData1.setDistrictName("District 1");

        WeatherData mockWeatherData2 = new WeatherData();
        mockWeatherData2.setDistrictName("District 2");

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData1, mockWeatherData2});

        WeatherData[] weatherData = weatherService.getWeatherData();

        assertEquals(2, weatherData.length);
        assertEquals("District 1", weatherData[0].getDistrictName());
        assertEquals("District 2", weatherData[1].getDistrictName());
    }
    

    @Test
    void getRecommendations_weatherMatchDifferentDay() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        WeatherData.Forecast forecast1 = new WeatherData.Forecast();
        forecast1.setMaxTemp(20);
        forecast1.setMinTemp(10);
        forecast1.setWeatherDescription("Clear sky");

        WeatherData.Forecast forecast2 = new WeatherData.Forecast();
        forecast2.setMaxTemp(25);
        forecast2.setMinTemp(18);
        forecast2.setWeatherDescription("Partly cloudy");

        mockWeatherData.setForecast(List.of(forecast1, forecast2));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        UserPreferences preferences = new UserPreferences();
        preferences.setPreferredWeather("Partly cloudy");
        preferences.setMinTemperature(15);
        preferences.setMaxTemperature(30);

        List<Map<String, Object>> recommendations = weatherService.getRecommendations(preferences);

        assertEquals(1, recommendations.size());
        assertEquals("Test District", recommendations.get(0).get("DistrictName"));
        assertEquals("Partly cloudy", recommendations.get(0).get("WeatherDesc"));
        assertEquals("18 to 25", recommendations.get(0).get("TemperatureRange"));
        assertEquals("1", recommendations.get(0).get("Day"));
    }

    @Test
    void getRecommendations_preferredWeatherNoMatch() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        WeatherData.Forecast forecast = new WeatherData.Forecast();
        forecast.setMaxTemp(22);
        forecast.setMinTemp(12);
        forecast.setWeatherDescription("Cloudy");

        mockWeatherData.setForecast(List.of(forecast));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        UserPreferences preferences = new UserPreferences();
        preferences.setPreferredWeather("Sunny");
        preferences.setMinTemperature(10);
        preferences.setMaxTemperature(30);

        List<Map<String, Object>> recommendations = weatherService.getRecommendations(preferences);

        assertEquals(1, recommendations.size());
        assertEquals("No matching recommendations found for your preferences.", recommendations.get(0).get("message"));
    }

    

    @Test
    void filterWeatherTrends_minTempExceeded() {
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        WeatherData.Forecast forecast1 = new WeatherData.Forecast();
        forecast1.setMaxTemp(25);
        forecast1.setMinTemp(15);
        
        WeatherData.Forecast forecast2 = new WeatherData.Forecast();
        forecast2.setMaxTemp(28);
        forecast2.setMinTemp(18);

        mockWeatherData.setForecast(List.of(forecast1, forecast2));

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        UserPreferences preferences = new UserPreferences();
        preferences.setMinTemperature(19);
        preferences.setMaxTemperature(30);

        List<Map<String, Object>> filteredTrends = weatherService.filterWeatherTrends(preferences);

        assertEquals(0, filteredTrends.size());
    }
}
