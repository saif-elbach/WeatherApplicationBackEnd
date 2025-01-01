package com.weatherAPI.weatherApplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WeatherData {

    @JsonProperty("DistrictName")
    private String districtName;

    @JsonProperty("BezirksForecast")
    private List<Forecast> forecast;

    public WeatherData(String districtName, List<Forecast> forecast) {
        this.districtName = districtName;
        this.forecast = forecast;
    }

    public WeatherData() {}

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

        @JsonProperty("Latitude") 
        private Double latitude;

        @JsonProperty("Longitude")  
        private Double longitude;

        public Forecast(String weatherDescription, int maxTemp, int minTemp, Double latitude, Double longitude) {
            this.weatherDescription = weatherDescription;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Forecast() {}

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

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
}
