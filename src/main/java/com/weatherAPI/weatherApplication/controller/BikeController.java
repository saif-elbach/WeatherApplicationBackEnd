package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;
import com.weatherAPI.weatherApplication.model.BikeTypeDetails;
import com.weatherAPI.weatherApplication.service.BikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/bikes")
@CrossOrigin(origins = "http://localhost:3000")
public class BikeController {

	private final BikeService bikeService;

    /**
     * Constructor to inject the {@link BikeService} dependency.
     *
     * @param bikeService the service handling bike-related operations
     */
    @Autowired
    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    /**
     * Retrieves all bike data.
     *
     * @return a list of {@link BikeData} objects
     */
    @GetMapping
    public List<BikeData> getBikeData() {
        return bikeService.getBikeData();
    }

    /**
     * Retrieves bike trends.
     *
     * @return a list of {@link BikeTrend} objects containing trend details
     */
    @GetMapping("/trends")
    public List<BikeTrend> getTrends() {
        return bikeService.getTrends();
    }

    /**
     * Retrieves details of bike stations.
     *
     * @return a list of maps containing bike station details
     */
    @GetMapping("/stations")
    public List<Map<String, Object>> getBikeStations() {
        return bikeService.getBikeStations();
    }

    /**
     * Retrieves details of bike types available at various stations.
     *
     * @return a list of {@link BikeTypeDetails} objects containing bike type details
     */
    @GetMapping("/types")
    public List<BikeTypeDetails> getBikeTypeDetails() {
        List<BikeData> bikeDataList = bikeService.getBikeData();
        List<BikeTypeDetails> bikeTypeDetailsList = new ArrayList<>();

        for (BikeData bikeData : bikeDataList) {
            if (hasValidMetadata(bikeData)) {
                bikeTypeDetailsList.add(mapToBikeTypeDetails(bikeData));
            }
        }
        return bikeTypeDetailsList;
    }

    /**
     * Checks if a {@link BikeData} object has valid metadata.
     *
     * @param bikeData the bike data to validate
     * @return {@code true} if metadata is valid, {@code false} otherwise
     */
    private boolean hasValidMetadata(BikeData bikeData) {
        return bikeData.getMetadata() != null && bikeData.getMetadata().getBikes() != null;
    }

    /**
     * Maps a {@link BikeData} object to a {@link BikeTypeDetails} object.
     *
     * @param bikeData the bike data to map
     * @return a {@link BikeTypeDetails} object containing bike type details
     */
    private BikeTypeDetails mapToBikeTypeDetails(BikeData bikeData) {
        BikeTypeDetails details = new BikeTypeDetails();
        details.setStationName(bikeData.getStationName());
        details.setBikeTypes(bikeData.getMetadata().getBikes());
        details.setMunicipality(bikeData.getMetadata().getMunicipality());
        return details;
    }
}
