package com.weatherAPI.weatherApplication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherAPI.weatherApplication.model.BikeApiResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BikeDataLogger {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BIKE_API_URL = "https://mobility.api.opendatahub.com/v2/flat/Bicycle";
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final File bikeLogFile = new File("bike_data_log.json");

    @Scheduled(fixedRate = 3600000) // Log every hour
    public void logBikeData() {
        try {
            // Fetch current data
            BikeApiResponse bikeResponse = restTemplate.getForObject(BIKE_API_URL, BikeApiResponse.class);

            // Create a log entry with timestamp and data
            Map<String, Object> entry = new HashMap<>();
            entry.put("timestamp", System.currentTimeMillis()); // Add current timestamp
            entry.put("data", bikeResponse != null ? bikeResponse.getData() : List.of());

            // Debugging: Print entry before saving
            System.out.println("Bike data entry before saving: " + entry);

            // Append to or create the log file
            if (bikeLogFile.exists()) {
                // Read existing logs
                List<Map<String, Object>> existingLogs = objectMapper.readValue(bikeLogFile, List.class);
                existingLogs.add(entry); // Add new entry
                objectMapper.writeValue(bikeLogFile, existingLogs); // Save updated logs
            } else {
                // Create a new log file
                objectMapper.writeValue(bikeLogFile, List.of(entry));
            }

            System.out.println("Bike data logged successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
