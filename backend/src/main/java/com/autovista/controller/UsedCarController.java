package com.autovista.controller;

import com.autovista.dto.response.ApiResponse;
import com.autovista.dto.response.UsedCarResponse;
import com.autovista.service.UsedCarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/used-cars")
public class UsedCarController {

    private final UsedCarService usedCarService;

    public UsedCarController(UsedCarService usedCarService) {
        this.usedCarService = usedCarService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsedCarResponse>>> getAllUsedCars(
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        List<UsedCarResponse> usedCars = usedCarService.getUsedCarsByStatus(status);
        return ResponseEntity.ok(ApiResponse.ok("Pre-owned vehicle collection retrieved", usedCars));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsedCarResponse>> getUsedCarById(@PathVariable Long id) {
        UsedCarResponse usedCar = usedCarService.getUsedCarById(id);
        return ResponseEntity.ok(ApiResponse.ok("Pre-owned vehicle details retrieved", usedCar));
    }
}
