package com.autovista.controller;

import com.autovista.dto.response.ApiResponse;
import com.autovista.dto.response.CarModelResponse;
import com.autovista.dto.response.CarVariantResponse;
import com.autovista.dto.response.CityPriceResponse;
import com.autovista.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CarModelResponse>>> getAllCarModels() {
        List<CarModelResponse> models = carService.getAllCarModels();
        return ResponseEntity.ok(ApiResponse.ok("Car models retrieved successfully", models));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarModelResponse>> getCarModelById(@PathVariable Long id) {
        CarModelResponse model = carService.getCarModelById(id);
        return ResponseEntity.ok(ApiResponse.ok("Car model details retrieved successfully", model));
    }

    @GetMapping("/variants")
    public ResponseEntity<ApiResponse<List<CarVariantResponse>>> getAllVariants(
            @RequestParam(required = false) String fuelType) {
        List<CarVariantResponse> variants = (fuelType != null && !fuelType.isBlank())
                ? carService.getVariantsByFuelType(fuelType)
                : carService.getAllCarVariants();
        return ResponseEntity.ok(ApiResponse.ok("Car variants retrieved successfully", variants));
    }

    @GetMapping("/variants/{id}")
    public ResponseEntity<ApiResponse<CarVariantResponse>> getVariantById(@PathVariable Long id) {
        CarVariantResponse variant = carService.getVariantById(id);
        return ResponseEntity.ok(ApiResponse.ok("Car variant details retrieved successfully", variant));
    }

    @GetMapping("/variants/{id}/pricing")
    public ResponseEntity<ApiResponse<CityPriceResponse>> getVariantPricing(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Lucknow") String city) {
        CityPriceResponse pricing = carService.getPricingForVariantAndCity(id, city);
        return ResponseEntity.ok(ApiResponse.ok("City on-road pricing retrieved successfully", pricing));
    }
}
