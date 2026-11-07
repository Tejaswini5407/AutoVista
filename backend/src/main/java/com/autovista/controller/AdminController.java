package com.autovista.controller;

import com.autovista.dto.request.CarModelRequest;
import com.autovista.dto.request.CarVariantRequest;
import com.autovista.dto.request.CityPriceRequest;
import com.autovista.dto.request.ReassignLeadRequest;
import com.autovista.dto.request.UsedCarRequest;
import com.autovista.dto.response.*;
import com.autovista.entity.UsedCarStatus;
import com.autovista.service.AdminLeadService;
import com.autovista.service.AdminService;
import com.autovista.service.MockNotificationService;
import com.autovista.service.TestDriveBookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final TestDriveBookingService bookingService;
    private final MockNotificationService notificationService;
    private final AdminLeadService adminLeadService;

    public AdminController(
            AdminService adminService,
            TestDriveBookingService bookingService,
            MockNotificationService notificationService,
            AdminLeadService adminLeadService) {
        this.adminService = adminService;
        this.bookingService = bookingService;
        this.notificationService = notificationService;
        this.adminLeadService = adminLeadService;
    }

    // ==========================================
    // 1. DASHBOARD STATS
    // ==========================================
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getDashboardStats() {
        AdminStatsResponse stats = adminService.getAdminStats();
        return ResponseEntity.ok(ApiResponse.ok("Admin stats retrieved successfully", stats));
    }

    // ==========================================
    // 2. NEW CAR INVENTORY MANAGEMENT
    // ==========================================
    @GetMapping("/cars")
    public ResponseEntity<ApiResponse<List<CarModelResponse>>> getAllModels() {
        List<CarModelResponse> models = adminService.getAllModels();
        return ResponseEntity.ok(ApiResponse.ok("All car models retrieved successfully", models));
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<ApiResponse<CarModelResponse>> getModelById(@PathVariable Long id) {
        CarModelResponse model = adminService.getModelById(id);
        return ResponseEntity.ok(ApiResponse.ok("Car model retrieved successfully", model));
    }

    @PostMapping("/cars")
    public ResponseEntity<ApiResponse<CarModelResponse>> createModel(@Valid @RequestBody CarModelRequest request) {
        CarModelResponse created = adminService.createModel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Car model created successfully", created));
    }

    @PutMapping("/cars/{id}")
    public ResponseEntity<ApiResponse<CarModelResponse>> updateModel(
            @PathVariable Long id,
            @Valid @RequestBody CarModelRequest request) {
        CarModelResponse updated = adminService.updateModel(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Car model updated successfully", updated));
    }

    @PatchMapping("/cars/{id}/status")
    public ResponseEntity<ApiResponse<CarModelResponse>> toggleModelStatus(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean active,
            @RequestBody(required = false) Map<String, Boolean> body) {
        Boolean targetActive = active;
        if (targetActive == null && body != null && body.containsKey("active")) {
            targetActive = body.get("active");
        }
        CarModelResponse updated = adminService.toggleModelStatus(id, targetActive);
        return ResponseEntity.ok(ApiResponse.ok("Car model status updated successfully", updated));
    }

    @PostMapping("/cars/{modelId}/variants")
    public ResponseEntity<ApiResponse<CarVariantResponse>> createVariant(
            @PathVariable Long modelId,
            @Valid @RequestBody CarVariantRequest request) {
        CarVariantResponse created = adminService.createVariant(modelId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Car variant created successfully", created));
    }

    @PutMapping("/cars/variants/{variantId}")
    public ResponseEntity<ApiResponse<CarVariantResponse>> updateVariant(
            @PathVariable Long variantId,
            @Valid @RequestBody CarVariantRequest request) {
        CarVariantResponse updated = adminService.updateVariant(variantId, request);
        return ResponseEntity.ok(ApiResponse.ok("Car variant updated successfully", updated));
    }

    @PatchMapping("/cars/variants/{variantId}/status")
    public ResponseEntity<ApiResponse<CarVariantResponse>> toggleVariantStatus(
            @PathVariable Long variantId,
            @RequestParam(required = false) Boolean available,
            @RequestBody(required = false) Map<String, Boolean> body) {
        Boolean targetAvailable = available;
        if (targetAvailable == null && body != null && body.containsKey("available")) {
            targetAvailable = body.get("available");
        }
        CarVariantResponse updated = adminService.toggleVariantStatus(variantId, targetAvailable);
        return ResponseEntity.ok(ApiResponse.ok("Car variant status updated successfully", updated));
    }

    // ==========================================
    // 3. USED CAR INVENTORY MANAGEMENT
    // ==========================================
    @GetMapping("/used-cars")
    public ResponseEntity<ApiResponse<List<AdminUsedCarResponse>>> getAllUsedCars() {
        List<AdminUsedCarResponse> usedCars = adminService.getAllUsedCars();
        return ResponseEntity.ok(ApiResponse.ok("All used cars retrieved successfully", usedCars));
    }

    @GetMapping("/used-cars/{id}")
    public ResponseEntity<ApiResponse<AdminUsedCarResponse>> getUsedCarById(@PathVariable Long id) {
        AdminUsedCarResponse usedCar = adminService.getUsedCarById(id);
        return ResponseEntity.ok(ApiResponse.ok("Used car retrieved successfully", usedCar));
    }

    @PostMapping("/used-cars")
    public ResponseEntity<ApiResponse<AdminUsedCarResponse>> createUsedCar(@Valid @RequestBody UsedCarRequest request) {
        AdminUsedCarResponse created = adminService.createUsedCar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Used car added to inventory successfully", created));
    }

    @PutMapping("/used-cars/{id}")
    public ResponseEntity<ApiResponse<AdminUsedCarResponse>> updateUsedCar(
            @PathVariable Long id,
            @Valid @RequestBody UsedCarRequest request) {
        AdminUsedCarResponse updated = adminService.updateUsedCar(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Used car updated successfully", updated));
    }

    @PatchMapping("/used-cars/{id}/status")
    public ResponseEntity<ApiResponse<AdminUsedCarResponse>> updateUsedCarStatus(
            @PathVariable Long id,
            @RequestParam(required = false) UsedCarStatus status,
            @RequestBody(required = false) Map<String, String> body) {
        UsedCarStatus targetStatus = status;
        if (targetStatus == null && body != null && body.containsKey("status")) {
            targetStatus = UsedCarStatus.valueOf(body.get("status").toUpperCase());
        }
        if (targetStatus == null) {
            targetStatus = UsedCarStatus.AVAILABLE;
        }
        AdminUsedCarResponse updated = adminService.updateUsedCarStatus(id, targetStatus);
        return ResponseEntity.ok(ApiResponse.ok("Used car status updated successfully", updated));
    }

    // ==========================================
    // 4. CITY-WISE PRICING MANAGEMENT (PRICING ENGINE)
    // ==========================================
    @GetMapping("/pricing")
    public ResponseEntity<ApiResponse<List<CityPriceResponse>>> getAllCityPrices() {
        List<CityPriceResponse> prices = adminService.getAllCityPrices();
        return ResponseEntity.ok(ApiResponse.ok("All city pricing rules retrieved successfully", prices));
    }

    @PostMapping("/pricing")
    public ResponseEntity<ApiResponse<CityPriceResponse>> createOrUpdateCityPrice(
            @Valid @RequestBody CityPriceRequest request) {
        CityPriceResponse created = adminService.createCityPrice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("City pricing rule saved and calculated successfully", created));
    }

    @PutMapping("/pricing/{id}")
    public ResponseEntity<ApiResponse<CityPriceResponse>> updateCityPrice(
            @PathVariable Long id,
            @Valid @RequestBody CityPriceRequest request) {
        CityPriceResponse updated = adminService.updateCityPrice(id, request);
        return ResponseEntity.ok(ApiResponse.ok("City pricing rule updated and recalculated successfully", updated));
    }

    // ==========================================
    // 5. TEST DRIVE BOOKING MANAGEMENT
    // ==========================================
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<com.autovista.dto.response.BookingResponse>>> getAllBookings(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(required = false) Long showroomId,
            @RequestParam(required = false) String status) {
        List<com.autovista.dto.response.BookingResponse> bookings = bookingService.getAllBookings(date, showroomId, status);
        return ResponseEntity.ok(ApiResponse.ok("All test drive bookings retrieved successfully", bookings));
    }

    @PatchMapping("/bookings/{id}/status")
    public ResponseEntity<ApiResponse<com.autovista.dto.response.BookingResponse>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {
        String targetStatus = status;
        String notes = null;
        if (body != null) {
            if (targetStatus == null && body.containsKey("status")) {
                targetStatus = body.get("status");
            }
            if (body.containsKey("notes")) {
                notes = body.get("notes");
            }
        }
        if (targetStatus == null || targetStatus.isBlank()) {
            targetStatus = "CONFIRMED";
        }
        BookingResponse updated = bookingService.updateBookingStatus(id, targetStatus, notes);
        return ResponseEntity.ok(ApiResponse.ok("Booking status updated successfully", updated));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<MockNotificationResponse>>> getAllNotifications() {
        List<MockNotificationResponse> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(ApiResponse.ok("All simulated notification records retrieved", notifications));
    }

    // ==========================================
    // 6. LEAD MANAGEMENT & ASSIGNMENT (PHASE 6)
    // ==========================================
    @GetMapping("/leads")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getAllLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long consultantId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean unassignedOnly,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LeadResponse> leads = adminLeadService.getAllLeads(status, consultantId, search, unassignedOnly, date);
        return ResponseEntity.ok(ApiResponse.ok("Dealership leads retrieved successfully", leads));
    }

    @GetMapping("/leads/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadDetails(@PathVariable Long id) {
        LeadResponse lead = adminLeadService.getLeadDetails(id);
        return ResponseEntity.ok(ApiResponse.ok("Lead details retrieved successfully", lead));
    }

    @PostMapping("/leads/{id}/reassign")
    public ResponseEntity<ApiResponse<LeadResponse>> reassignLead(
            @PathVariable Long id,
            @Valid @RequestBody ReassignLeadRequest request,
            Authentication authentication) {
        String adminEmail = authentication.getName();
        LeadResponse updated = adminLeadService.reassignLead(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.ok("Lead reassigned successfully", updated));
    }

    @GetMapping("/leads/{id}/history")
    public ResponseEntity<ApiResponse<List<LeadStatusHistoryResponse>>> getLeadStatusHistory(@PathVariable Long id) {
        List<LeadStatusHistoryResponse> history = adminLeadService.getLeadStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.ok("Lead status history retrieved successfully", history));
    }

    @GetMapping("/leads/{id}/communications")
    public ResponseEntity<ApiResponse<List<LeadCommunicationResponse>>> getLeadCommunications(@PathVariable Long id) {
        List<LeadCommunicationResponse> comms = adminLeadService.getLeadCommunications(id);
        return ResponseEntity.ok(ApiResponse.ok("Lead communication audit history retrieved successfully", comms));
    }

    @GetMapping("/leads/overview")
    public ResponseEntity<ApiResponse<AdminLeadOverviewResponse>> getLeadOverview() {
        AdminLeadOverviewResponse overview = adminLeadService.getLeadOverview();
        return ResponseEntity.ok(ApiResponse.ok("Dealership lead overview metrics retrieved successfully", overview));
    }
}
