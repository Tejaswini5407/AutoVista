package com.autovista.service;

import com.autovista.dto.request.SendSimulatedReplyRequest;
import com.autovista.dto.request.UpdateDraftReplyRequest;
import com.autovista.dto.request.UpdateLeadStatusRequest;
import com.autovista.dto.response.LeadCommunicationResponse;
import com.autovista.dto.response.LeadResponse;
import com.autovista.entity.Lead;
import com.autovista.entity.LeadCommunication;
import com.autovista.entity.LeadStatusHistory;
import com.autovista.entity.User;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.LeadCommunicationRepository;
import com.autovista.repository.LeadRepository;
import com.autovista.repository.LeadStatusHistoryRepository;
import com.autovista.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultantLeadService {

    private static final Logger log = LoggerFactory.getLogger(ConsultantLeadService.class);

    private final LeadRepository leadRepository;
    private final LeadStatusHistoryRepository statusHistoryRepository;
    private final LeadCommunicationRepository communicationRepository;
    private final UserRepository userRepository;
    private final AiLeadService aiLeadService;
    private final CarMapper carMapper;

    // Allowed status transitions
    private static final Set<String> VALID_STATUSES = Set.of(
            "NEW", "CONTACTED", "FOLLOW_UP", "TEST_DRIVE_SCHEDULED", "CONVERTED", "LOST"
    );

    public ConsultantLeadService(
            LeadRepository leadRepository,
            LeadStatusHistoryRepository statusHistoryRepository,
            LeadCommunicationRepository communicationRepository,
            UserRepository userRepository,
            AiLeadService aiLeadService,
            CarMapper carMapper) {
        this.leadRepository = leadRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.communicationRepository = communicationRepository;
        this.userRepository = userRepository;
        this.aiLeadService = aiLeadService;
        this.carMapper = carMapper;
    }

    /**
     * Requirement #4: Consultant Lead Workspace.
     * Consultants can view ONLY their assigned leads.
     */
    @Transactional(readOnly = true)
    public List<LeadResponse> getAssignedLeads(String consultantEmail, String status, String searchTerm, LocalDate date) {
        User consultant = userRepository.findByEmail(consultantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", consultantEmail));

        List<Lead> leads = leadRepository.findByLastAssignedConsultantIdOrderByCreatedAtDesc(consultant.getId());

        return leads.stream()
                .filter(l -> status == null || status.isBlank() || status.equalsIgnoreCase("ALL") || l.getStatus().equalsIgnoreCase(status))
                .filter(l -> {
                    if (searchTerm == null || searchTerm.isBlank()) return true;
                    String term = searchTerm.toLowerCase().trim();
                    return l.getLeadRef().toLowerCase().contains(term) ||
                            l.getCustomerName().toLowerCase().contains(term) ||
                            l.getCustomerPhone().contains(term) ||
                            l.getCustomerEmail().toLowerCase().contains(term);
                })
                .filter(l -> date == null || l.getCreatedAt().toLocalDate().isEqual(date))
                .map(carMapper::toLeadResponse)
                .collect(Collectors.toList());
    }

    /**
     * Requirement #4 & #10: View single assigned lead with strict consultant ownership isolation.
     */
    @Transactional(readOnly = true)
    public LeadResponse getAssignedLeadById(Long leadId, String consultantEmail) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        validateConsultantAccess(lead, consultantEmail);
        return carMapper.toLeadResponse(lead);
    }

    /**
     * Requirement #4: Update lead status with transition validation and history tracking.
     */
    public LeadResponse updateLeadStatus(Long leadId, UpdateLeadStatusRequest request, String consultantEmail) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        User consultant = validateConsultantAccess(lead, consultantEmail);

        String fromStatus = lead.getStatus();
        String toStatus = request.getStatus().trim().toUpperCase();

        if (!VALID_STATUSES.contains(toStatus)) {
            throw new IllegalArgumentException("Invalid target lead status: " + toStatus +
                    ". Valid statuses: " + VALID_STATUSES);
        }

        // Validate Status Transitions
        validateStatusTransition(fromStatus, toStatus);

        lead.setStatus(toStatus);
        Lead saved = leadRepository.saveAndFlush(lead);

        // Record History
        LeadStatusHistory history = new LeadStatusHistory(saved, consultant, fromStatus, toStatus, request.getNotes());
        statusHistoryRepository.save(history);

        log.info("Lead {} status transitioned from {} to {} by {}", saved.getLeadRef(), fromStatus, toStatus, consultantEmail);
        return carMapper.toLeadResponse(saved);
    }

    /**
     * Requirement #5: AI Buyer Summary retrieval or generation.
     */
    public String getOrGenerateAiSummary(Long leadId, String consultantEmail) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        validateConsultantAccess(lead, consultantEmail);

        if (lead.getAiSummary() != null && !lead.getAiSummary().isBlank()) {
            return lead.getAiSummary();
        }

        String summary = aiLeadService.generateBuyerSummary(lead);
        lead.setAiSummary(summary);
        leadRepository.saveAndFlush(lead);
        return summary;
    }

    /**
     * Requirement #6: AI First-Reply Draft generation.
     */
    public LeadResponse generateAiDraftReply(Long leadId, String consultantEmail) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        validateConsultantAccess(lead, consultantEmail);

        String draft = aiLeadService.generateFirstReplyDraft(lead);
        lead.setAiDraftReply(draft);
        lead.setConsultantApproved(false); // Reset approval on regeneration
        Lead saved = leadRepository.saveAndFlush(lead);
        return carMapper.toLeadResponse(saved);
    }

    /**
     * Requirement #6 & #7: Consultant edits the draft reply text.
     */
    public LeadResponse updateDraftReply(Long leadId, UpdateDraftReplyRequest request, String consultantEmail) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        validateConsultantAccess(lead, consultantEmail);

        lead.setAiDraftReply(request.getDraftReply().trim());
        lead.setConsultantApproved(false); // Reset approval when edited
        Lead saved = leadRepository.saveAndFlush(lead);
        return carMapper.toLeadResponse(saved);
    }

    /**
     * Requirement #7: Consultant explicitly reviews and approves the draft.
     */
    public LeadResponse approveDraft(Long leadId, String consultantEmail) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        validateConsultantAccess(lead, consultantEmail);

        if (lead.getAiDraftReply() == null || lead.getAiDraftReply().isBlank()) {
            throw new IllegalStateException("Cannot approve an empty reply draft. Please generate or enter draft content first.");
        }

        lead.setConsultantApproved(true);
        Lead saved = leadRepository.saveAndFlush(lead);
        return carMapper.toLeadResponse(saved);
    }

    /**
     * Requirement #7: Send simulated communication.
     * STRICT RULES:
     * - An unapproved draft must NEVER be sent!
     * - Opted-out customers must NOT receive communication.
     * - Dispatches simulated message and records audit log.
     */
    public LeadCommunicationResponse sendSimulatedReply(Long leadId, SendSimulatedReplyRequest request, String consultantEmail) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        User consultant = validateConsultantAccess(lead, consultantEmail);

        // 1. Strict approval check
        if (!Boolean.TRUE.equals(lead.getConsultantApproved())) {
            throw new IllegalStateException("An unapproved draft cannot be sent. The Sales Consultant must explicitly review and approve the draft before sending.");
        }

        // 2. Strict opt-out check
        if (Boolean.TRUE.equals(lead.getOptOut())) {
            throw new IllegalStateException("This customer has explicitly opted out of further communications. Dispatch of messages is strictly blocked.");
        }

        String channel = (request.getChannel() != null && !request.getChannel().isBlank())
                ? request.getChannel().trim().toUpperCase()
                : "WHATSAPP_SIMULATED";

        OffsetDateTime now = OffsetDateTime.now();

        // 3. Record Communication Audit
        LeadCommunication comm = new LeadCommunication();
        comm.setLead(lead);
        comm.setConsultant(consultant);
        comm.setDraftContent(lead.getAiDraftReply());
        comm.setApprovedContent(lead.getAiDraftReply());
        comm.setChannel(channel);
        comm.setDeliveryStatus("DELIVERED_SIMULATED");
        comm.setIsApproved(true);
        comm.setApprovedAt(now);
        comm.setSentAt(now);

        LeadCommunication savedComm = communicationRepository.save(comm);

        // 4. Update Lead status to CONTACTED if it was NEW
        if ("NEW".equalsIgnoreCase(lead.getStatus())) {
            lead.setStatus("CONTACTED");
            lead.setSentVia(channel);
            leadRepository.saveAndFlush(lead);

            LeadStatusHistory history = new LeadStatusHistory(lead, consultant, "NEW", "CONTACTED",
                    "Simulated first-reply dispatched via " + channel + " by " + consultant.getFullName());
            statusHistoryRepository.save(history);
        }

        log.info("[SIMULATED COMMUNICATION] Dispatched approved reply for Lead {} via {} to {}",
                lead.getLeadRef(), channel, lead.getCustomerEmail());

        return carMapper.toLeadCommunicationResponse(savedComm);
    }

    private User validateConsultantAccess(Lead lead, String consultantEmail) {
        User consultant = userRepository.findByEmail(consultantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", consultantEmail));

        // Strict consultant data isolation
        if (lead.getLastAssignedConsultant() == null || !lead.getLastAssignedConsultant().getId().equals(consultant.getId())) {
            throw new AccessDeniedException("You do not have permission to access leads assigned to other consultants.");
        }
        return consultant;
    }

    private void validateStatusTransition(String from, String to) {
        if (from.equalsIgnoreCase(to)) {
            return; // No-op transition
        }

        switch (from) {
            case "NEW":
                if (!to.equals("CONTACTED") && !to.equals("LOST")) {
                    throw new IllegalArgumentException("Cannot transition directly from NEW to " + to + ". Lead must be CONTACTED first or marked LOST.");
                }
                break;
            case "CONTACTED":
                if (!to.equals("FOLLOW_UP") && !to.equals("TEST_DRIVE_SCHEDULED") && !to.equals("CONVERTED") && !to.equals("LOST")) {
                    throw new IllegalArgumentException("Cannot transition from CONTACTED to " + to);
                }
                break;
            case "FOLLOW_UP":
                if (!to.equals("CONTACTED") && !to.equals("TEST_DRIVE_SCHEDULED") && !to.equals("CONVERTED") && !to.equals("LOST")) {
                    throw new IllegalArgumentException("Cannot transition from FOLLOW_UP to " + to);
                }
                break;
            case "TEST_DRIVE_SCHEDULED":
                if (!to.equals("FOLLOW_UP") && !to.equals("CONVERTED") && !to.equals("LOST")) {
                    throw new IllegalArgumentException("Cannot transition from TEST_DRIVE_SCHEDULED to " + to);
                }
                break;
            case "CONVERTED":
                throw new IllegalArgumentException("Lead is already CONVERTED and cannot be moved to another status.");
            case "LOST":
                if (!to.equals("NEW") && !to.equals("FOLLOW_UP")) {
                    throw new IllegalArgumentException("Closed LOST leads can only be reopened to NEW or FOLLOW_UP.");
                }
                break;
            default:
                break;
        }
    }
}
