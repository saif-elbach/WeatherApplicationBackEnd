package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.service.BikeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BikeController {

    @Autowired
    private BikeService bikeService;

    @GetMapping("/bikes")
    public List<BikeData> getBikes(){
        return bikeService.getBikeData();
    }
}
