package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.BikeTrend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for generating recommendations based on bike trends and weather data.
 * It calculates distances between weather stations and bike stations, maps bike trends to stations,
 * and provides weather-related recommendations for bikes.
 */
@Service
public class RecommendationService {

    @Autowired
    private BikeService bikeService;

    @Autowired
    private WeatherService weatherService;

    /**
     * Retrieves bike and weather recommendations including mapped bike trends, weather data,
     * and calculated distances between bike stations and weather stations.
     *
     * @return a map containing weather data, bike stations with trends, and distances to stations.
     */
    public Map<String, Object> getBikeWeatherRecommendations() {
        List<Map<String, Object>> weatherData = getWeatherData();
        List<BikeTrend> bikeTrends = bikeService.getTrends();
        List<Map<String, Object>> bikeStations = bikeService.getBikeStations();

        List<Map<String, Object>> bikeStationsWithTrends = mapBikeTrends(bikeTrends);
        List<Map<String, Object>> bikeStationsWithDistances = calculateBikeStationsDistances(bikeStations, weatherData);

        return Map.of(
                "weatherData", weatherData,
                "bikeStationsWithDistances", bikeStationsWithDistances,
                "bikeStationsWithTrends", bikeStationsWithTrends
        );
    }

    /**
     * Calculates distances from a specified weather station to all bike stations.
     *
     * @param weatherStationName the name of the weather station.
     * @return a map containing the distances to bike stations or an error if the weather station is not found.
     */
    
    public Map<String, Object> getDistancesToBikeStations(String weatherStationName) {
        List<Map<String, Object>> weatherData = getWeatherData();
        Optional<Map<String, Object>> weatherStationOpt = findWeatherStationByName(weatherData, weatherStationName);

        if (weatherStationOpt.isEmpty()) {
            return Map.of("error", "Weather station not found");
        }

        Map<String, Object> weatherStation = weatherStationOpt.get();
        List<Map<String, Object>> bikeStations = bikeService.getBikeStations();
        List<Map<String, Object>> bikeStationDistances = calculateDistancesToWeatherStation(weatherStation, bikeStations);

        return Map.of(
                "weatherStationName", weatherStationName,
                "distancesToBikeStations", bikeStationDistances
        );
    }

    /**
     * Maps bike trends to a simplified data structure for easier processing.
     *
     * @param bikeTrends a list of bike trend data.
     * @return a list of maps containing bike trend information.
     */
    
    private List<Map<String, Object>> mapBikeTrends(List<BikeTrend> bikeTrends) {
    	return bikeTrends.stream()
    	        .map(trend -> {
    	            Map<String, Object> trendMap = new HashMap<>();
    	            trendMap.put("stationName", trend.getStationName());
    	            trendMap.put("availableBikes", trend.getAvailableBikes());
    	            trendMap.put("timestamp", trend.getTimestamp());
    	            trendMap.put("latitude", trend.getLatitude());
    	            trendMap.put("longitude", trend.getLongitude());
    	            return trendMap;
    	        })
    	        .collect(Collectors.toList());

    }

