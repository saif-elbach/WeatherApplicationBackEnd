package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class BikeService {

    private final RestTemplate restTemplate;

    private final String API_URL = "https://mobility.api.opendatahub.com/v2/flat/Bicycle";

    private final List<BikeTrend> trends = new ArrayList<>();

    public BikeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches live bike data from the external API.
     * Also calls the saveBikeTrends method to store trends in memory.
     *
     * @return List of BikeData objects representing live bike data.
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
     * Saves trends for the fetched bike data by processing each station's metadata.
     * Trends are added to the in-memory list trends.
     *
     * @param bikeDataList List of BikeData objects fetched from the API.
     */
    private void saveBikeTrends(List<BikeData> bikeDataList) {
        for (BikeData bikeData : bikeDataList) {
            if (bikeData.getStationName() != null) { 
                BikeTrend trend = new BikeTrend();
                trend.setStationName(bikeData.getStationName());

                if (bikeData.getMetadata() != null && bikeData.getMetadata().getBikes() != null) {
                    trend.setAvailableBikes(bikeData.getMetadata().getBikes().getOrDefault("number-available", 0));
                } else {
                    trend.setAvailableBikes(0);
                }

                trend.setTimestamp(LocalDateTime.now());

                if (bikeData.getCoordinate() != null) {
                    trend.setLongitude(bikeData.getCoordinate().getLongitude());
                    trend.setLatitude(bikeData.getCoordinate().getLatitude());
                } else {
                    trend.setLongitude(0.0); 
                    trend.setLatitude(0.0); 
                }

                boolean isDuplicate = trends.stream().anyMatch(existingTrend ->
                    existingTrend.getStationName() != null &&
                    existingTrend.getStationName().equals(trend.getStationName()) &&
                    existingTrend.getAvailableBikes() == trend.getAvailableBikes() &&
                    existingTrend.getTimestamp().toLocalDate().equals(trend.getTimestamp().toLocalDate())
                );

                if (!isDuplicate) {
                    trends.add(trend);
                }
            }
        }
    }


    /**
     * Exposes the saved trends in the in-memory list.
     *
     * @return List of BikeTrend objects representing historical trends.
     */
    public List<BikeTrend> getTrends() {
        return trends;
    }
    
    /**
     * Fetches the bike station data for integration with weather districts.
     *
     * @return List of maps containing station name and available bikes.
     */
    public List<Map<String, Object>> getBikeStations() {
        List<BikeData> bikeDataList = getBikeData();
        List<Map<String, Object>> stations = new ArrayList<>();
        List<String> seenStations = new ArrayList<>(); 

        for (BikeData bikeData : bikeDataList) {
            if (bikeData.getStationName() != null && bikeData.getMetadata() != null) {
                Map<String, Object> station = new HashMap<>();
                station.put("stationName", bikeData.getStationName());

                if (bikeData.getMetadata().getBikes() != null) {
                    station.put("availableBikes", bikeData.getMetadata().getBikes().getOrDefault("number-available", 0));
                } else {
                    station.put("availableBikes", 0); 
                }

                double latitude = (bikeData.getCoordinate() != null) ? bikeData.getCoordinate().getLatitude() : 0.0;
                double longitude = (bikeData.getCoordinate() != null) ? bikeData.getCoordinate().getLongitude() : 0.0;

                station.put("latitude", latitude);
                station.put("longitude", longitude);

                String stationKey = bikeData.getStationName() + "_" + latitude + "_" + longitude;
                
                if (!seenStations.contains(stationKey)) {
                    seenStations.add(stationKey);
                    stations.add(station);
                }
            }
        }

        return stations;
    }




}