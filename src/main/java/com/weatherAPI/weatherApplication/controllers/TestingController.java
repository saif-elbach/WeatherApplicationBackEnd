package com.weatherAPI.weatherApplication.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/testing")
public class TestingController {

    @GetMapping("/message")
    public String getMessage() {
        return "alles gutt";
    }
}