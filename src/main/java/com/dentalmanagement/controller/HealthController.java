package com.dentalmanagement.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller for AWS ECS Fargate & Application Load Balancer
 * Provides lightweight zero-DB liveness and readiness probe responses.
 */
@RestController
@CrossOrigin
public class HealthController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping({"/health", "/api/health", "/"})
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "hospital-system-backend");
        response.put("environment", activeProfile);
        response.put("timestamp", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

