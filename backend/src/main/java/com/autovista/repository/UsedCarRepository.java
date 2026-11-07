package com.autovista.repository;

import com.autovista.entity.UsedCar;
import com.autovista.entity.UsedCarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsedCarRepository extends JpaRepository<UsedCar, Long> {

    List<UsedCar> findByStatus(UsedCarStatus status);

    List<UsedCar> findByShowroomId(Long showroomId);

    long countByStatus(UsedCarStatus status);
}
