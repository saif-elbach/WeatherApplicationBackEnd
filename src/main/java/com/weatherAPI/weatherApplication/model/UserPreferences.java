package com.weatherAPI.weatherApplication.model;

public class UserPreferences {
    private String location; 
    private String preferredWeather; 
    private Integer minTemperature; 
    private Integer maxTemperature; 

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPreferredWeather() {
        return preferredWeather;
    }

    public void setPreferredWeather(String preferredWeather) {
        this.preferredWeather = preferredWeather;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }
}
