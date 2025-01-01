package com.weatherAPI.weatherApplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WeatherData {

    @JsonProperty("DistrictName")
    private String districtName;

    @JsonProperty("BezirksForecast")
    private List<Forecast> forecast;

    // Constructor to match the test case
    public WeatherData(String districtName, List<Forecast> forecast) {
        this.districtName = districtName;
        this.forecast = forecast;
    }

    // Default constructor if needed
    public WeatherData() {}

    // Getters and setters
    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public List<Forecast> getForecast() {
        return forecast;
    }

    public void setForecast(List<Forecast> forecast) {
        this.forecast = forecast;
    }

    // Static inner class for Forecast
    public static class Forecast {

        @JsonProperty("WeatherDesc")
        private String weatherDescription;

        @JsonProperty("MaxTemp")
        private int maxTemp;

        @JsonProperty("MinTemp")
        private int minTemp;

        // Constructor to match the test case
        public Forecast(String weatherDescription, int maxTemp, int minTemp) {
            this.weatherDescription = weatherDescription;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
        }

        // Default constructor
        public Forecast() {}

        // Getters and setters
        public String getWeatherDescription() {
            return weatherDescription;
        }

        public void setWeatherDescription(String weatherDescription) {
            this.weatherDescription = weatherDescription;
        }

        public int getMaxTemp() {
            return maxTemp;
        }

        public void setMaxTemp(int maxTemp) {
            this.maxTemp = maxTemp;
        }

        public int getMinTemp() {
            return minTemp;
        }

        public void setMinTemp(int minTemp) {
            this.minTemp = minTemp;
        }
    }
}
