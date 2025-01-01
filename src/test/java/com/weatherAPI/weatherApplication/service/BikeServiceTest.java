package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class BikeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BikeService bikeService;

    public BikeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBikeData_success() {
        BikeApiResponse mockResponse = new BikeApiResponse();
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Test Station");
        mockResponse.setData(List.of(mockBikeData));

        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        List<BikeData> bikeData = bikeService.getBikeData();

        assertEquals(1, bikeData.size());
        assertEquals("Test Station", bikeData.get(0).getStationName());
    }

    @Test
    void getBikeData_emptyResponse() {
        BikeApiResponse mockResponse = new BikeApiResponse();
        mockResponse.setData(List.of());

        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        List<BikeData> bikeData = bikeService.getBikeData();

        assertTrue(bikeData.isEmpty());
    }

    @Test
    void getBikeData_nullResponse() {
        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(null);

        List<BikeData> bikeData = bikeService.getBikeData();

        assertTrue(bikeData.isEmpty());
    }

    @Test
    void getTrends_addsUniqueTrends() {
        BikeApiResponse mockResponse = new BikeApiResponse();
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Test Station");
        
        BikeData.Coordinate coordinate = new BikeData.Coordinate();
        coordinate.setLongitude(10.0);
        coordinate.setLatitude(20.0);
        mockBikeData.setCoordinate(coordinate);

        BikeData.Metadata metadata = new BikeData.Metadata();
        metadata.setBikes(Map.of("number-available", 5));  
        mockBikeData.setMetadata(metadata);
        
        mockResponse.setData(List.of(mockBikeData));

        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        bikeService.getBikeData();

        List<BikeTrend> trends = bikeService.getTrends();
        assertEquals(1, trends.size());
        assertEquals("Test Station", trends.get(0).getStationName());
    }

    @Test
    void getTrends_doesNotAddDuplicateTrends() {
        BikeApiResponse mockResponse = new BikeApiResponse();
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Test Station");
        
        BikeData.Coordinate coordinate = new BikeData.Coordinate();
        coordinate.setLongitude(10.0);
        coordinate.setLatitude(20.0);
        mockBikeData.setCoordinate(coordinate);

        BikeData.Metadata metadata = new BikeData.Metadata();
        metadata.setBikes(Map.of("number-available", 5));  
        mockBikeData.setMetadata(metadata);
        
        mockResponse.setData(List.of(mockBikeData));

        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        bikeService.getBikeData();

        bikeService.getBikeData();

        List<BikeTrend> trends = bikeService.getTrends();
        assertEquals(1, trends.size());  
    }

    @Test
    void getBikeStations_success() {
        BikeApiResponse mockResponse = new BikeApiResponse();
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Test Station");
        
        BikeData.Coordinate coordinate = new BikeData.Coordinate();
        coordinate.setLongitude(10.0);
        coordinate.setLatitude(20.0);
        mockBikeData.setCoordinate(coordinate);

        BikeData.Metadata metadata = new BikeData.Metadata();
        metadata.setBikes(Map.of("number-available", 5));  
        mockBikeData.setMetadata(metadata);
        
        mockResponse.setData(List.of(mockBikeData));

        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        List<Map<String, Object>> bikeStations = bikeService.getBikeStations();

        assertEquals(1, bikeStations.size());
        assertEquals("Test Station", bikeStations.get(0).get("stationName"));
        assertEquals(5, bikeStations.get(0).get("availableBikes"));
        assertEquals(20.0, bikeStations.get(0).get("latitude"));  
        assertEquals(10.0, bikeStations.get(0).get("longitude")); 
    }

    @Test
    void getBikeStations_noCoordinate() {
        BikeApiResponse mockResponse = new BikeApiResponse();
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Test Station");
        mockBikeData.setCoordinate(null);

        BikeData.Metadata metadata = new BikeData.Metadata();
        metadata.setBikes(Map.of("number-available", 5));  
        mockBikeData.setMetadata(metadata);

        mockResponse.setData(List.of(mockBikeData));

        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        List<Map<String, Object>> bikeStations = bikeService.getBikeStations();

        assertEquals(1, bikeStations.size());
        assertEquals("Test Station", bikeStations.get(0).get("stationName"));
        assertEquals(5, bikeStations.get(0).get("availableBikes"));
        assertEquals(0.0, bikeStations.get(0).get("latitude"));
        assertEquals(0.0, bikeStations.get(0).get("longitude"));
    }

    @Test
    void getBikeStations_emptyResponse() {
        BikeApiResponse mockResponse = new BikeApiResponse();
        mockResponse.setData(List.of());

        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        List<Map<String, Object>> bikeStations = bikeService.getBikeStations();

        assertTrue(bikeStations.isEmpty());
    }
}
