package com.autovista.service;

import com.autovista.dto.response.CarModelResponse;
import com.autovista.dto.response.CarVariantResponse;
import com.autovista.dto.response.CityPriceResponse;
import com.autovista.entity.CarVariant;
import com.autovista.entity.CityPrice;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.CarModelRepository;
import com.autovista.repository.CarVariantRepository;
import com.autovista.repository.CityPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarModelRepository carModelRepository;
    private final CarVariantRepository carVariantRepository;
    private final CityPriceRepository cityPriceRepository;
    private final CarMapper carMapper;

    public CarService(CarModelRepository carModelRepository,
                      CarVariantRepository carVariantRepository,
                      CityPriceRepository cityPriceRepository,
                      CarMapper carMapper) {
        this.carModelRepository = carModelRepository;
        this.carVariantRepository = carVariantRepository;
        this.cityPriceRepository = cityPriceRepository;
        this.carMapper = carMapper;
    }

    @Transactional(readOnly = true)
    public List<CarModelResponse> getAllCarModels() {
        return carModelRepository.findByIsActiveTrue().stream()
                .map(carMapper::toModelResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarModelResponse getCarModelById(Long id) {
        return carModelRepository.findByIdAndIsActiveTrue(id)
                .map(carMapper::toModelResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Car Model", "id", id));
    }

    @Transactional(readOnly = true)
    public List<CarVariantResponse> getAllCarVariants() {
        return carVariantRepository.findByIsAvailableTrue().stream()
                .map(carMapper::toVariantResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarVariantResponse getVariantById(Long id) {
        CarVariant variant = carVariantRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car Variant", "id", id));
        return carMapper.toVariantResponse(variant);
    }

    @Transactional(readOnly = true)
    public List<CarVariantResponse> getVariantsByFuelType(String fuelType) {
        return carVariantRepository.findByFuelTypeIgnoreCaseAndIsAvailableTrue(fuelType).stream()
                .map(carMapper::toVariantResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CityPriceResponse getPricingForVariantAndCity(Long variantId, String city) {
        CityPrice cityPrice = cityPriceRepository.findByCarVariantIdAndCityIgnoreCase(variantId, city)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Pricing configuration not found for variant ID %d in city '%s'", variantId, city)
                ));
        return carMapper.toCityPriceResponse(cityPrice);
    }
}
