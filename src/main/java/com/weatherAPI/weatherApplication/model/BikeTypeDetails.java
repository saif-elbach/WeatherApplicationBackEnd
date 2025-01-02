package com.weatherAPI.weatherApplication.model;

import java.util.Map;

public class BikeTypeDetails {
    private String stationName;
    private Map<String, Integer> bikeTypes; 
    private String municipality;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Map<String, Integer> getBikeTypes() {
        return bikeTypes;
    }

    public void setBikeTypes(Map<String, Integer> bikeTypes) {
        this.bikeTypes = bikeTypes;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
}
