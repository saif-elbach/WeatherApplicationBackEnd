package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.BikeTrend;
import com.weatherAPI.weatherApplication.service.BikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")

public class TrendController {

    private final BikeService bikeService;

    @Autowired
    public TrendController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    // Standalone endpoint to fetch trends
    @GetMapping("/trends")
    public List<BikeTrend> getStandaloneTrends() {
        return bikeService.getTrends();
    }
}
