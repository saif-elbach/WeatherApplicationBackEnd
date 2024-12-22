package com.weatherAPI.weatherApplication.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BikeApiResponse {

    @JsonProperty("offset")
    private int offset;

    @JsonProperty("data")
    private List<BikeData> data;

    @JsonProperty("timestamp")
    private Long timestamp; // New field

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<BikeData> getData() {
        return data;
    }

    public void setData(List<BikeData> data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
