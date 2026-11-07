package com.autovista.service;

import com.autovista.dto.request.CarModelRequest;
import com.autovista.dto.request.CarVariantRequest;
import com.autovista.dto.request.CityPriceRequest;
import com.autovista.dto.request.UsedCarRequest;
import com.autovista.dto.response.AdminStatsResponse;
import com.autovista.dto.response.AdminUsedCarResponse;
import com.autovista.dto.response.CarModelResponse;
import com.autovista.dto.response.CarVariantResponse;
import com.autovista.dto.response.CityPriceResponse;
import com.autovista.entity.CarModel;
import com.autovista.entity.CarVariant;
import com.autovista.entity.CityPrice;
import com.autovista.entity.Showroom;
import com.autovista.entity.UsedCar;
import com.autovista.entity.UsedCarStatus;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.CarModelRepository;
import com.autovista.repository.CarVariantRepository;
import com.autovista.repository.CityPriceRepository;
import com.autovista.repository.ShowroomRepository;
import com.autovista.repository.TestDriveBookingRepository;
import com.autovista.repository.UsedCarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final CarModelRepository carModelRepository;
    private final CarVariantRepository carVariantRepository;
    private final UsedCarRepository usedCarRepository;
    private final CityPriceRepository cityPriceRepository;
    private final ShowroomRepository showroomRepository;
    private final TestDriveBookingRepository bookingRepository;
    private final CarMapper carMapper;

    public AdminService(CarModelRepository carModelRepository,
                        CarVariantRepository carVariantRepository,
                        UsedCarRepository usedCarRepository,
                        CityPriceRepository cityPriceRepository,
                        ShowroomRepository showroomRepository,
                        TestDriveBookingRepository bookingRepository,
                        CarMapper carMapper) {
        this.carModelRepository = carModelRepository;
        this.carVariantRepository = carVariantRepository;
        this.usedCarRepository = usedCarRepository;
        this.cityPriceRepository = cityPriceRepository;
        this.showroomRepository = showroomRepository;
        this.bookingRepository = bookingRepository;
        this.carMapper = carMapper;
    }

    // ==========================================
    // 1. DASHBOARD OVERVIEW STATS
    // ==========================================
    @Transactional(readOnly = true)
    public AdminStatsResponse getAdminStats() {
        long totalNewModels = carModelRepository.count();
        long totalNewVariants = carVariantRepository.count();
        long totalUsedCars = usedCarRepository.count();
        long usedAvailable = usedCarRepository.countByStatus(UsedCarStatus.AVAILABLE);
        long usedBooked = usedCarRepository.countByStatus(UsedCarStatus.BOOKED);
        long usedSold = usedCarRepository.countByStatus(UsedCarStatus.SOLD);
        long totalPricingRules = cityPriceRepository.count();
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.findByStatus("CONFIRMED").size();

        return new AdminStatsResponse(
                totalNewModels,
                totalNewVariants,
                totalUsedCars,
                usedAvailable,
                usedBooked,
                usedSold,
                totalPricingRules,
                totalBookings,
                confirmedBookings
        );
    }

    // ==========================================
    // 2. NEW CAR INVENTORY MANAGEMENT
    // ==========================================
    @Transactional(readOnly = true)
    public List<CarModelResponse> getAllModels() {
        return carModelRepository.findAll().stream()
                .map(carMapper::toModelResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarModelResponse getModelById(Long id) {
        CarModel model = carModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarModel", "id", id));
        return carMapper.toModelResponse(model);
    }

    public CarModelResponse createModel(CarModelRequest request) {
        CarModel model = new CarModel();
        model.setBrand(request.getBrand().trim());
        model.setModelName(request.getModelName().trim());
        model.setBodyType(request.getBodyType().trim());
        model.setYear(request.getYear());
        model.setDescription(request.getDescription());
        model.setHeroImageUrl(request.getHeroImageUrl());
        model.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        CarModel saved = carModelRepository.save(model);
        return carMapper.toModelResponse(saved);
    }

    public CarModelResponse updateModel(Long id, CarModelRequest request) {
        CarModel model = carModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarModel", "id", id));

        model.setBrand(request.getBrand().trim());
        model.setModelName(request.getModelName().trim());
        model.setBodyType(request.getBodyType().trim());
        model.setYear(request.getYear());
        model.setDescription(request.getDescription());
        if (request.getHeroImageUrl() != null && !request.getHeroImageUrl().isBlank()) {
            model.setHeroImageUrl(request.getHeroImageUrl());
        }
        if (request.getIsActive() != null) {
            model.setIsActive(request.getIsActive());
        }

        CarModel updated = carModelRepository.save(model);
        return carMapper.toModelResponse(updated);
    }

    public CarModelResponse toggleModelStatus(Long id, Boolean active) {
        CarModel model = carModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarModel", "id", id));
        model.setIsActive(active != null ? active : !model.getIsActive());
        CarModel updated = carModelRepository.save(model);
        return carMapper.toModelResponse(updated);
    }

    public CarVariantResponse createVariant(Long modelId, CarVariantRequest request) {
        CarModel model = carModelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("CarModel", "id", modelId));

        CarVariant variant = new CarVariant();
        variant.setCarModel(model);
        variant.setVariantName(request.getVariantName().trim());
        variant.setFuelType(request.getFuelType().trim());
        variant.setTransmission(request.getTransmission().trim());
        variant.setEngineSpecs(request.getEngineSpecs());
        variant.setPowerSpecs(request.getPowerSpecs());
        variant.setTorqueSpecs(request.getTorqueSpecs());
        variant.setSeatingCapacity(request.getSeatingCapacity() != null ? request.getSeatingCapacity() : 5);
        variant.setExShowroomPrice(request.getExShowroomPrice());
        variant.setTagline(request.getTagline());
        variant.setFeatures(request.getFeatures());
        variant.setColors(request.getColors());
        variant.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);

        CarVariant saved = carVariantRepository.save(variant);
        return carMapper.toVariantResponse(saved);
    }

    public CarVariantResponse updateVariant(Long variantId, CarVariantRequest request) {
        CarVariant variant = carVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("CarVariant", "id", variantId));

        variant.setVariantName(request.getVariantName().trim());
        variant.setFuelType(request.getFuelType().trim());
        variant.setTransmission(request.getTransmission().trim());
        variant.setEngineSpecs(request.getEngineSpecs());
        variant.setPowerSpecs(request.getPowerSpecs());
        variant.setTorqueSpecs(request.getTorqueSpecs());
        if (request.getSeatingCapacity() != null) {
            variant.setSeatingCapacity(request.getSeatingCapacity());
        }
        variant.setExShowroomPrice(request.getExShowroomPrice());
        variant.setTagline(request.getTagline());
        if (request.getFeatures() != null) {
            variant.setFeatures(request.getFeatures());
        }
        if (request.getColors() != null) {
            variant.setColors(request.getColors());
        }
        if (request.getIsAvailable() != null) {
            variant.setIsAvailable(request.getIsAvailable());
        }

        CarVariant updated = carVariantRepository.save(variant);
        return carMapper.toVariantResponse(updated);
    }

    public CarVariantResponse toggleVariantStatus(Long variantId, Boolean available) {
        CarVariant variant = carVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("CarVariant", "id", variantId));
        variant.setIsAvailable(available != null ? available : !variant.getIsAvailable());
        CarVariant updated = carVariantRepository.save(variant);
        return carMapper.toVariantResponse(updated);
    }

    // ==========================================
    // 3. USED CAR INVENTORY MANAGEMENT
    // ==========================================
    @Transactional(readOnly = true)
    public List<AdminUsedCarResponse> getAllUsedCars() {
        return usedCarRepository.findAll().stream()
                .map(carMapper::toAdminUsedCarResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminUsedCarResponse getUsedCarById(Long id) {
        UsedCar car = usedCarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UsedCar", "id", id));
        return carMapper.toAdminUsedCarResponse(car);
    }

    public AdminUsedCarResponse createUsedCar(UsedCarRequest request) {
        Showroom showroom;
        if (request.getShowroomId() != null) {
            showroom = showroomRepository.findById(request.getShowroomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Showroom", "id", request.getShowroomId()));
        } else {
            showroom = showroomRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No showrooms available in database"));
        }

        UsedCar car = new UsedCar();
        car.setShowroom(showroom);
        car.setBrand(request.getBrand().trim());
        car.setModelName(request.getModelName().trim());
        car.setVariantName(request.getVariantName().trim());
        car.setManufacturingYear(request.getManufacturingYear());
        car.setPrice(request.getPrice());
        car.setOdometerKm(request.getOdometerKm());
        car.setRawRegistrationNumber(request.getRawRegistrationNumber().trim().toUpperCase());
        car.setFuelType(request.getFuelType().trim());
        car.setTransmission(request.getTransmission().trim());
        car.setOwnersCount(request.getOwnersCount() != null ? request.getOwnersCount() : 1);
        car.setInsuranceValidity(request.getInsuranceValidity());
        car.setInspectionPointsPassed(request.getInspectionPointsPassed() != null ? request.getInspectionPointsPassed() : 150);
        car.setStatus(request.getStatus() != null ? request.getStatus() : UsedCarStatus.AVAILABLE);
        car.setHeroImageUrl(request.getHeroImageUrl());
        car.setFeatures(request.getFeatures());
        car.setTagline(request.getTagline());

        UsedCar saved = usedCarRepository.save(car);
        return carMapper.toAdminUsedCarResponse(saved);
    }

    public AdminUsedCarResponse updateUsedCar(Long id, UsedCarRequest request) {
        UsedCar car = usedCarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UsedCar", "id", id));

        if (request.getShowroomId() != null) {
            Showroom showroom = showroomRepository.findById(request.getShowroomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Showroom", "id", request.getShowroomId()));
            car.setShowroom(showroom);
        }
        car.setBrand(request.getBrand().trim());
        car.setModelName(request.getModelName().trim());
        car.setVariantName(request.getVariantName().trim());
        car.setManufacturingYear(request.getManufacturingYear());
        car.setPrice(request.getPrice());
        car.setOdometerKm(request.getOdometerKm());
        if (request.getRawRegistrationNumber() != null && !request.getRawRegistrationNumber().isBlank()) {
            car.setRawRegistrationNumber(request.getRawRegistrationNumber().trim().toUpperCase());
        }
        car.setFuelType(request.getFuelType().trim());
        car.setTransmission(request.getTransmission().trim());
        if (request.getOwnersCount() != null) car.setOwnersCount(request.getOwnersCount());
        if (request.getInsuranceValidity() != null) car.setInsuranceValidity(request.getInsuranceValidity());
        if (request.getInspectionPointsPassed() != null) car.setInspectionPointsPassed(request.getInspectionPointsPassed());
        if (request.getStatus() != null) car.setStatus(request.getStatus());
        if (request.getHeroImageUrl() != null && !request.getHeroImageUrl().isBlank()) car.setHeroImageUrl(request.getHeroImageUrl());
        if (request.getFeatures() != null) car.setFeatures(request.getFeatures());
        if (request.getTagline() != null) car.setTagline(request.getTagline());

        UsedCar updated = usedCarRepository.save(car);
        return carMapper.toAdminUsedCarResponse(updated);
    }

    public AdminUsedCarResponse updateUsedCarStatus(Long id, UsedCarStatus status) {
        UsedCar car = usedCarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UsedCar", "id", id));
        car.setStatus(status);
        UsedCar updated = usedCarRepository.save(car);
        return carMapper.toAdminUsedCarResponse(updated);
    }

    // ==========================================
    // 4. CITY-WISE PRICING MANAGEMENT (PRICING ENGINE)
    // ==========================================
    @Transactional(readOnly = true)
    public List<CityPriceResponse> getAllCityPrices() {
        return cityPriceRepository.findAll().stream()
                .map(carMapper::toCityPriceResponse)
                .collect(Collectors.toList());
    }

    public CityPriceResponse createCityPrice(CityPriceRequest request) {
        CarVariant variant = carVariantRepository.findById(request.getCarVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("CarVariant", "id", request.getCarVariantId()));

        CityPrice price = cityPriceRepository
                .findByCarVariantIdAndCityIgnoreCase(request.getCarVariantId(), request.getCity().trim())
                .orElse(new CityPrice());

        price.setCarVariant(variant);
        price.setCity(request.getCity().trim());
        price.setState(request.getState() != null ? request.getState().trim() : "Uttar Pradesh");
        price.setRtoTaxPercent(request.getRtoTaxPercent());

        BigDecimal exShowroom = variant.getExShowroomPrice();
        BigDecimal rtoTaxAmount = exShowroom.multiply(request.getRtoTaxPercent())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        price.setRtoTaxAmount(rtoTaxAmount);

        BigDecimal insuranceAmount = request.getInsuranceAmount();
        price.setInsuranceAmount(insuranceAmount);

        BigDecimal accessories = request.getAccessoriesAmount() != null
                ? request.getAccessoriesAmount() : BigDecimal.ZERO;
        price.setAccessoriesAmount(accessories);

        BigDecimal handling = request.getHandlingCharges() != null
                ? request.getHandlingCharges() : BigDecimal.ZERO;
        price.setHandlingCharges(handling);

        // Section 206C(1F) IT Act: 1% TCS on motor vehicles exceeding ₹10 Lakhs (10,00,000)
        BigDecimal tcsAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (exShowroom.compareTo(new BigDecimal("1000000.00")) >= 0) {
            tcsAmount = exShowroom.multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_UP);
        }
        price.setTcsAmount(tcsAmount);

        BigDecimal total = exShowroom.add(rtoTaxAmount)
                .add(insuranceAmount)
                .add(accessories)
                .add(handling)
                .add(tcsAmount);
        price.setTotalOnRoadPrice(total);

        price.setPriceValidUntil(request.getPriceValidUntil());
        if (request.getDisclaimer() != null && !request.getDisclaimer().isBlank()) {
            price.setDisclaimer(request.getDisclaimer());
        }

        CityPrice saved = cityPriceRepository.save(price);
        return carMapper.toCityPriceResponse(saved);
    }

    public CityPriceResponse updateCityPrice(Long id, CityPriceRequest request) {
        CityPrice price = cityPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CityPrice", "id", id));

        CarVariant variant = price.getCarVariant();
        if (request.getCarVariantId() != null && !request.getCarVariantId().equals(variant.getId())) {
            variant = carVariantRepository.findById(request.getCarVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("CarVariant", "id", request.getCarVariantId()));
            price.setCarVariant(variant);
        }

        if (request.getCity() != null && !request.getCity().isBlank()) {
            price.setCity(request.getCity().trim());
        }
        if (request.getState() != null) {
            price.setState(request.getState().trim());
        }
        if (request.getRtoTaxPercent() != null) {
            price.setRtoTaxPercent(request.getRtoTaxPercent());
        }

        BigDecimal exShowroom = variant.getExShowroomPrice();
        BigDecimal rtoTaxAmount = exShowroom.multiply(price.getRtoTaxPercent())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        price.setRtoTaxAmount(rtoTaxAmount);

        if (request.getInsuranceAmount() != null) {
            price.setInsuranceAmount(request.getInsuranceAmount());
        }
        if (request.getAccessoriesAmount() != null) {
            price.setAccessoriesAmount(request.getAccessoriesAmount());
        }
        if (request.getHandlingCharges() != null) {
            price.setHandlingCharges(request.getHandlingCharges());
        }

        BigDecimal tcsAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (exShowroom.compareTo(new BigDecimal("1000000.00")) >= 0) {
            tcsAmount = exShowroom.multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_UP);
        }
        price.setTcsAmount(tcsAmount);

        BigDecimal total = exShowroom.add(price.getRtoTaxAmount())
                .add(price.getInsuranceAmount())
                .add(price.getAccessoriesAmount())
                .add(price.getHandlingCharges())
                .add(tcsAmount);
        price.setTotalOnRoadPrice(total);

        if (request.getPriceValidUntil() != null) {
            price.setPriceValidUntil(request.getPriceValidUntil());
        }
        if (request.getDisclaimer() != null && !request.getDisclaimer().isBlank()) {
            price.setDisclaimer(request.getDisclaimer());
        }

        CityPrice updated = cityPriceRepository.save(price);
        return carMapper.toCityPriceResponse(updated);
    }
}
