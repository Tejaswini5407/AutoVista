package com.autovista.service;

import com.autovista.entity.Lead;
import com.autovista.entity.LeadAssignment;
import com.autovista.entity.LeadStatusHistory;
import com.autovista.entity.Role;
import com.autovista.entity.User;
import com.autovista.repository.LeadAssignmentRepository;
import com.autovista.repository.LeadRepository;
import com.autovista.repository.LeadStatusHistoryRepository;
import com.autovista.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeadAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(LeadAssignmentService.class);

    private final LeadRepository leadRepository;
    private final LeadAssignmentRepository assignmentRepository;
    private final LeadStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    public LeadAssignmentService(
            LeadRepository leadRepository,
            LeadAssignmentRepository assignmentRepository,
            LeadStatusHistoryRepository statusHistoryRepository,
            UserRepository userRepository) {
        this.leadRepository = leadRepository;
        this.assignmentRepository = assignmentRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Requirement #3: Automatic Lead Assignment.
     * Concurrency-safe assignment of a lead using pessimistic write lock.
     * Assigns to the next available active sales consultant using round-robin.
     */
    public Lead assignLead(Long leadId) {
        Lead lead = leadRepository.findByIdWithPessimisticLock(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found with ID: " + leadId));

        // Concurrency guard: avoid assigning an already assigned lead
        if (lead.getLastAssignedConsultant() != null) {
            log.info("Lead {} is already assigned to consultant {}. Skipping duplicate assignment.",
                    lead.getLeadRef(), lead.getLastAssignedConsultant().getEmail());
            return lead;
        }

        List<User> activeConsultants = getActiveConsultants();

        if (activeConsultants.isEmpty()) {
            log.warn("No active sales consultants available for lead {}. Queuing for scheduler.", lead.getLeadRef());
            recordStatusTransition(lead, null, null, "NEW",
                    "Lead created. No active sales consultant available; queued for automatic assignment.");
            return lead;
        }

        // Round-robin selection among active consultants
        int index = Math.abs(roundRobinIndex.getAndIncrement() % activeConsultants.size());
        User assignedConsultant = activeConsultants.get(index);

        OffsetDateTime now = OffsetDateTime.now();
        lead.setLastAssignedConsultant(assignedConsultant);
        lead.setAssignedAt(now);

        Lead savedLead = leadRepository.saveAndFlush(lead);

        // Record LeadAssignment audit entry with 5-minute SLA
        LeadAssignment assignment = new LeadAssignment();
        assignment.setLead(savedLead);
        assignment.setConsultant(assignedConsultant);
        assignment.setAssignedAt(now);
        assignment.setResponseDueBy(now.plusMinutes(5));
        assignment.setStatus("PENDING");
        assignment.setNotes("Automatically assigned via round-robin distribution.");
        assignmentRepository.save(assignment);

        // Record initial status entry (from null to NEW) to represent lead genesis clearly
        recordStatusTransition(savedLead, null, null, "NEW",
                "Customer enquiry created and assigned to consultant " + assignedConsultant.getFullName() + " with 5-minute response SLA.");

        log.info("Lead {} successfully assigned to {} with 5m SLA (Due: {})",
                savedLead.getLeadRef(), assignedConsultant.getEmail(), assignment.getResponseDueBy());

        return savedLead;
    }

    /**
     * Requirement #8: Reassign lead by Admin.
     */
    public Lead reassignLead(Long leadId, Long targetConsultantId, String notes, User adminUser) {
        Lead lead = leadRepository.findByIdWithPessimisticLock(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found with ID: " + leadId));

        User newConsultant = userRepository.findById(targetConsultantId)
                .filter(u -> u.getRole() == Role.CONSULTANT && Boolean.TRUE.equals(u.getIsActive()))
                .orElseThrow(() -> new IllegalArgumentException("Target consultant not found or not active"));

        User previousConsultant = lead.getLastAssignedConsultant();
        OffsetDateTime now = OffsetDateTime.now();

        lead.setLastAssignedConsultant(newConsultant);
        lead.setAssignedAt(now);
        Lead savedLead = leadRepository.saveAndFlush(lead);

        // Record reassignment in the dedicated LeadAssignment audit entity
        LeadAssignment assignment = new LeadAssignment();
        assignment.setLead(savedLead);
        assignment.setConsultant(newConsultant);
        assignment.setAssignedAt(now);
        assignment.setResponseDueBy(now.plusMinutes(5));
        assignment.setStatus("PENDING");
        assignment.setNotes("Reassigned by Admin " + (adminUser != null ? adminUser.getFullName() : "") +
                (notes != null && !notes.isBlank() ? ". Note: " + notes : ""));
        assignmentRepository.save(assignment);

        return savedLead;
    }

    /**
     * Requirement #3: Background scheduler to ensure all leads are assigned within 5 minutes.
     * Runs every 60 seconds as a reliable backstop for any queued unassigned leads.
     */
    @Scheduled(fixedDelay = 60000)
    public void assignPendingUnassignedLeads() {
        List<Lead> unassigned = leadRepository.findByLastAssignedConsultantIsNullOrderByCreatedAtDesc();
        if (unassigned.isEmpty()) {
            return;
        }

        log.info("Scheduler checking {} unassigned leads for automatic assignment", unassigned.size());
        for (Lead lead : unassigned) {
            try {
                assignLead(lead.getId());
            } catch (Exception ex) {
                log.error("Failed scheduled assignment for lead {}: {}", lead.getLeadRef(), ex.getMessage());
            }
        }
    }

    public List<User> getActiveConsultants() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CONSULTANT && Boolean.TRUE.equals(u.getIsActive()))
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    private void recordStatusTransition(Lead lead, User changedBy, String fromStatus, String toStatus, String notes) {
        LeadStatusHistory history = new LeadStatusHistory(lead, changedBy, fromStatus, toStatus, notes);
        statusHistoryRepository.save(history);
    }
}
