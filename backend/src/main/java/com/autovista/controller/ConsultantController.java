package com.autovista.controller;

import com.autovista.dto.request.SendSimulatedReplyRequest;
import com.autovista.dto.request.UpdateDraftReplyRequest;
import com.autovista.dto.request.UpdateLeadStatusRequest;
import com.autovista.dto.response.ApiResponse;
import com.autovista.dto.response.LeadCommunicationResponse;
import com.autovista.dto.response.LeadResponse;
import com.autovista.service.ConsultantLeadService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultant")
public class ConsultantController {

    private final ConsultantLeadService consultantLeadService;

    public ConsultantController(ConsultantLeadService consultantLeadService) {
        this.consultantLeadService = consultantLeadService;
    }

    /**
     * Requirement #4: Consultant views only their assigned leads with status, search, and date filters.
     */
    @GetMapping("/leads")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getAssignedLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        String consultantEmail = authentication.getName();
        List<LeadResponse> leads = consultantLeadService.getAssignedLeads(consultantEmail, status, search, date);
        return ResponseEntity.ok(ApiResponse.ok("Assigned leads retrieved successfully", leads));
    }

    /**
     * Requirement #4: Consultant views single assigned lead.
     */
    @GetMapping("/leads/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getAssignedLeadById(
            @PathVariable Long id,
            Authentication authentication) {
        String consultantEmail = authentication.getName();
        LeadResponse lead = consultantLeadService.getAssignedLeadById(id, consultantEmail);
        return ResponseEntity.ok(ApiResponse.ok("Lead details retrieved successfully", lead));
    }

    /**
     * Requirement #4: Consultant updates lead status with validation and history notes.
     */
    @PatchMapping("/leads/{id}/status")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLeadStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeadStatusRequest request,
            Authentication authentication) {
        String consultantEmail = authentication.getName();
        LeadResponse updated = consultantLeadService.updateLeadStatus(id, request, consultantEmail);
        return ResponseEntity.ok(ApiResponse.ok("Lead status updated successfully", updated));
    }

    /**
     * Requirement #5: AI Buyer Summary.
     */
    @GetMapping("/leads/{id}/ai-summary")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAiSummary(
            @PathVariable Long id,
            Authentication authentication) {
        String consultantEmail = authentication.getName();
        String summary = consultantLeadService.getOrGenerateAiSummary(id, consultantEmail);
        return ResponseEntity.ok(ApiResponse.ok("AI Buyer Summary generated successfully", Map.of("summary", summary)));
    }

    /**
     * Requirement #6: Generate AI First-Reply Draft.
     */
    @PostMapping("/leads/{id}/ai-draft")
    public ResponseEntity<ApiResponse<LeadResponse>> generateAiDraft(
            @PathVariable Long id,
            Authentication authentication) {
        String consultantEmail = authentication.getName();
        LeadResponse updated = consultantLeadService.generateAiDraftReply(id, consultantEmail);
        return ResponseEntity.ok(ApiResponse.ok("AI First-Reply draft generated successfully", updated));
    }

    /**
     * Requirement #6 & #7: Consultant edits the reply draft.
     */
    @PutMapping("/leads/{id}/draft")
    public ResponseEntity<ApiResponse<LeadResponse>> updateDraft(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDraftReplyRequest request,
            Authentication authentication) {
        String consultantEmail = authentication.getName();
        LeadResponse updated = consultantLeadService.updateDraftReply(id, request, consultantEmail);
        return ResponseEntity.ok(ApiResponse.ok("Draft reply updated successfully", updated));
    }

    /**
     * Requirement #7: Consultant explicitly reviews and approves the reply draft.
     */
    @PostMapping("/leads/{id}/approve-draft")
    public ResponseEntity<ApiResponse<LeadResponse>> approveDraft(
            @PathVariable Long id,
            Authentication authentication) {
        String consultantEmail = authentication.getName();
        LeadResponse updated = consultantLeadService.approveDraft(id, consultantEmail);
        return ResponseEntity.ok(ApiResponse.ok("Reply draft approved successfully for dispatch", updated));
    }

    /**
     * Requirement #7: Send simulated communication (blocked if draft not approved or customer opted out).
     */
    @PostMapping("/leads/{id}/send-reply")
    public ResponseEntity<ApiResponse<LeadCommunicationResponse>> sendSimulatedReply(
            @PathVariable Long id,
            @RequestBody(required = false) SendSimulatedReplyRequest request,
            Authentication authentication) {
        String consultantEmail = authentication.getName();
        SendSimulatedReplyRequest req = request != null ? request : new SendSimulatedReplyRequest("WHATSAPP_SIMULATED");
        LeadCommunicationResponse comm = consultantLeadService.sendSimulatedReply(id, req, consultantEmail);
        return ResponseEntity.ok(ApiResponse.ok("Simulated communication dispatched and recorded in audit log", comm));
    }
}
