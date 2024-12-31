package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.service.BikeService;
import com.weatherAPI.weatherApplication.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    @Autowired
    private BikeService bikeService;

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/bike-weather-recommendations")
    public Map<String, Object> getBikeWeatherRecommendations() {
        List<Map<String, Object>> weatherData = getWeatherData();

        List<Map<String, Object>> bikeStations = bikeService.getBikeStations();

        Map<String, List<Map<String, Object>>> districtToBikeStations = mapBikeStationsToDistricts(bikeStations, weatherData);

        return Map.of(
            "weatherData", weatherData,
            "bikeStationsByDistrict", districtToBikeStations
        );
    }

    private List<Map<String, Object>> getWeatherData() {
        return Arrays.asList(
            Map.of(
                "DistrictName", "Bolzano, Überetsch and Unterland",
                "Latitude", 46.498,
                "Longitude", 11.354
            ),
            Map.of(
                "DistrictName", "Burggrafenamt - Meran and surroundings",
                "Latitude", 46.67,
                "Longitude", 11.162
            ),
            Map.of(
                "DistrictName", "Vinschgau",
                "Latitude", 46.69,
                "Longitude", 10.55
            ),
            Map.of(
                "DistrictName", "Eisacktal and Sarntal",
                "Latitude", 46.6,
                "Longitude", 11.5
            ),
            Map.of(
                "DistrictName", "Wipptal - Sterzing and surroundings",
                "Latitude", 46.85,
                "Longitude", 11.4
            ),
            Map.of(
                "DistrictName", "Pustertal",
                "Latitude", 46.7,
                "Longitude", 11.9
            ),
            Map.of(
                "DistrictName", "Ladinia - Dolomites",
                "Latitude", 46.5,
                "Longitude", 11.4
            )
        );
    }

    private Map<String, List<Map<String, Object>>> mapBikeStationsToDistricts(
            List<Map<String, Object>> bikeStations,
            List<Map<String, Object>> weatherData
    ) {
        Map<String, Set<Map<String, Object>>> districtToBikeStations = new HashMap<>();

        for (Map<String, Object> district : weatherData) {
            String districtName = (String) district.get("DistrictName");
            districtToBikeStations.put(districtName, new HashSet<>());
        }

        for (Map<String, Object> bikeStation : bikeStations) {
            if (bikeStation != null && bikeStation.get("pname") != null && bikeStation.get("pcoordinate") != null) {
                String stationName = (String) bikeStation.get("pname");
                Map<String, Object> coordinate = (Map<String, Object>) bikeStation.get("pcoordinate");
                double stationLat = (double) coordinate.get("y");
                double stationLon = (double) coordinate.get("x");

                int availableBikes = 0;
                if (bikeStation.get("pmetadata") != null) {
                    Map<String, Object> bikes = (Map<String, Object>) ((Map<String, Object>) bikeStation.get("pmetadata")).get("bikes");
                    if (bikes != null && bikes.get("number-available") != null) {
                        availableBikes = (int) bikes.get("number-available");
                    }
                }

                String nearestDistrict = findNearestDistrict(stationLat, stationLon, weatherData);

                if (nearestDistrict != null) {
                    districtToBikeStations.get(nearestDistrict).add(Map.of(
                        "stationName", stationName,
                        "availableBikes", availableBikes
                    ));
                }
            }
        }

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        for (Map.Entry<String, Set<Map<String, Object>>> entry : districtToBikeStations.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return result;
    }

    private String findNearestDistrict(double stationLat, double stationLon, List<Map<String, Object>> weatherData) {
        double minDistance = Double.MAX_VALUE;
        String nearestDistrict = null;

        for (Map<String, Object> district : weatherData) {
            String districtName = (String) district.get("DistrictName");
            double districtLat = (double) district.get("Latitude");
            double districtLon = (double) district.get("Longitude");

            double distance = calculateDistance(stationLat, stationLon, districtLat, districtLon);
            if (distance < minDistance) {
                minDistance = distance;
                nearestDistrict = districtName;
            }
        }

        return nearestDistrict;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDifference = lat2 - lat1;
        double lonDifference = lon2 - lon1;
        return Math.sqrt(latDifference * latDifference + lonDifference * lonDifference);
    }
}