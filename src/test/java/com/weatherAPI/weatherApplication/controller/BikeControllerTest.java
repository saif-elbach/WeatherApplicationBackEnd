package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.service.BikeService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BikeControllerTest {

    @Mock
    private BikeService bikeService;

    @InjectMocks
    private BikeController bikeController;

    public BikeControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBikes_success() {
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Test Station");

        when(bikeService.getBikeData()).thenReturn(List.of(mockBikeData));

        List<BikeData> response = bikeController.getBikes();

        assertEquals(1, response.size());
        assertEquals("Test Station", response.get(0).getStationName());
    }
}
