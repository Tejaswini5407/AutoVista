package com.autovista.repository;

import com.autovista.entity.Showroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowroomRepository extends JpaRepository<Showroom, Long> {

    Optional<Showroom> findByCode(String code);

    List<Showroom> findByCityIgnoreCaseAndIsActiveTrue(String city);

    List<Showroom> findByIsActiveTrue();
}
