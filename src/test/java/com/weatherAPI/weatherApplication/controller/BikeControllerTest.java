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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BikeControllerTest {

    @Mock
    private BikeService bikeService;

    @InjectMocks
    private BikeController bikeController;

    public BikeControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBikeData_success() {
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Test Station");

        when(bikeService.getBikeData()).thenReturn(List.of(mockBikeData));

        List<BikeData> response = bikeController.getBikeData();

        assertEquals(1, response.size());
        assertEquals("Test Station", response.get(0).getStationName());
    }

    @Test
    void getBikeData_emptyResponse() {
        when(bikeService.getBikeData()).thenReturn(Collections.emptyList());

        List<BikeData> response = bikeController.getBikeData();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getBikeData_nullResponse() {
        when(bikeService.getBikeData()).thenReturn(null);

        List<BikeData> response = bikeController.getBikeData();

        assertNull(response);
    }

    @Test
    void getBikeData_serviceThrowsException() {
        when(bikeService.getBikeData()).thenThrow(new RuntimeException("Service error"));

        Exception exception = assertThrows(RuntimeException.class, () -> bikeController.getBikeData());

        assertEquals("Service error", exception.getMessage());
    }

    @Test
    void getTrends_success() {
        BikeTrend mockTrend = new BikeTrend();
        mockTrend.setStationName("Trend Station");

        when(bikeService.getTrends()).thenReturn(List.of(mockTrend));

        List<BikeTrend> response = bikeController.getTrends();

        assertEquals(1, response.size());
        assertEquals("Trend Station", response.get(0).getStationName());
    }

    @Test
    void getTrends_emptyResponse() {
        when(bikeService.getTrends()).thenReturn(Collections.emptyList());

        List<BikeTrend> response = bikeController.getTrends();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getBikeStations_success() {
        Map<String, Object> mockStation = Map.of(
                "stationName", "Station A",
                "availableBikes", 5,
                "latitude", 46.0,
                "longitude", 11.0
        );

        when(bikeService.getBikeStations()).thenReturn(List.of(mockStation));

        List<Map<String, Object>> response = bikeController.getBikeStations();

        assertEquals(1, response.size());
        assertEquals("Station A", response.get(0).get("stationName"));
        assertEquals(5, response.get(0).get("availableBikes"));
    }

  


    @Test
    void getBikeTypeDetails_noMetadata() {
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Station Type");

        when(bikeService.getBikeData()).thenReturn(List.of(mockBikeData));

        var response = bikeController.getBikeTypeDetails();

        assertEquals(0, response.size());
    }

    @Test
    void getBikeData_mixedResponse() {
        BikeData validBikeData = new BikeData();
        validBikeData.setStationName("Valid Station");

        BikeData invalidBikeData = new BikeData();

        when(bikeService.getBikeData()).thenReturn(List.of(validBikeData, invalidBikeData));

        List<BikeData> response = bikeController.getBikeData();

        assertEquals(2, response.size());
        assertNull(response.get(1).getStationName());
    }
    @Test
    void getBikeData_partialDataValidation() {
        BikeData validBikeData = new BikeData();
        validBikeData.setStationName("Station A");

        BikeData invalidBikeData = new BikeData(); // Missing station name

        when(bikeService.getBikeData()).thenReturn(List.of(validBikeData, invalidBikeData));

        List<BikeData> response = bikeController.getBikeData();

        assertEquals(2, response.size());
        assertEquals("Station A", response.get(0).getStationName());
        assertNull(response.get(1).getStationName());
    }

    @Test
    void getTrends_nullResponse() {
        when(bikeService.getTrends()).thenReturn(null);

        List<BikeTrend> response = bikeController.getTrends();

        assertNull(response);
    }

    @Test
    void getTrends_serviceThrowsException() {
        when(bikeService.getTrends()).thenThrow(new RuntimeException("Trend service error"));

        Exception exception = assertThrows(RuntimeException.class, () -> bikeController.getTrends());

        assertEquals("Trend service error", exception.getMessage());
    }

    @Test
    void getBikeStations_multipleStations() {
        Map<String, Object> station1 = Map.of(
                "stationName", "Station A",
                "availableBikes", 5,
                "latitude", 46.1,
                "longitude", 11.1
        );

        Map<String, Object> station2 = Map.of(
                "stationName", "Station B",
                "availableBikes", 10,
                "latitude", 47.2,
                "longitude", 12.2
        );

        when(bikeService.getBikeStations()).thenReturn(List.of(station1, station2));

        List<Map<String, Object>> response = bikeController.getBikeStations();

        assertEquals(2, response.size());
        assertEquals("Station A", response.get(0).get("stationName"));
        assertEquals(10, response.get(1).get("availableBikes"));
    }

    @Test
    void getBikeData_duplicateEntries() {
        BikeData duplicateBikeData = new BikeData();
        duplicateBikeData.setStationName("Duplicate Station");

        when(bikeService.getBikeData()).thenReturn(List.of(duplicateBikeData, duplicateBikeData));

        List<BikeData> response = bikeController.getBikeData();

        assertEquals(2, response.size());
        assertEquals("Duplicate Station", response.get(0).getStationName());
        assertEquals("Duplicate Station", response.get(1).getStationName());
    }


}
