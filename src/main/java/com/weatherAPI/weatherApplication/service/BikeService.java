package com.weatherAPI.weatherApplication.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;

/**
 * Service class for managing bike data and trends retrieved from an external API.
 */
@Service
public class BikeService {

    private final RestTemplate restTemplate;

    private final String API_URL = "https://mobility.api.opendatahub.com/v2/flat/Bicycle";

    private final List<BikeTrend> trends = new ArrayList<>();

    /**
     * Constructor to initialize the BikeService.
     *
     * @param restTemplate the RestTemplate for API calls
     */
    public BikeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches bike data from the external API.
     *
     * @return a list of {@link BikeData} objects containing bike station information
     */
    public List<BikeData> getBikeData() {
        BikeApiResponse response = restTemplate.getForObject(API_URL, BikeApiResponse.class);
        if (response != null) {
            saveBikeTrends(response.getData());
            return response.getData();
        }
        return List.of();
    }

    /**
     * Saves trends for bike data based on the latest API response.
     *
     * @param bikeDataList list of {@link BikeData} objects to process and save trends
     */
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

    /**
     * Extracts the number of available bikes from the metadata.
     *
     * @param bikeData the {@link BikeData} object containing metadata
     * @return the number of available bikes
     */
    private int getAvailableBikes(BikeData bikeData) {
        return bikeData.getMetadata() != null && bikeData.getMetadata().getBikes() != null
                ? bikeData.getMetadata().getBikes().getOrDefault("number-available", 0)
                : 0;
    }

    /**
     * Sets the coordinates for a bike trend.
     *
     * @param trend    the {@link BikeTrend} object to update
     * @param bikeData the {@link BikeData} containing coordinate information
     */
    private void setCoordinates(BikeTrend trend, BikeData bikeData) {
        if (bikeData.getCoordinate() != null) {
            trend.setLongitude(bikeData.getCoordinate().getLongitude());
            trend.setLatitude(bikeData.getCoordinate().getLatitude());
        } else {
            trend.setLongitude(0.0);
            trend.setLatitude(0.0);
        }
    }

    /**
     * Checks if a bike trend is a duplicate.
     *
     * @param trend the {@link BikeTrend} to check
     * @return true if the trend is a duplicate, false otherwise
     */
    private boolean isDuplicateTrend(BikeTrend trend) {
        return trends.stream().anyMatch(existingTrend ->
                Objects.equals(existingTrend.getStationName(), trend.getStationName()) &&
                        existingTrend.getAvailableBikes() == trend.getAvailableBikes() &&
                        existingTrend.getTimestamp().toLocalDate().equals(trend.getTimestamp().toLocalDate()));
    }

    /**
     * Retrieves the list of saved bike trends.
     *
     * @return a list of {@link BikeTrend} objects
     */
    public List<BikeTrend> getTrends() {
        return trends;
    }

    /**
     * Retrieves a list of bike stations with their details.
     *
     * @return a list of maps containing station details
     */
    public List<Map<String, Object>> getBikeStations() {
        List<BikeData> bikeDataList = getBikeData();
        Set<String> seenStations = new HashSet<>();

        return bikeDataList.stream()
                .filter(bikeData -> bikeData.getStationName() != null && bikeData.getMetadata() != null)
                .map(bikeData -> createStationMap(bikeData, seenStations))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Creates a map of station details if not already seen.
     *
     * @param bikeData    the {@link BikeData} object containing station details
     * @param seenStations a set of seen station keys to avoid duplicates
     * @return a map of station details or null if already seen
     */
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
