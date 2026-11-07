package com.autovista.controller;

import com.autovista.dto.request.CreateBookingRequest;
import com.autovista.dto.response.ApiResponse;
import com.autovista.dto.response.BookingResponse;
import com.autovista.dto.response.MockNotificationResponse;
import com.autovista.dto.response.SlotAvailabilityResponse;
import com.autovista.security.UserPrincipal;
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

@RestController
@RequestMapping("/api/test-drives")
public class TestDriveBookingController {

    private final TestDriveBookingService bookingService;
    private final MockNotificationService notificationService;

    public TestDriveBookingController(
            TestDriveBookingService bookingService,
            MockNotificationService notificationService) {
        this.bookingService = bookingService;
        this.notificationService = notificationService;
    }

    /**
     * Requirement #3: Check available time slots for a showroom and date.
     */
    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<SlotAvailabilityResponse>> getAvailableSlots(
            @RequestParam(defaultValue = "1") Long showroomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now().plusDays(1);
        SlotAvailabilityResponse availability = bookingService.getSlotAvailability(showroomId, queryDate);
        return ResponseEntity.ok(ApiResponse.ok("Slot availability retrieved successfully", availability));
    }

    /**
     * Requirements #2, #4 & #6: Customer test drive booking creation with database-level conflict prevention.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            Authentication authentication) {
        String email = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : request.getCustomerEmail();
        BookingResponse booking = bookingService.createBooking(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("VIP test drive confirmed and scheduled successfully", booking));
    }

    /**
     * Requirement #6: Customer booking history. Only own bookings are returned.
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        List<BookingResponse> bookings = bookingService.getCustomerBookings(email);
        return ResponseEntity.ok(ApiResponse.ok("Customer bookings retrieved successfully", bookings));
    }

    /**
     * View individual booking details (owner or admin).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        BookingResponse booking = bookingService.getBookingById(id, email, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Booking details retrieved successfully", booking));
    }

    /**
     * Cancel booking by owner or admin.
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        BookingResponse booking = bookingService.cancelCustomerBooking(id, reason, email, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled successfully", booking));
    }

    /**
     * Requirement #7: Mock notification audit history for a booking (strictly isolated to owner or admin).
     */
    @GetMapping("/{id}/notifications")
    public ResponseEntity<ApiResponse<List<MockNotificationResponse>>> getBookingNotifications(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        bookingService.getBookingById(id, email, isAdmin);
        List<MockNotificationResponse> notifications = notificationService.getNotificationsByBookingId(id);
        return ResponseEntity.ok(ApiResponse.ok("Simulated notification logs retrieved", notifications));
    }
}
