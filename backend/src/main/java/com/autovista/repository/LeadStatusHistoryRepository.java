package com.autovista.repository;

import com.autovista.entity.LeadStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadStatusHistoryRepository extends JpaRepository<LeadStatusHistory, Long> {

    List<LeadStatusHistory> findByLeadIdOrderByCreatedAtDesc(Long leadId);
}
