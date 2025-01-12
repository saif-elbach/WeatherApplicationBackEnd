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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/bike-types")
public class BikeTypeController {

    private final BikeService bikeService;

    @Autowired
    public BikeTypeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

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

    private boolean hasValidMetadata(BikeData bikeData) {
        return bikeData.getMetadata() != null && bikeData.getMetadata().getBikes() != null;
    }

    private BikeTypeDetails mapToBikeTypeDetails(BikeData bikeData) {
        BikeTypeDetails details = new BikeTypeDetails();
        details.setStationName(bikeData.getStationName());
        details.setBikeTypes(bikeData.getMetadata().getBikes());
        details.setMunicipality(bikeData.getMetadata().getMunicipality());
        return details;
    }

    private boolean isBikeTypeAvailable(BikeData bikeData, String bikeType) {
        Integer bikeCount = bikeData.getMetadata().getBikes().get(bikeType);
        return bikeCount != null && bikeCount > 0;
    }

    private BikeTypeDetails mapToFilteredBikeTypeDetails(BikeData bikeData, String bikeType) {
        BikeTypeDetails details = new BikeTypeDetails();
        details.setStationName(bikeData.getStationName());
        details.setMunicipality(bikeData.getMetadata().getMunicipality());
        details.setBikeTypes(Map.of(bikeType, bikeData.getMetadata().getBikes().get(bikeType)));
        return details;
    }
}
