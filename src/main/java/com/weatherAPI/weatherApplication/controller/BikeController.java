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

    @Autowired
    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @GetMapping
    public List<BikeData> getBikeData() {
        return bikeService.getBikeData();
    }

    @GetMapping("/trends")
    public List<BikeTrend> getTrends() {
        return bikeService.getTrends();
    }

    @GetMapping("/stations")
    public List<Map<String, Object>> getBikeStations() {
        return bikeService.getBikeStations();
    }
    
    @GetMapping("/types")
    public List<BikeTypeDetails> getBikeTypeDetails() {
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
}