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

    // Method to get weather forecast for all stations for a specific day
    public List<Map<String, Object>> getWeatherForecastForAllStations(String day) {
        // List of station names (you can expand this list or fetch dynamically)
        List<String> stations = List.of("Bolzano", "Meran", "Vinschgau", "Eisacktal", "Pustertal", "Wipptal");

        // Fetch the weather data for each station and filter by the provided day
        return stations.stream()
                .map(station -> fetchWeatherForStation(station, day))
                .filter(Objects::nonNull)  // Make sure the forecast is not null
                .map(this::filterRelevantData) // Filter the relevant data
                .collect(Collectors.toList());
    }

    // Method to fetch weather data for a specific station and day
    private Map<String, Object> fetchWeatherForStation(String stationName, String day) {
        // Construct the API URL based on the station name
        String url = "https://tourism.api.opendatahub.com/v1/Weather/District?districtName=" + stationName;

        // Fetch the weather data from the API (we expect a list of districts)
        List<Map<String, Object>> weatherDataList = restTemplate.getForObject(url, List.class);

        if (weatherDataList == null || weatherDataList.isEmpty()) {
            return null;
        }

        // Extract the first district's forecast data (since we expect a list of districts)
        Map<String, Object> weatherData = weatherDataList.get(0);

        // Extract the forecast data for the district
        List<Map<String, Object>> forecasts = (List<Map<String, Object>>) weatherData.get("BezirksForecast");

        // Filter the forecast for the provided day
        Map<String, Object> selectedForecast = forecasts.stream()
            .filter(forecast -> forecast.get("date").equals(day))
            .findFirst()
            .orElse(null);  // Return null if no forecast for the given day

        // Add station name and day to the forecast data
        if (selectedForecast != null) {
            selectedForecast.put("stationName", stationName);
            selectedForecast.put("requestedDay", day);
        }

        return selectedForecast;
    }

    // Method to filter and return only relevant weather data
    private Map<String, Object> filterRelevantData(Map<String, Object> forecastData) {
        if (forecastData != null) {
            // Keep only the relevant fields
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
