package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String API_URL = "https://tourism.api.opendatahub.com/v1/Weather/District";

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherData[] getWeatherData() {
        return restTemplate.getForObject(API_URL, WeatherData[].class);
    }
}
