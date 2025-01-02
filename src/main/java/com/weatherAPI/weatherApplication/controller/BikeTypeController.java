package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTypeDetails;
import com.weatherAPI.weatherApplication.service.BikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api/bike-types")
public class BikeTypeController {

    private final BikeService bikeService;

    @Autowired
    public BikeTypeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    /**
     * Endpoint to fetch all bike types and their availability
     *
     * @return List of BikeTypeDetails
     */
    @GetMapping
    public List<BikeTypeDetails> getAllBikeTypeDetails() {
        List<BikeData> bikeDataList = bikeService.getBikeData();
        List<BikeTypeDetails> bikeTypeDetailsList = new ArrayList<>();

        for (BikeData bikeData : bikeDataList) {
            if (bikeData.getMetadata() != null && bikeData.getMetadata().getBikes() != null) {
                BikeTypeDetails details = new BikeTypeDetails();
                details.setStationName(bikeData.getStationName());
                details.setBikeTypes(bikeData.getMetadata().getBikes());
                details.setMunicipality(bikeData.getMetadata().getMunicipality());
                bikeTypeDetailsList.add(details);
            }
        }
        return bikeTypeDetailsList;
    }

    /**
     * Endpoint to fetch availability of a specific bike type at all stations
     *
     * @param bikeType The type of bike to filter by
     * @return Filtered list of BikeTypeDetails
     */
    @GetMapping("/availability")
    public List<BikeTypeDetails> getBikeAvailabilityByType(@RequestParam String bikeType) {
        List<BikeData> bikeDataList = bikeService.getBikeData();
        List<BikeTypeDetails> filteredDetailsList = new ArrayList<>();
        Set<String> seenStations = new HashSet<>();

        for (BikeData bikeData : bikeDataList) {
            if (bikeData.getMetadata() != null && bikeData.getMetadata().getBikes() != null) {
                Integer bikeCount = bikeData.getMetadata().getBikes().get(bikeType);
                if (bikeCount != null && bikeCount > 0) {
                    String stationName = bikeData.getStationName();
                    if (!seenStations.contains(stationName)) { // Avoid duplicates
                        seenStations.add(stationName);
                        BikeTypeDetails details = new BikeTypeDetails();
                        details.setStationName(stationName);
                        details.setMunicipality(bikeData.getMetadata().getMunicipality());
                        details.setBikeTypes(Map.of(bikeType, bikeCount)); // Include only the filtered bike type
                        filteredDetailsList.add(details);
                    }
                }
            }
        }
        return filteredDetailsList;
    }

}
