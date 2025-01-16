package com.weatherAPI.weatherApplication.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for fetching and processing weather forecast data for multiple stations.
 * It provides functionality to retrieve weather data for specific days and filter relevant details.
 */
@Service
public class WeatherForecastService {

    private final RestTemplate restTemplate;

    /**
     * Constructor for injecting the RestTemplate dependency.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests.
     */ 
    public WeatherForecastService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves weather forecast data for all predefined stations for a specific day.
     *
     * @param day the date for which the weather forecast is requested (in YYYY-MM-DD format).
     * @return a list of maps containing filtered weather data for each station.
     */
    public List<Map<String, Object>> getWeatherForecastForAllStations(String day) {
        List<String> stations = List.of("Bolzano", "Meran", "Vinschgau", "Eisacktal", "Pustertal", "Wipptal");

        return stations.stream()
                .map(station -> fetchWeatherForStation(station, day))
                .filter(Objects::nonNull)
                .map(this::filterRelevantWeatherData)
                .collect(Collectors.toList());
    }

    /**
     * Fetches weather data for a specific station and day from the external API.
     *
     * @param stationName the name of the weather station.
     * @param day         the date for which the forecast is requested (in YYYY-MM-DD format).
     * @return a map containing the weather forecast for the specified station and day, or null if no data is found.
     */
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

    /**
     * Filters and extracts relevant weather information from the raw forecast data.
     *
     * @param forecastData the raw forecast data.
     * @return a map containing only the relevant weather details.
     */
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
