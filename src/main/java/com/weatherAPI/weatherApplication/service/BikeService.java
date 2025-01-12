package com.weatherAPI.weatherApplication.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;

@Service
public class BikeService {

    private final RestTemplate restTemplate;

    private final String API_URL = "https://mobility.api.opendatahub.com/v2/flat/Bicycle";

    private final List<BikeTrend> trends = new ArrayList<>();

    public BikeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BikeData> getBikeData() {
        BikeApiResponse response = restTemplate.getForObject("https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class);
        if (response != null) {
            saveBikeTrends(response.getData());
            return response.getData();
        }
        return List.of();
    }

    private void saveBikeTrends(List<BikeData> bikeDataList) {
        for (BikeData bikeData : bikeDataList) {
            if (bikeData.getStationName() != null) {
                BikeTrend trend = new BikeTrend();
                trend.setStationName(bikeData.getStationName());
                trend.setAvailableBikes(getAvailableBikes(bikeData));
                trend.setTimestamp(LocalDateTime.now());
                setCoordinates(trend, bikeData);

                if (!isDuplicateTrend(trend)) {
                    trends.add(trend);
                }
            }
        }
    }

    private int getAvailableBikes(BikeData bikeData) {
        return bikeData.getMetadata() != null && bikeData.getMetadata().getBikes() != null
                ? bikeData.getMetadata().getBikes().getOrDefault("number-available", 0)
                : 0;
    }

    private void setCoordinates(BikeTrend trend, BikeData bikeData) {
        if (bikeData.getCoordinate() != null) {
            trend.setLongitude(bikeData.getCoordinate().getLongitude());
            trend.setLatitude(bikeData.getCoordinate().getLatitude());
        } else {
            trend.setLongitude(0.0);
            trend.setLatitude(0.0);
        }
    }

    private boolean isDuplicateTrend(BikeTrend trend) {
        return trends.stream().anyMatch(existingTrend ->
                Objects.equals(existingTrend.getStationName(), trend.getStationName()) &&
                        existingTrend.getAvailableBikes() == trend.getAvailableBikes() &&
                        existingTrend.getTimestamp().toLocalDate().equals(trend.getTimestamp().toLocalDate()));
    }

    public List<BikeTrend> getTrends() {
        return trends;
    }

    public List<Map<String, Object>> getBikeStations() {
        List<BikeData> bikeDataList = getBikeData();
        Set<String> seenStations = new HashSet<>();

        return bikeDataList.stream()
                .filter(bikeData -> bikeData.getStationName() != null && bikeData.getMetadata() != null)
                .map(bikeData -> createStationMap(bikeData, seenStations))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<String, Object> createStationMap(BikeData bikeData, Set<String> seenStations) {
        Map<String, Object> station = new HashMap<>();
        station.put("stationName", bikeData.getStationName());
        station.put("availableBikes", getAvailableBikes(bikeData));
        double latitude = bikeData.getCoordinate() != null ? bikeData.getCoordinate().getLatitude() : 0.0;
        double longitude = bikeData.getCoordinate() != null ? bikeData.getCoordinate().getLongitude() : 0.0;
        station.put("latitude", latitude);
        station.put("longitude", longitude);

        String stationKey = bikeData.getStationName() + "_" + latitude + "_" + longitude;
        if (seenStations.add(stationKey)) {
            return station;
        }
        return null;
    }
}