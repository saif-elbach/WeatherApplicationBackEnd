package com.weatherAPI.weatherApplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BikeApiResponse {

    @JsonProperty("offset")
    private int offset;

    @JsonProperty("data")
    private List<BikeData> data;

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
}
