package com.autovista.repository;

import com.autovista.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {

    List<CarModel> findByIsActiveTrue();

    Optional<CarModel> findByIdAndIsActiveTrue(Long id);
}
