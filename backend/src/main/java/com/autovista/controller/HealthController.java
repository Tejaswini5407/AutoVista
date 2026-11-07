package com.autovista.controller;

import com.autovista.dto.response.ApiResponse;
import com.autovista.dto.response.HealthResponse;
import com.autovista.service.HealthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HealthResponse>> getHealth() {
        HealthResponse health = healthService.checkHealth();
        HttpStatus status = "UP".equals(health.getStatus()) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(ApiResponse.ok("AutoVista system health status", health), status);
    }
}
