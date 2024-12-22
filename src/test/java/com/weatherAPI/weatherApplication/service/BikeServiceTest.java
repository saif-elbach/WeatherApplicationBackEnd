package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import com.weatherAPI.weatherApplication.model.BikeData;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        // Mock API response
        BikeApiResponse mockResponse = new BikeApiResponse();
        BikeData mockBikeData = new BikeData();
        mockBikeData.setStationName("Test Station");
        mockResponse.setData(List.of(mockBikeData));

        // Mock RestTemplate behavior
        when(restTemplate.getForObject(
            "https://mobility.api.opendatahub.com/v2/flat/Bicycle", BikeApiResponse.class
        )).thenReturn(mockResponse);

        // Service call
        List<BikeData> bikeData = bikeService.getBikeData();

        // Assertions
        assertEquals(1, bikeData.size());
        assertEquals("Test Station", bikeData.get(0).getStationName());
    }
}
