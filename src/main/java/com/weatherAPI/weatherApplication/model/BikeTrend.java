package com.weatherAPI.weatherApplication.model;

import java.time.LocalDateTime;

public class BikeTrend {
    private String stationName;
    private Integer availableBikes;
    private LocalDateTime timestamp;

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
}
