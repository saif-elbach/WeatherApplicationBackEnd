package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.WeatherData;
import com.weatherAPI.weatherApplication.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    public WeatherControllerTest() {
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
}
