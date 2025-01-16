package com.weatherAPI.weatherApplication.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTypeDetails;
import com.weatherAPI.weatherApplication.service.BikeService;
/**
 * REST controller for managing bike type data.
 * Provides endpoints to retrieve bike type details and availability by type.
 * 
 * @version 1.0
 * @since 2025-01-04
 */
@RestController
@RequestMapping("/api/bike-types")
@CrossOrigin(origins = "http://localhost:3000")
public class BikeTypeController {

    private final BikeService bikeService;

    /**
     * Constructor to inject the {@link BikeService} dependency.
     *
     * @param bikeService the service handling bike-related operations
     */
    @Autowired
    public BikeTypeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    /**
     * Retrieves all bike type details.
     *
     * @return a list of {@link BikeTypeDetails} objects containing bike type details
     */
    @GetMapping
    public List<BikeTypeDetails> getAllBikeTypeDetails() {
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
     * Retrieves bike type availability for a specified type.
     *
     * @param bikeType the type of bike to filter by
     * @return a list of {@link BikeTypeDetails} objects with availability information
     */
    @GetMapping("/availability")
    public List<BikeTypeDetails> getBikeAvailabilityByType(@RequestParam String bikeType) {
        List<BikeData> bikeDataList = bikeService.getBikeData();
        List<BikeTypeDetails> filteredDetailsList = new ArrayList<>();
        Set<String> seenStations = new HashSet<>();

        for (BikeData bikeData : bikeDataList) {
            if (hasValidMetadata(bikeData) && isBikeTypeAvailable(bikeData, bikeType)) {
                String stationName = bikeData.getStationName();
                if (seenStations.add(stationName)) {
                    filteredDetailsList.add(mapToFilteredBikeTypeDetails(bikeData, bikeType));
                }
            }
        }
        return filteredDetailsList;
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

    /**
     * Checks if a specific bike type is available in a {@link BikeData} object.
     *
     * @param bikeData the bike data to check
     * @param bikeType the type of bike to check for
     * @return {@code true} if the bike type is available, {@code false} otherwise
     */
    private boolean isBikeTypeAvailable(BikeData bikeData, String bikeType) {
        Integer bikeCount = bikeData.getMetadata().getBikes().get(bikeType);
        return bikeCount != null && bikeCount > 0;
    }

    /**
     * Maps a {@link BikeData} object to a filtered {@link BikeTypeDetails} object for a specific bike type.
     *
     * @param bikeData the bike data to map
     * @param bikeType the bike type to include in the mapping
     * @return a {@link BikeTypeDetails} object with filtered details
     */
    private BikeTypeDetails mapToFilteredBikeTypeDetails(BikeData bikeData, String bikeType) {
        BikeTypeDetails details = new BikeTypeDetails();
        details.setStationName(bikeData.getStationName());
        details.setMunicipality(bikeData.getMetadata().getMunicipality());
        details.setBikeTypes(Map.of(bikeType, bikeData.getMetadata().getBikes().get(bikeType)));
        return details;
    }
}
