package com.weatherAPI.weatherApplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WeatherData {

    @JsonProperty("DistrictName")
    private String districtName;

    @JsonProperty("BezirksForecast")
    private List<Forecast> forecast;

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

    public static class Forecast {
        @JsonProperty("WeatherDesc")
        private String weatherDescription;

        @JsonProperty("MaxTemp")
        private int maxTemp;

        @JsonProperty("MinTemp")
        private int minTemp;

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