    /**
     * Calculates distances between bike stations and other stations (weather or bike).
     * Augments bike station data with distance information.
     *
     * @param bikeStations a list of bike station data.
     * @param weatherData  a list of weather station data.
     * @return a list of bike stations with calculated distances to other stations.
     */
    private List<Map<String, Object>> calculateBikeStationsDistances(List<Map<String, Object>> bikeStations, List<Map<String, Object>> weatherData) {
        return bikeStations.stream().map(station -> {
            Double stationLat = (Double) station.get("latitude");
            Double stationLon = (Double) station.get("longitude");

            if (stationLat == null || stationLon == null) return null;

            Map<String, Object> bikeStationDetails = new HashMap<>(station);

            bikeStationDetails.put("distancesToWeatherStations", calculateDistancesToStations(stationLat, stationLon, weatherData, "DistrictName"));
            bikeStationDetails.put("distancesToBikeStations", calculateDistancesToStations(stationLat, stationLon, bikeStations, "stationName"));

            return bikeStationDetails;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Computes distances between a given point and a list of stations (weather or bike).
     *
     * @param lat1    the latitude of the origin point.
     * @param lon1    the longitude of the origin point.
     * @param stations a list of station data.
     * @param nameKey the key for identifying the station (e.g., "DistrictName" or "stationName").
     * @return a list of maps containing station names and their distances from the origin point.
     */
    private List<Map<String, Object>> calculateDistancesToStations(Double lat1, Double lon1, List<Map<String, Object>> stations, String nameKey) {
        return stations.stream().map(station -> {
            Double stationLat = (Double) station.get("latitude");
            Double stationLon = (Double) station.get("longitude");

            if (stationLat == null || stationLon == null) return null;

            double distance = Math.round(calculateDistance(lat1, lon1, stationLat, stationLon));

            return Map.of(
                    nameKey, station.get(nameKey),
                    "distance", distance
            );
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Calculates distances between a specified weather station and all bike stations.
     *
     * @param weatherStation the weather station data.
     * @param bikeStations   a list of bike station data.
     * @return a list of maps containing bike station names, distances, and available bikes.
     */
    private List<Map<String, Object>> calculateDistancesToWeatherStation(Map<String, Object> weatherStation, List<Map<String, Object>> bikeStations) {
        Double weatherLat = (Double) weatherStation.get("Latitude");
        Double weatherLon = (Double) weatherStation.get("Longitude");

        return bikeStations.stream().map(bikeStation -> {
            Double stationLat = (Double) bikeStation.get("latitude");
            Double stationLon = (Double) bikeStation.get("longitude");

            if (stationLat == null || stationLon == null) return null;

            double distance = Math.round(calculateDistance(weatherLat, weatherLon, stationLat, stationLon));

            return Map.of(
                    "bikeStationName", bikeStation.get("stationName"),
                    "distance", distance,
                    "availableBikes", bikeStation.get("availableBikes")
            );
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Searches for a weather station by name from a list of weather data.
     *
     * @param weatherData        a list of weather station data.
     * @param weatherStationName the name of the weather station to search for.
     * @return an Optional containing the weather station if found, or empty if not.
     */
    private Optional<Map<String, Object>> findWeatherStationByName(List<Map<String, Object>> weatherData, String weatherStationName) {
        return weatherData.stream()
                .filter(weather -> weatherStationName.equals(weather.get("DistrictName")))
                .findFirst();
    }

    /**
     * Retrieves predefined weather data.
     *
     * @return a list of maps representing weather station data.
     */
    private List<Map<String, Object>> getWeatherData() {
        return Arrays.asList(
                Map.of("DistrictName", "Bolzano, Überetsch and Unterland", "Latitude", 46.498, "Longitude", 11.354),
                Map.of("DistrictName", "Burggrafenamt - Meran and surroundings", "Latitude", 46.67, "Longitude", 11.162),
                Map.of("DistrictName", "Vinschgau", "Latitude", 46.69, "Longitude", 10.55),
                Map.of("DistrictName", "Eisacktal and Sarntal", "Latitude", 46.6, "Longitude", 11.5),
                Map.of("DistrictName", "Wipptal - Sterzing and surroundings", "Latitude", 46.85, "Longitude", 11.4),
                Map.of("DistrictName", "Pustertal", "Latitude", 46.7, "Longitude", 11.9),
                Map.of("DistrictName", "Ladinia - Dolomites", "Latitude", 46.5, "Longitude", 11.4)
        );
    }

    /**
     * Calculates the distance in kilometers between two geographical coordinates.
     *
     * @param lat1 the latitude of the first point.
     * @param lon1 the longitude of the first point.
     * @param lat2 the latitude of the second point.
     * @param lon2 the longitude of the second point.
     * @return the calculated distance in kilometers.
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
