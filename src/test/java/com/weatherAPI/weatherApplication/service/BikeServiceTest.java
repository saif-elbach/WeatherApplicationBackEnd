package com.weatherAPI.weatherApplication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import com.weatherAPI.weatherApplication.model.BikeData;
import com.weatherAPI.weatherApplication.model.BikeTrend;

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
    
 

    @Test
    void nullMetadataOrBikesMap() {
        BikeApiResponse mockResponse = new BikeApiResponse();
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Station 1");
        mockBikeData.setMetadata(null);

        mockResponse.setData(List.of(mockBikeData));
        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        List<BikeTrend> trends = bikeService.getTrends();
        assertTrue(trends.isEmpty());
    }

    @Test
    void testGetBikeData_whenApiReturnsData() {
        BikeApiResponse apiResponse = new BikeApiResponse();
        apiResponse.setOffset(0);
        apiResponse.setData(List.of(new BikeData()));

        when(restTemplate.getForObject(anyString(), eq(BikeApiResponse.class))).thenReturn(apiResponse);

        List<BikeData> bikeData = bikeService.getBikeData();
        assertNotNull(bikeData);
        assertFalse(bikeData.isEmpty());
    }
  

    @Test
    void testGetBikeStations_whenNoBikesAvailable() {
        BikeData bikeData = new BikeData();
        bikeData.setStationName("Station6");
        BikeData.Metadata metadata = new BikeData.Metadata();
        metadata.setBikes(Map.of("number-available", 0));
        bikeData.setMetadata(metadata);
        bikeData.setCoordinate(new BikeData.Coordinate());

        List<BikeData> bikeDataList = List.of(bikeData);
        BikeApiResponse apiResponse = new BikeApiResponse();
        apiResponse.setData(bikeDataList);

        when(restTemplate.getForObject(anyString(), eq(BikeApiResponse.class))).thenReturn(apiResponse);
        
        bikeService.getBikeData();

        List<Map<String, Object>> stations = bikeService.getBikeStations();
        assertNotNull(stations);
        assertEquals(1, stations.size());
        assertEquals(0, stations.get(0).get("availableBikes"));
    }

    @Test
    void testSaveBikeTrends_withStationNameNull() {
        BikeData bikeData = new BikeData();
        bikeData.setStationName(null); // null station name
        BikeData.Metadata metadata = new BikeData.Metadata();
        metadata.setBikes(Map.of("number-available", 3));
        bikeData.setMetadata(metadata);
        bikeData.setCoordinate(new BikeData.Coordinate());

        List<BikeData> bikeDataList = List.of(bikeData);
        BikeApiResponse apiResponse = new BikeApiResponse();
        apiResponse.setData(bikeDataList);

        when(restTemplate.getForObject(anyString(), eq(BikeApiResponse.class))).thenReturn(apiResponse);
        
        bikeService.getBikeData();

        List<BikeTrend> trends = bikeService.getTrends();
        assertNotNull(trends);
        assertTrue(trends.isEmpty()); // No trend should be saved for station with null name
    }

    @Test
    void testSaveBikeTrends_whenStationNameIsDuplicate() {
        BikeData bikeData1 = new BikeData();
        bikeData1.setStationName("Station7");
        BikeData.Metadata metadata1 = new BikeData.Metadata();
        metadata1.setBikes(Map.of("number-available", 5));
        bikeData1.setMetadata(metadata1);
        bikeData1.setCoordinate(new BikeData.Coordinate());

        BikeData bikeData2 = new BikeData();
        bikeData2.setStationName("Station7");
        BikeData.Metadata metadata2 = new BikeData.Metadata();
        metadata2.setBikes(Map.of("number-available", 5));
        bikeData2.setMetadata(metadata2);
        bikeData2.setCoordinate(new BikeData.Coordinate());

        List<BikeData> bikeDataList = List.of(bikeData1, bikeData2);
        BikeApiResponse apiResponse = new BikeApiResponse();
        apiResponse.setData(bikeDataList);

        when(restTemplate.getForObject(anyString(), eq(BikeApiResponse.class))).thenReturn(apiResponse);
        
        bikeService.getBikeData();

        List<BikeTrend> trends = bikeService.getTrends();
        assertNotNull(trends);
        assertEquals(1, trends.size()); // Only one trend should be saved since station name is duplicate
    }

    @Test
    void testGetBikeData_whenStationHasNoCoordinate() {
        BikeData bikeData = new BikeData();
        bikeData.setStationName("Station8");
        BikeData.Metadata metadata = new BikeData.Metadata();
        metadata.setBikes(Map.of("number-available", 3));
        bikeData.setMetadata(metadata);
        bikeData.setCoordinate(null); // No coordinate

        List<BikeData> bikeDataList = List.of(bikeData);
        BikeApiResponse apiResponse = new BikeApiResponse();
        apiResponse.setData(bikeDataList);

        when(restTemplate.getForObject(anyString(), eq(BikeApiResponse.class))).thenReturn(apiResponse);
        
        bikeService.getBikeData();

        List<Map<String, Object>> stations = bikeService.getBikeStations();
        assertNotNull(stations);
        assertEquals(1, stations.size());
        assertEquals(0.0, stations.get(0).get("latitude"));
        assertEquals(0.0, stations.get(0).get("longitude"));
    }

    @Test
    void testGetBikeStations_withInvalidLatitudeLongitude() {
        BikeData bikeData = new BikeData();
        bikeData.setStationName("Station9");
        BikeData.Metadata metadata = new BikeData.Metadata();
        metadata.setBikes(Map.of("number-available", 2));
        bikeData.setMetadata(metadata);

        BikeData.Coordinate coordinate = new BikeData.Coordinate();
        coordinate.setLatitude(999.0); // Invalid latitude
        coordinate.setLongitude(999.0); // Invalid longitude
        bikeData.setCoordinate(coordinate);

        List<BikeData> bikeDataList = List.of(bikeData);
        BikeApiResponse apiResponse = new BikeApiResponse();
        apiResponse.setData(bikeDataList);

        when(restTemplate.getForObject(anyString(), eq(BikeApiResponse.class))).thenReturn(apiResponse);
        
        bikeService.getBikeData();

        List<Map<String, Object>> stations = bikeService.getBikeStations();
        assertNotNull(stations);
        assertEquals(1, stations.size());
        assertEquals(999.0, stations.get(0).get("latitude"));
        assertEquals(999.0, stations.get(0).get("longitude"));
    }

    @Test
    void testGetBikeData_whenApiReturnsEmptyStationList() {
        BikeApiResponse apiResponse = new BikeApiResponse();
        apiResponse.setOffset(0);
        apiResponse.setData(List.of()); // Empty list

        when(restTemplate.getForObject(anyString(), eq(BikeApiResponse.class))).thenReturn(apiResponse);

        List<BikeData> bikeData = bikeService.getBikeData();
        assertNotNull(bikeData);
        assertTrue(bikeData.isEmpty());
    }
    
   
}
