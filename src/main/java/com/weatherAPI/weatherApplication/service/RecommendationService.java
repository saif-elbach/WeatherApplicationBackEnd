package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.BikeTrend;

import com.weatherAPI.weatherApplication.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private BikeService bikeService;

    @Autowired
    private WeatherService weatherService;

    public Map<String, Object> getBikeWeatherRecommendations() {
        List<Map<String, Object>> weatherData = getWeatherData();

        List<BikeTrend> bikeTrends = bikeService.getTrends();

        List<Map<String, Object>> bikeStationsWithTrends = bikeTrends.stream()
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

        List<Map<String, Object>> bikeStations = bikeService.getBikeStations();

        List<Map<String, Object>> bikeStationsWithDistances = new ArrayList<>();

        if (!bikeStations.isEmpty()) {
            for (Map<String, Object> bikeStation : bikeStations) {
                Map<String, Object> mutableBikeStation = new HashMap<>(bikeStation);

                Double stationLat = (Double) mutableBikeStation.get("latitude");
                Double stationLon = (Double) mutableBikeStation.get("longitude");

                if (stationLat == null || stationLon == null) {
                    continue;
                }

                Map<String, Object> bikeStationDetails = new HashMap<>();
                bikeStationDetails.put("stationName", mutableBikeStation.get("stationName"));
                bikeStationDetails.put("availableBikes", mutableBikeStation.get("availableBikes"));

                Set<Map<String, Object>> distancesForWeatherStations = new HashSet<>();

                for (Map<String, Object> weather : weatherData) {
                    Double weatherLat = (Double) weather.get("Latitude");
                    Double weatherLon = (Double) weather.get("Longitude");

                    if (weatherLat == null || weatherLon == null) {
                        continue;
                    }

                    double distanceToWeather = calculateDistance(stationLat, stationLon, weatherLat, weatherLon);
                    distanceToWeather = Math.round(distanceToWeather);

                    distancesForWeatherStations.add(Map.of(
                        "weatherDistrict", weather.get("DistrictName"),
                        "distance", distanceToWeather
                    ));
                }

                bikeStationDetails.put("distancesToWeatherStations", new ArrayList<>(distancesForWeatherStations));

                Set<Map<String, Object>> distancesForBikeStations = new HashSet<>();

                for (Map<String, Object> otherBikeStation : bikeStations) {
                    if (otherBikeStation.equals(mutableBikeStation)) {
                        continue;
                    }

                    Double otherStationLat = (Double) otherBikeStation.get("latitude");
                    Double otherStationLon = (Double) otherBikeStation.get("longitude");

                    if (otherStationLat == null || otherStationLon == null) {
                        continue;
                    }

                    double distanceToBike = calculateDistance(stationLat, stationLon, otherStationLat, otherStationLon);
                    distanceToBike = Math.round(distanceToBike);

                    distancesForBikeStations.add(Map.of(
                        "bikeStationName", otherBikeStation.get("stationName"),
                        "distance", distanceToBike
                    ));
                }

                bikeStationDetails.put("distancesToBikeStations", new ArrayList<>(distancesForBikeStations));

                bikeStationsWithDistances.add(bikeStationDetails);
            }
        }

        return Map.of(
            "weatherData", weatherData,
            "bikeStationsWithDistances", bikeStationsWithDistances,
            "bikeStationsWithTrends", bikeStationsWithTrends
        );
    }

    public Map<String, Object> getDistancesToBikeStations(String weatherStationName) {
        List<Map<String, Object>> weatherData = getWeatherData();

        Optional<Map<String, Object>> weatherStationOpt = weatherData.stream()
                .filter(weather -> weather.get("DistrictName").equals(weatherStationName))
                .findFirst();

        if (!weatherStationOpt.isPresent()) {
            return Map.of("error", "Weather station not found");
        }

        Map<String, Object> weatherStation = weatherStationOpt.get();
        Double weatherLat = (Double) weatherStation.get("Latitude");
        Double weatherLon = (Double) weatherStation.get("Longitude");

        List<Map<String, Object>> bikeStations = bikeService.getBikeStations();

        List<Map<String, Object>> bikeStationDistances = new ArrayList<>();

        for (Map<String, Object> bikeStation : bikeStations) {
            Double stationLat = (Double) bikeStation.get("latitude");
            Double stationLon = (Double) bikeStation.get("longitude");

            if (stationLat == null || stationLon == null) {
                continue;
            }

            double distance = calculateDistance(weatherLat, weatherLon, stationLat, stationLon);

            distance = Math.round(distance);

            bikeStationDistances.add(Map.of(
                    "bikeStationName", bikeStation.get("stationName"),
                    "distance", distance,
                    "availableBikes", bikeStation.get("availableBikes")
            ));
        }

        return Map.of(
                "weatherStationName", weatherStationName,
                "distancesToBikeStations", bikeStationDistances
        );
    }

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
