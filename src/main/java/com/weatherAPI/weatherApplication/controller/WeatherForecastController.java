package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.service.WeatherForecastService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
/**
 * REST controller for managing weather forecasts.
 * 
 * <p>This controller provides an API endpoint for retrieving weather forecasts for specific days 
 * across all available stations. It delegates the logic to the {@link WeatherForecastService}.
 * </p>
 */
@RestController
@RequestMapping("/weather")
public class WeatherForecastController {

    private final WeatherForecastService weatherForecastService;

    /**
     * Constructor to inject dependencies.
     *
     * @param weatherForecastService the service handling weather forecast operations
     */
    public WeatherForecastController(WeatherForecastService weatherForecastService) {
        this.weatherForecastService = weatherForecastService;
    }

    /**
     * Retrieves the weather forecast for a specific day across all stations.
     *
     * @param day the day for which the forecast is requested (e.g., "Monday")
     * @return a list of maps containing the forecast details, or a message if no data is available
     */
    @GetMapping("/forecast")
    public List<Map<String, Object>> getWeatherForecastForDay(@RequestParam String day) {
        List<Map<String, Object>> weatherForecasts = weatherForecastService.getWeatherForecastForAllStations(day);

        if (weatherForecasts.isEmpty()) {
            return List.of(Map.of("message", "No weather data found for the given day."));
        }

        return weatherForecasts;
    }
}
