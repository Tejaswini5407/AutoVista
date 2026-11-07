package com.autovista.controller;

import com.autovista.dto.request.CreateLeadRequest;
import com.autovista.dto.response.ApiResponse;
import com.autovista.dto.response.LeadResponse;
import com.autovista.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    /**
     * Requirement #2: Customer creates vehicle enquiry with mandatory explicit consent.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(
            @Valid @RequestBody CreateLeadRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : request.getCustomerEmail();
        LeadResponse response = leadService.createLead(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Vehicle enquiry submitted successfully. A sales specialist will connect with you.", response));
    }

    /**
     * Requirement #2 & #10: Customer views their own submitted enquiries.
     */
    @GetMapping("/my-leads")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getMyLeads(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        List<LeadResponse> leads = leadService.getCustomerLeads(email);
        return ResponseEntity.ok(ApiResponse.ok("Customer enquiries retrieved successfully", leads));
    }

    /**
     * Customer views a specific enquiry details (with ownership isolation).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadById(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isConsultant = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONSULTANT"));

        LeadResponse lead = leadService.getLeadById(id, email, isAdmin, isConsultant);
        return ResponseEntity.ok(ApiResponse.ok("Enquiry details retrieved successfully", lead));
    }

    /**
     * Requirement #2: Customer opt-out from further marketing and contact communications.
     */
    @PatchMapping("/{id}/opt-out")
    public ResponseEntity<ApiResponse<LeadResponse>> updateOptOut(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean optOut,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        LeadResponse updated = leadService.updateOptOut(id, optOut, email, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Communication preferences updated successfully", updated));
    }
}
