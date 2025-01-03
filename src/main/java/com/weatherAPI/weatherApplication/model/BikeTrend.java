package com.weatherAPI.weatherApplication.model;

import java.time.LocalDateTime;

public class BikeTrend {
    private String stationName;
    private Integer availableBikes;
    private LocalDateTime timestamp;
    private double latitude;
    private double longitude;

    public BikeTrend(String stationName, Integer availableBikes, LocalDateTime timestamp, double latitude, double longitude) {
        this.stationName = stationName;
        this.availableBikes = availableBikes;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BikeTrend() {
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Integer getAvailableBikes() {
        return availableBikes;
    }

    public void setAvailableBikes(Integer availableBikes) {
        this.availableBikes = availableBikes;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
