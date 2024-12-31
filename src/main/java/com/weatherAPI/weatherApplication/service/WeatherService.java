package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.WeatherData;
import com.weatherAPI.weatherApplication.model.WeatherData.Forecast;
import com.weatherAPI.weatherApplication.model.UserPreferences;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<Map<String, Object>> getWeatherTrends() {
        WeatherData[] weatherData = getWeatherData();
        List<Map<String, Object>> trends = new ArrayList<>();

        for (WeatherData district : weatherData) {
            Map<String, Object> trend = new HashMap<>();
            trend.put("DistrictName", district.getDistrictName());

            double avgMaxTemp = district.getForecast().stream()
                .mapToInt(Forecast::getMaxTemp)
                .average()
                .orElse(0.0);
            trend.put("AverageMaxTemp", avgMaxTemp);

            double avgMinTemp = district.getForecast().stream()
                .mapToInt(Forecast::getMinTemp)
                .average()
                .orElse(0.0);
            trend.put("AverageMinTemp", avgMinTemp);

            trends.add(trend);
        }

        return trends;
    }

    public List<Map<String, Object>> getRecommendations(UserPreferences preferences) {
        validatePreferences(preferences); 
        WeatherData[] weatherData = getWeatherData();
        List<Map<String, Object>> recommendations = new ArrayList<>();

        for (WeatherData district : weatherData) {
            String districtName = district.getDistrictName();
            boolean districtMatched = false;

            for (int i = 0; i < district.getForecast().size(); i++) {
                Forecast forecast = district.getForecast().get(i);
                String weatherDescription = forecast.getWeatherDescription();
                int maxTemp = forecast.getMaxTemp();
                int minTemp = forecast.getMinTemp();

                if ((preferences.getPreferredWeather() == null ||
                        weatherDescription.equalsIgnoreCase(preferences.getPreferredWeather())) &&
                    minTemp >= preferences.getMinTemperature() &&
                    maxTemp <= preferences.getMaxTemperature()) {

                    Map<String, Object> recommendation = new HashMap<>();
                    recommendation.put("DistrictName", districtName);
                    recommendation.put("WeatherDesc", weatherDescription);
                    recommendation.put("TemperatureRange", minTemp + " to " + maxTemp);

                    if (i == 0) {
                        recommendation.put("Day", "current");
                    } else {
                        recommendation.put("Day", String.valueOf(i)); 
                    }

                    recommendations.add(recommendation);
                    districtMatched = true;
                    break;
                }
            }

            if (!districtMatched) {
                System.out.println("No matching forecast for district: " + districtName);
            }
        }

        if (recommendations.isEmpty()) {
            Map<String, Object> noResult = new HashMap<>();
            noResult.put("message", "No matching recommendations found for your preferences.");
            recommendations.add(noResult);
        }

        return recommendations;
    }


    public List<Map<String, Object>> filterWeatherTrends(UserPreferences preferences) {
        validatePreferences(preferences);
        List<Map<String, Object>> trends = getWeatherTrends();

        return trends.stream()
            .filter(trend -> {
                double avgMinTemp = (double) trend.get("AverageMinTemp");
                double avgMaxTemp = (double) trend.get("AverageMaxTemp");

                return avgMinTemp >= preferences.getMinTemperature() &&
                       avgMaxTemp <= preferences.getMaxTemperature();
            })
            .collect(Collectors.toList());
    }

    private void validatePreferences(UserPreferences preferences) {
        if (preferences.getMinTemperature() > preferences.getMaxTemperature()) {
            throw new IllegalArgumentException("Minimum temperature cannot be greater than maximum temperature.");
        }
    }

    private final Map<String, String> districtToMunicipality = Map.of(
        "Bolzano, Überetsch and Unterland", "Bolzano",
        "Burggrafenamt - Meran and surroundings", "Meran - Merano",
        "Vinschgau", "Vinschgau",
        "Eisacktal and Sarntal", "Sarntal",
        "Wipptal - Sterzing and surroundings", "Sterzing",
        "Pustertal", "Pustertal",
        "Ladinia - Dolomites", "Dolomites"
    );
}
