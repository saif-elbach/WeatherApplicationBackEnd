package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.WeatherData;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        // Mock API response
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setDistrictName("Test District");

        when(restTemplate.getForObject(
            "https://tourism.api.opendatahub.com/v1/Weather/District", WeatherData[].class
        )).thenReturn(new WeatherData[]{mockWeatherData});

        // Service call
        WeatherData[] weatherData = weatherService.getWeatherData();

        // Assertions
        assertEquals(1, weatherData.length);
        assertEquals("Test District", weatherData[0].getDistrictName());
    }
}