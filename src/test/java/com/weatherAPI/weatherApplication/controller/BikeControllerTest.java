package com.weatherAPI.weatherApplication.controller;

import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;
import com.weatherAPI.weatherApplication.service.BikeService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void getBikes_emptyResponse() {
        when(bikeService.getBikeData()).thenReturn(Collections.emptyList());

        List<BikeData> response = bikeController.getBikes();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getBikes_nullResponse() {
        when(bikeService.getBikeData()).thenReturn(null);

        List<BikeData> response = bikeController.getBikes();

        assertNull(response);
    }



    @Test
    void getTrends_emptyResponse() {
        when(bikeService.getTrends()).thenReturn(Collections.emptyList());

        List<BikeTrend> response = bikeController.getTrends();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getTrends_nullResponse() {
        when(bikeService.getTrends()).thenReturn(null);

        List<BikeTrend> response = bikeController.getTrends();

        assertNull(response);
    }

    @Test
    void getBikes_errorHandling() {
        when(bikeService.getBikeData()).thenThrow(new RuntimeException("Service Error"));

        Exception exception = assertThrows(RuntimeException.class, () -> bikeController.getBikes());

        assertEquals("Service Error", exception.getMessage());
    }

    @Test
    void getTrends_errorHandling() {
        when(bikeService.getTrends()).thenThrow(new RuntimeException("Service Error"));

        Exception exception = assertThrows(RuntimeException.class, () -> bikeController.getTrends());

        assertEquals("Service Error", exception.getMessage());
    }
}
