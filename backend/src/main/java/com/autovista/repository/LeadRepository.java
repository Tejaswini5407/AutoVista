package com.autovista.repository;

import com.autovista.entity.Lead;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    Optional<Lead> findByLeadRef(String leadRef);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lead l WHERE l.id = :id")
    Optional<Lead> findByIdWithPessimisticLock(@Param("id") Long id);

    List<Lead> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Lead> findByLastAssignedConsultantIdOrderByCreatedAtDesc(Long consultantId);

    List<Lead> findByLastAssignedConsultantIdAndStatusOrderByCreatedAtDesc(Long consultantId, String status);

    List<Lead> findByLastAssignedConsultantIsNullOrderByCreatedAtDesc();

    List<Lead> findByStatus(String status);

    List<Lead> findByOptOutFalse();

    @Query("SELECT l FROM Lead l WHERE l.customerEmail = :email AND l.interestedCarVariant.id = :variantId AND l.createdAt >= :afterTime ORDER BY l.createdAt DESC")
    List<Lead> findRecentDuplicateNewCarLead(
            @Param("email") String email,
            @Param("variantId") Long variantId,
            @Param("afterTime") OffsetDateTime afterTime);

    @Query("SELECT l FROM Lead l WHERE l.customerEmail = :email AND l.interestedUsedCar.id = :usedCarId AND l.createdAt >= :afterTime ORDER BY l.createdAt DESC")
    List<Lead> findRecentDuplicateUsedCarLead(
            @Param("email") String email,
            @Param("usedCarId") Long usedCarId,
            @Param("afterTime") OffsetDateTime afterTime);

    long countByStatus(String status);

    long countByLastAssignedConsultantId(Long consultantId);

    long countByLastAssignedConsultantIsNull();
}
