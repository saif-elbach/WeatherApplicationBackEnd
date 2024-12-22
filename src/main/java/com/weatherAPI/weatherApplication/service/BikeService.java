package com.weatherAPI.weatherApplication.service;

import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import com.weatherAPI.weatherApplication.model.BikeData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class BikeService {

    private final RestTemplate restTemplate;
    private final String API_URL = "https://mobility.api.opendatahub.com/v2/flat/Bicycle";

    public BikeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BikeData> getBikeData() {
        BikeApiResponse response = restTemplate.getForObject(API_URL, BikeApiResponse.class);
        return response != null ? response.getData() : List.of();
    }
}
