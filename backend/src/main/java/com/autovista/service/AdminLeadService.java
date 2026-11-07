package com.autovista.service;

import com.autovista.dto.request.ReassignLeadRequest;
import com.autovista.dto.response.AdminLeadOverviewResponse;
import com.autovista.dto.response.LeadCommunicationResponse;
import com.autovista.dto.response.LeadResponse;
import com.autovista.dto.response.LeadStatusHistoryResponse;
import com.autovista.entity.Lead;
import com.autovista.entity.Role;
import com.autovista.entity.User;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminLeadService {

    private final LeadRepository leadRepository;
    private final LeadAssignmentRepository assignmentRepository;
    private final LeadStatusHistoryRepository statusHistoryRepository;
    private final LeadCommunicationRepository communicationRepository;
    private final UserRepository userRepository;
    private final LeadAssignmentService assignmentService;
    private final CarMapper carMapper;

    public AdminLeadService(
            LeadRepository leadRepository,
            LeadAssignmentRepository assignmentRepository,
            LeadStatusHistoryRepository statusHistoryRepository,
            LeadCommunicationRepository communicationRepository,
            UserRepository userRepository,
            LeadAssignmentService assignmentService,
            CarMapper carMapper) {
        this.leadRepository = leadRepository;
        this.assignmentRepository = assignmentRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.communicationRepository = communicationRepository;
        this.userRepository = userRepository;
        this.assignmentService = assignmentService;
        this.carMapper = carMapper;
    }

    /**
     * Requirement #8: Admin view all leads with filtering, search, and unassigned views.
     */
    @Transactional(readOnly = true)
    public List<LeadResponse> getAllLeads(String status, Long consultantId, String searchTerm, Boolean unassignedOnly, LocalDate date) {
        List<Lead> leads = leadRepository.findAll();

        return leads.stream()
                .filter(l -> status == null || status.isBlank() || status.equalsIgnoreCase("ALL") || l.getStatus().equalsIgnoreCase(status))
                .filter(l -> consultantId == null || (l.getLastAssignedConsultant() != null && l.getLastAssignedConsultant().getId().equals(consultantId)))
                .filter(l -> unassignedOnly == null || !unassignedOnly || l.getLastAssignedConsultant() == null)
                .filter(l -> date == null || l.getCreatedAt().toLocalDate().isEqual(date))
                .filter(l -> {
                    if (searchTerm == null || searchTerm.isBlank()) return true;
                    String term = searchTerm.toLowerCase().trim();
                    return l.getLeadRef().toLowerCase().contains(term) ||
                            l.getCustomerName().toLowerCase().contains(term) ||
                            l.getCustomerPhone().contains(term) ||
                            l.getCustomerEmail().toLowerCase().contains(term) ||
                            (l.getLastAssignedConsultant() != null && l.getLastAssignedConsultant().getFullName().toLowerCase().contains(term));
                })
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(carMapper::toLeadResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LeadResponse getLeadDetails(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));
        return carMapper.toLeadResponse(lead);
    }

    /**
     * Requirement #8: Reassign lead to an active sales consultant.
     */
    public LeadResponse reassignLead(Long leadId, ReassignLeadRequest request, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));

        Lead reassigned = assignmentService.reassignLead(leadId, request.getConsultantId(), request.getNotes(), admin);
        return carMapper.toLeadResponse(reassigned);
    }

    /**
     * Requirement #8: View lead status transition history.
     */
    @Transactional(readOnly = true)
    public List<LeadStatusHistoryResponse> getLeadStatusHistory(Long leadId) {
        return statusHistoryRepository.findByLeadIdOrderByCreatedAtDesc(leadId).stream()
                .map(carMapper::toLeadStatusHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Requirement #8: View communication audit history for a lead.
     */
    @Transactional(readOnly = true)
    public List<LeadCommunicationResponse> getLeadCommunications(Long leadId) {
        return communicationRepository.findByLeadIdOrderBySentAtDesc(leadId).stream()
                .map(carMapper::toLeadCommunicationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Requirement #8: View dealership lead counts by status and consultant workloads.
     */
    @Transactional(readOnly = true)
    public AdminLeadOverviewResponse getLeadOverview() {
        AdminLeadOverviewResponse overview = new AdminLeadOverviewResponse();

        overview.setTotalLeads(leadRepository.count());
        overview.setNewLeads(leadRepository.countByStatus("NEW"));
        overview.setContactedLeads(leadRepository.countByStatus("CONTACTED"));
        overview.setFollowUpLeads(leadRepository.countByStatus("FOLLOW_UP"));
        overview.setScheduledTestDrives(leadRepository.countByStatus("TEST_DRIVE_SCHEDULED"));
        overview.setConvertedLeads(leadRepository.countByStatus("CONVERTED"));
        overview.setLostLeads(leadRepository.countByStatus("LOST"));
        overview.setUnassignedLeads(leadRepository.countByLastAssignedConsultantIsNull());

        List<User> consultants = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CONSULTANT)
                .collect(Collectors.toList());

        List<AdminLeadOverviewResponse.ConsultantWorkload> workloads = new ArrayList<>();
        for (User consultant : consultants) {
            long total = leadRepository.countByLastAssignedConsultantId(consultant.getId());
            long pending = leadRepository.findByLastAssignedConsultantIdAndStatusOrderByCreatedAtDesc(consultant.getId(), "NEW").size();
            workloads.add(new AdminLeadOverviewResponse.ConsultantWorkload(
                    consultant.getId(),
                    consultant.getFullName(),
                    consultant.getEmail(),
                    total,
                    pending
            ));
        }
        overview.setConsultantWorkloads(workloads);

        return overview;
    }
}
