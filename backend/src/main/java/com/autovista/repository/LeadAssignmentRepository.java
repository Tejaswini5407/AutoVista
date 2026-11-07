package com.autovista.repository;

import com.autovista.entity.LeadAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadAssignmentRepository extends JpaRepository<LeadAssignment, Long> {

    List<LeadAssignment> findByConsultantId(Long consultantId);

    List<LeadAssignment> findByConsultantIdOrderByAssignedAtDesc(Long consultantId);

    List<LeadAssignment> findByLeadIdOrderByAssignedAtDesc(Long leadId);

    Optional<LeadAssignment> findTopByLeadIdOrderByAssignedAtDesc(Long leadId);

    List<LeadAssignment> findByStatus(String status);
}
