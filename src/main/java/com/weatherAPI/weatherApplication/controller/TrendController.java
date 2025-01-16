package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.BikeTrend;
import com.weatherAPI.weatherApplication.service.BikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * REST controller for managing bike trend data.
 * 
 * <p>This controller provides an API endpoint for retrieving bike trends data. 
 * It delegates the business logic to the {@link BikeService}.
 * </p>
 */
@RestController
@RequestMapping("/api")
public class TrendController {

    private final BikeService bikeService;

    /**
     * Constructor to inject dependencies.
     *
     * @param bikeService the service handling bike data operations
     */
    @Autowired
    public TrendController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    /**
     * Retrieves the standalone bike trends data.
     *
     * @return a list of {@link BikeTrend} objects representing the trends
     */
    @GetMapping("/trends")
    public List<BikeTrend> getStandaloneTrends() {
        return bikeService.getTrends();
    }
}
