package com.weatherAPI.weatherApplication.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class WeatherForecastService {

    private final RestTemplate restTemplate;

    // Constructor to inject RestTemplate
    public WeatherForecastService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getWeatherForecastForAllStations(String day) {
        List<String> stations = List.of("Bolzano", "Meran", "Vinschgau", "Eisacktal", "Pustertal", "Wipptal");

        return stations.stream()
                .map(station -> fetchWeatherForStation(station, day))
                .filter(Objects::nonNull)
                .map(this::filterRelevantWeatherData)
                .collect(Collectors.toList());
    }

    private Map<String, Object> fetchWeatherForStation(String stationName, String day) {
        String url = "https://tourism.api.opendatahub.com/v1/Weather/District?districtName" + stationName;
        List<Map<String, Object>> weatherDataList = restTemplate.getForObject(url, List.class);

        if (weatherDataList == null || weatherDataList.isEmpty()) {
            return null;
        }

        Map<String, Object> weatherData = weatherDataList.get(0);
        List<Map<String, Object>> forecasts = (List<Map<String, Object>>) weatherData.get("BezirksForecast");

        Map<String, Object> selectedForecast = forecasts.stream()
                .filter(forecast -> forecast.get("date").equals(day))
                .findFirst()
                .orElse(null);

        if (selectedForecast != null) {
            selectedForecast.put("stationName", stationName);
            selectedForecast.put("requestedDay", day);
        }

        return selectedForecast;
    }

    private Map<String, Object> filterRelevantWeatherData(Map<String, Object> forecastData) {
        if (forecastData != null) {
            Map<String, Object> filteredData = new HashMap<>();
            filteredData.put("WeatherDesc", forecastData.get("WeatherDesc"));
            filteredData.put("MaxTemp", forecastData.get("MaxTemp"));
            filteredData.put("MinTemp", forecastData.get("MinTemp"));
            filteredData.put("Freeze", forecastData.get("Freeze"));
            filteredData.put("RainFrom", forecastData.get("RainFrom"));
            filteredData.put("RainTo", forecastData.get("RainTo"));
            filteredData.put("Thunderstorm", forecastData.get("Thunderstorm"));
            filteredData.put("stationName", forecastData.get("stationName"));
            filteredData.put("requestedDay", forecastData.get("requestedDay"));
            return filteredData;
        }
        return null;
    }
}
