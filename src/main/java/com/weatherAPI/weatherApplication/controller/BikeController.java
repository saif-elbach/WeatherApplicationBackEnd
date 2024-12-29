package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;
import com.weatherAPI.weatherApplication.service.BikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BikeController {

    @Autowired
    private BikeService bikeService;

    @GetMapping("/bikes")
    public List<BikeData> getBikes() {
        return bikeService.getBikeData(); 
    }

    @GetMapping("/trends")
    public List<BikeTrend> getTrends() {
        return bikeService.getTrends(); 
    }
}
