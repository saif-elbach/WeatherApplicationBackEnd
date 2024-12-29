package com.weatherAPI.weatherApplication.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestingControllerTest {

    @Test
    void getMessage_success() {
        TestingController controller = new TestingController();

        String response = controller.getMessage();

        assertEquals("alles gutt", response);
    }
}