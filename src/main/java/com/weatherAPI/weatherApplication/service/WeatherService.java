package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.WeatherData;
import com.weatherAPI.weatherApplication.model.WeatherData.Forecast;
import com.weatherAPI.weatherApplication.model.UserPreferences;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://tourism.api.opendatahub.com/v1/Weather/District";

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherData[] getWeatherData() {
        return restTemplate.getForObject(API_URL, WeatherData[].class);
    }

    public List<Map<String, Object>> getWeatherTrends() {
        return Arrays.stream(getWeatherData())
            .map(this::calculateTrend)
            .collect(Collectors.toList());
    }

    private Map<String, Object> calculateTrend(WeatherData district) {
        Map<String, Object> trend = new HashMap<>();
        trend.put("DistrictName", district.getDistrictName());

        trend.put("AverageMaxTemp", calculateAverage(district.getForecast(), Forecast::getMaxTemp));
        trend.put("AverageMinTemp", calculateAverage(district.getForecast(), Forecast::getMinTemp));

        return trend;
    }

    private double calculateAverage(List<Forecast> forecasts, ToIntFunction<Forecast> mapper) {
        return forecasts.stream().mapToInt(mapper).average().orElse(0.0);
    }

    public List<Map<String, Object>> getRecommendations(UserPreferences preferences) {
        validatePreferences(preferences);

        return Arrays.stream(getWeatherData())
            .map(district -> getRecommendationForDistrict(district, preferences))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private Map<String, Object> getRecommendationForDistrict(WeatherData district, UserPreferences preferences) {
        for (int i = 0; i < district.getForecast().size(); i++) {
            Forecast forecast = district.getForecast().get(i);

            if (matchesPreferences(forecast, preferences)) {
                return createRecommendation(district.getDistrictName(), forecast, i);
            }
        }
        return null;
    }

    private boolean matchesPreferences(Forecast forecast, UserPreferences preferences) {
        String weatherDescription = preferences.getPreferredWeather();
        return (weatherDescription == null || forecast.getWeatherDescription().equalsIgnoreCase(weatherDescription)) &&
               forecast.getMinTemp() >= preferences.getMinTemperature() &&
               forecast.getMaxTemp() <= preferences.getMaxTemperature();
    }

    private Map<String, Object> createRecommendation(String districtName, Forecast forecast, int dayIndex) {
        Map<String, Object> recommendation = new HashMap<>();
        recommendation.put("DistrictName", districtName);
        recommendation.put("WeatherDesc", forecast.getWeatherDescription());
        recommendation.put("TemperatureRange", forecast.getMinTemp() + " to " + forecast.getMaxTemp());
        recommendation.put("Day", dayIndex == 0 ? "current" : String.valueOf(dayIndex));
        return recommendation;
    }

    public List<Map<String, Object>> filterWeatherTrends(UserPreferences preferences) {
        validatePreferences(preferences);

        return getWeatherTrends().stream()
            .filter(trend -> matchesTrendPreferences(trend, preferences))
            .collect(Collectors.toList());
    }

    private boolean matchesTrendPreferences(Map<String, Object> trend, UserPreferences preferences) {
        double avgMinTemp = (double) trend.get("AverageMinTemp");
        double avgMaxTemp = (double) trend.get("AverageMaxTemp");
        return avgMinTemp >= preferences.getMinTemperature() &&
               avgMaxTemp <= preferences.getMaxTemperature();
    }

    private void validatePreferences(UserPreferences preferences) {
        if (preferences.getMinTemperature() > preferences.getMaxTemperature()) {
            throw new IllegalArgumentException("Minimum temperature cannot be greater than maximum temperature.");
        }
    }
}
