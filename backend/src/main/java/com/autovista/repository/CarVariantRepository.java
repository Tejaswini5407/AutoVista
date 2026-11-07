package com.autovista.repository;

import com.autovista.entity.CarVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarVariantRepository extends JpaRepository<CarVariant, Long> {

    List<CarVariant> findByIsAvailableTrue();

    Optional<CarVariant> findByIdAndIsAvailableTrue(Long id);

    List<CarVariant> findByCarModelIdAndIsAvailableTrue(Long carModelId);

    List<CarVariant> findByFuelTypeIgnoreCaseAndIsAvailableTrue(String fuelType);

    List<CarVariant> findByCarModelId(Long carModelId);
}
