package com.autovista.repository;

import com.autovista.entity.LeadCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadCommunicationRepository extends JpaRepository<LeadCommunication, Long> {

    List<LeadCommunication> findByLeadIdOrderBySentAtDesc(Long leadId);

    List<LeadCommunication> findByConsultantIdOrderBySentAtDesc(Long consultantId);
}
