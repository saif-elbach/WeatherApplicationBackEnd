package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     * Also calls the `saveBikeTrends` method to store trends in memory.
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
     * Trends are added to the in-memory list `trends`.
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
}
