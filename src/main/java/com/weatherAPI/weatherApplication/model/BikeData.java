package com.weatherAPI.weatherApplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class BikeData {

    @JsonProperty("pname")
    private String stationName;

    @JsonProperty("pcoordinate")
    private Coordinate coordinate;

    @JsonProperty("pmetadata")
    private Metadata metadata;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public static class Coordinate {
        @JsonProperty("x")
        private double longitude;

        @JsonProperty("y")
        private double latitude;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }

    public static class Metadata {
        @JsonProperty("bikes")
        private Map<String, Integer> bikes;

        @JsonProperty("municipality")
        private String municipality;

        public Map<String, Integer> getBikes() {
            return bikes;
        }

        public void setBikes(Map<String, Integer> bikes) {
            this.bikes = bikes;
        }

        public String getMunicipality() {
            return municipality;
        }

        public void setMunicipality(String municipality) {
            this.municipality = municipality;
        }
    }
}
