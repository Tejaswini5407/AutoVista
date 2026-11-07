package com.autovista.service;

import com.autovista.dto.request.CreateLeadRequest;
import com.autovista.dto.response.LeadResponse;
import com.autovista.entity.*;
import com.autovista.exception.DuplicateBookingException;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeadService {

    private static final Logger log = LoggerFactory.getLogger(LeadService.class);

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ShowroomRepository showroomRepository;
    private final CarVariantRepository carVariantRepository;
    private final UsedCarRepository usedCarRepository;
    private final LeadAssignmentService assignmentService;
    private final AiLeadService aiLeadService;
    private final CarMapper carMapper;

    public LeadService(
            LeadRepository leadRepository,
            UserRepository userRepository,
            ShowroomRepository showroomRepository,
            CarVariantRepository carVariantRepository,
            UsedCarRepository usedCarRepository,
            LeadAssignmentService assignmentService,
            AiLeadService aiLeadService,
            CarMapper carMapper) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.showroomRepository = showroomRepository;
        this.carVariantRepository = carVariantRepository;
        this.usedCarRepository = usedCarRepository;
        this.assignmentService = assignmentService;
        this.aiLeadService = aiLeadService;
        this.carMapper = carMapper;
    }

    /**
     * Requirement #2: Customer Enquiry and Lead Creation.
     * Enforces mandatory explicit consent, authenticated customer binding,
     * duplicate submission prevention, unique reference, and immediate assignment.
     */
    public LeadResponse createLead(CreateLeadRequest request, String authenticatedEmail) {
        // 1. Mandatory Explicit Consent Validation
        if (!Boolean.TRUE.equals(request.getConsentGiven())) {
            throw new IllegalArgumentException("Explicit consent to be contacted is mandatory before submitting an enquiry.");
        }

        // 2. Resolve Authenticated User
        User customerUser = null;
        if (authenticatedEmail != null && !authenticatedEmail.isBlank()) {
            customerUser = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authenticatedEmail));
        }

        String email = (customerUser != null) ? customerUser.getEmail() : request.getCustomerEmail().trim();
        String name = (customerUser != null) ? customerUser.getFullName() : request.getCustomerName().trim();
        String phone = (customerUser != null && customerUser.getPhone() != null)
                ? customerUser.getPhone()
                : request.getCustomerPhone().trim();

        // 3. Resolve Vehicle
        CarVariant variant = null;
        if (request.getCarVariantId() != null) {
            variant = carVariantRepository.findById(request.getCarVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("CarVariant", "id", request.getCarVariantId()));
        }

        UsedCar usedCar = null;
        if (request.getUsedCarId() != null) {
            usedCar = usedCarRepository.findById(request.getUsedCarId())
                    .orElseThrow(() -> new ResourceNotFoundException("UsedCar", "id", request.getUsedCarId()));
        }

        if (variant == null && usedCar == null) {
            throw new IllegalArgumentException("An enquiry must be associated with either a new-car variant or a certified pre-owned vehicle.");
        }

        // 4. Duplicate Submission Protection (Within last 10 minutes for identical vehicle and email)
        OffsetDateTime tenMinutesAgo = OffsetDateTime.now().minusMinutes(10);
        if (variant != null) {
            List<Lead> duplicates = leadRepository.findRecentDuplicateNewCarLead(email, variant.getId(), tenMinutesAgo);
            if (!duplicates.isEmpty()) {
                throw new DuplicateBookingException("You have already submitted an enquiry for this model within the last 10 minutes (Ref: "
                        + duplicates.get(0).getLeadRef() + "). Our consultant is already reviewing your request.");
            }
        } else {
            List<Lead> duplicates = leadRepository.findRecentDuplicateUsedCarLead(email, usedCar.getId(), tenMinutesAgo);
            if (!duplicates.isEmpty()) {
                throw new DuplicateBookingException("You have already submitted an enquiry for this pre-owned vehicle within the last 10 minutes (Ref: "
                        + duplicates.get(0).getLeadRef() + "). Our consultant is already reviewing your request.");
            }
        }

        // 5. Resolve Showroom (Default to Flagship Experience Centre if unspecified)
        Showroom showroom = null;
        if (request.getShowroomId() != null) {
            showroom = showroomRepository.findById(request.getShowroomId()).orElse(null);
        }
        if (showroom == null) {
            showroom = showroomRepository.findById(1L).orElse(null);
        }

        // 6. Generate Unique Lead Reference (AV-LD-2026-XXXXXXXX)
        String leadRef = String.format("AV-LD-%d-%s",
                OffsetDateTime.now().getYear(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Lead lead = new Lead();
        lead.setLeadRef(leadRef);
        lead.setUser(customerUser);
        lead.setShowroom(showroom);
        lead.setCustomerName(name);
        lead.setCustomerEmail(email);
        lead.setCustomerPhone(phone);
        lead.setCustomerCity(request.getCustomerCity() != null ? request.getCustomerCity().trim() : "Lucknow");
        lead.setInterestedCarVariant(variant);
        lead.setInterestedUsedCar(usedCar);
        lead.setEnquiryType(request.getEnquiryType() != null ? request.getEnquiryType().trim() : "GENERAL_ENQUIRY");
        lead.setEnquiryMessage(request.getEnquiryMessage() != null ? request.getEnquiryMessage().trim() : null);
        lead.setStatedBudget(request.getStatedBudget() != null ? request.getStatedBudget().trim() : null);
        lead.setPurchaseTimeline(request.getPurchaseTimeline() != null ? request.getPurchaseTimeline().trim() : null);
        lead.setPreferredFuel(request.getPreferredFuel() != null ? request.getPreferredFuel().trim() : null);
        lead.setPreferredTransmission(request.getPreferredTransmission() != null ? request.getPreferredTransmission().trim() : null);
        lead.setConsentGiven(true);
        lead.setOptOut(Boolean.TRUE.equals(request.getOptOut()));
        lead.setStatus("NEW");

        // 7. Generate Initial AI Buyer Summary
        String aiSummary = aiLeadService.generateBuyerSummary(lead);
        lead.setAiSummary(aiSummary);

        Lead savedLead = leadRepository.saveAndFlush(lead);

        // 8. Automatic Immediate Lead Assignment to Available Sales Consultant
        savedLead = assignmentService.assignLead(savedLead.getId());

        log.info("Successfully created lead {} for customer {} ({})",
                savedLead.getLeadRef(), savedLead.getCustomerName(), savedLead.getCustomerEmail());

        return carMapper.toLeadResponse(savedLead);
    }

    /**
     * Requirement #2 & #10: Customer opt-out from further communications.
     */
    public LeadResponse updateOptOut(Long leadId, boolean optOut, String userEmail, boolean isAdmin) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        if (!isAdmin && (lead.getUser() == null || !lead.getUser().getEmail().equalsIgnoreCase(userEmail))) {
            throw new AccessDeniedException("You do not have permission to modify contact preferences for this enquiry.");
        }

        lead.setOptOut(optOut);
        Lead saved = leadRepository.saveAndFlush(lead);
        return carMapper.toLeadResponse(saved);
    }

    /**
     * Requirement #10: Customer view own enquiries only.
     */
    @Transactional(readOnly = true)
    public List<LeadResponse> getCustomerLeads(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return leadRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(carMapper::toLeadResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LeadResponse getLeadById(Long id, String userEmail, boolean isAdmin, boolean isConsultant) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));

        if (!isAdmin) {
            if (isConsultant) {
                // Must be the assigned consultant
                if (lead.getLastAssignedConsultant() == null || !lead.getLastAssignedConsultant().getEmail().equalsIgnoreCase(userEmail)) {
                    throw new AccessDeniedException("You do not have permission to view this assigned lead.");
                }
            } else {
                // Customer must own the lead
                if (lead.getUser() == null || !lead.getUser().getEmail().equalsIgnoreCase(userEmail)) {
                    throw new AccessDeniedException("You do not have permission to view this enquiry.");
                }
            }
        }

        return carMapper.toLeadResponse(lead);
    }
}
