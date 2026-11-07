package com.autovista.repository;

import com.autovista.entity.CityPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityPriceRepository extends JpaRepository<CityPrice, Long> {

    Optional<CityPrice> findByCarVariantIdAndCityIgnoreCase(Long carVariantId, String city);

    List<CityPrice> findByCarVariantId(Long carVariantId);

    List<CityPrice> findByCityIgnoreCase(String city);
}
