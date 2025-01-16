package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.WeatherData;
import com.weatherAPI.weatherApplication.model.WeatherData.Forecast;
import com.weatherAPI.weatherApplication.model.UserPreferences;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Service class for processing and analyzing weather data from an external API.
 * It provides functionality for retrieving weather data, calculating trends, 
 * generating recommendations, and filtering trends based on user preferences.
 */
@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://tourism.api.opendatahub.com/v1/Weather/District";

    /**
     * Constructor for injecting the RestTemplate dependency.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests.
     */
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches weather data for all districts from the external API.
     *
     * @return an array of WeatherData objects containing the weather data for all districts.
     */
    public WeatherData[] getWeatherData() {
        return restTemplate.getForObject(API_URL, WeatherData[].class);
    }

    /**
     * Calculates and retrieves weather trends for all districts.
     *
     * @return a list of maps containing average temperature trends for each district.
     */
    public List<Map<String, Object>> getWeatherTrends() {
        return Arrays.stream(getWeatherData())
            .map(this::calculateTrend)
            .collect(Collectors.toList());
    }
    /**
     * Calculates weather trends for a specific district.
     *
     * @param district the WeatherData for a district.
     * @return a map containing the average temperature trends for the district.
     */
    
    private Map<String, Object> calculateTrend(WeatherData district) {
        Map<String, Object> trend = new HashMap<>();
        trend.put("DistrictName", district.getDistrictName());

        trend.put("AverageMaxTemp", calculateAverage(district.getForecast(), Forecast::getMaxTemp));
        trend.put("AverageMinTemp", calculateAverage(district.getForecast(), Forecast::getMinTemp));

        return trend;
    }

    /**
     * Calculates the average temperature for a list of forecasts based on the provided mapper function.
     *
     * @param forecasts the list of Forecast objects.
     * @param mapper    a function to map a Forecast object to a temperature value.
     * @return the average temperature as a double.
     */
    private double calculateAverage(List<Forecast> forecasts, ToIntFunction<Forecast> mapper) {
        return forecasts.stream().mapToInt(mapper).average().orElse(0.0);
    }

    /**
     * Generates weather recommendations for all districts based on user preferences.
     *
     * @param preferences the user's weather preferences.
     * @return a list of maps containing recommendations for each district.
     */
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

    /**
     * Generates a recommendation for a given district based on the user's weather preferences.
     * 
     * The method iterates through the forecast list of a district and checks if any forecast 
     * matches the provided user preferences. If a match is found, it creates a recommendation 
     * for the district.
     * 
     * @param district    the weather data of the district, including its forecast.
     * @param preferences the user's weather preferences, such as temperature range and preferred weather conditions.
     * @return a map containing the recommendation details if a matching forecast is found, or null otherwise.
     */
    private boolean matchesPreferences(Forecast forecast, UserPreferences preferences) {
        String weatherDescription = preferences.getPreferredWeather();
        return (weatherDescription == null || forecast.getWeatherDescription().equalsIgnoreCase(weatherDescription)) &&
               forecast.getMinTemp() >= preferences.getMinTemperature() &&
               forecast.getMaxTemp() <= preferences.getMaxTemperature();
    }

    /**
     * Creates a weather recommendation for a specific district based on a forecast and day index.
     *
     * @param districtName the name of the district.
     * @param forecast     the forecast data.
     * @param dayIndex     the index of the day in the forecast list (0 for current day).
     * @return a map containing the recommendation details.
     */
    private Map<String, Object> createRecommendation(String districtName, Forecast forecast, int dayIndex) {
        Map<String, Object> recommendation = new HashMap<>();
        recommendation.put("DistrictName", districtName);
        recommendation.put("WeatherDesc", forecast.getWeatherDescription());
        recommendation.put("TemperatureRange", forecast.getMinTemp() + " to " + forecast.getMaxTemp());
        recommendation.put("Day", dayIndex == 0 ? "current" : String.valueOf(dayIndex));
        return recommendation;
    }

    /**
     * Filters weather trends based on user preferences.
     *
     * @param preferences the user's weather preferences.
     * @return a list of maps containing trends that match the user's preferences.
     */
    public List<Map<String, Object>> filterWeatherTrends(UserPreferences preferences) {
        validatePreferences(preferences);

        return getWeatherTrends().stream()
            .filter(trend -> matchesTrendPreferences(trend, preferences))
            .collect(Collectors.toList());
    }

    /**
     * Determines if a trend matches the user's temperature preferences.
     *
     * @param trend        the trend data.
     * @param preferences  the user's weather preferences.
     * @return true if the trend matches the preferences, false otherwise.
     */
    private boolean matchesTrendPreferences(Map<String, Object> trend, UserPreferences preferences) {
        double avgMinTemp = (double) trend.get("AverageMinTemp");
        double avgMaxTemp = (double) trend.get("AverageMaxTemp");
        return avgMinTemp >= preferences.getMinTemperature() &&
               avgMaxTemp <= preferences.getMaxTemperature();
    }

    /**
     * Validates the user's weather preferences to ensure they are logically consistent.
     *
     * @param preferences the user's weather preferences.
     * @throws IllegalArgumentException if the minimum temperature is greater than the maximum temperature.
     */
    private void validatePreferences(UserPreferences preferences) {
        if (preferences.getMinTemperature() > preferences.getMaxTemperature()) {
            throw new IllegalArgumentException("Minimum temperature cannot be greater than maximum temperature.");
        }
    }
}
