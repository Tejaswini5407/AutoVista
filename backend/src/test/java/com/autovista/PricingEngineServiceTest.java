package com.autovista;

import com.autovista.dto.request.CityPriceRequest;
import com.autovista.dto.response.CityPriceResponse;
import com.autovista.entity.CarVariant;
import com.autovista.entity.CityPrice;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.*;
import com.autovista.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingEngineServiceTest {

    @Mock
    private CarModelRepository carModelRepository;
    @Mock
    private CarVariantRepository carVariantRepository;
    @Mock
    private UsedCarRepository usedCarRepository;
    @Mock
    private CityPriceRepository cityPriceRepository;
    @Mock
    private ShowroomRepository showroomRepository;
    @Mock
    private TestDriveBookingRepository bookingRepository;

    private CarMapper carMapper;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        carMapper = new CarMapper();
        adminService = new AdminService(
                carModelRepository,
                carVariantRepository,
                usedCarRepository,
                cityPriceRepository,
                showroomRepository,
                bookingRepository,
                carMapper
        );
    }

    @Test
    @DisplayName("Pricing Engine calculates RTO tax, TCS (1% for >= 10 Lakhs) and Total accurately")
    void shouldCalculatePricingWithTcsForVehicleOver10Lakhs() {
        CarVariant variant = new CarVariant();
        variant.setId(1L);
        variant.setVariantName("ZX Automatic Petrol");
        variant.setExShowroomPrice(new BigDecimal("1245000.00")); // 12.45 Lakhs (> 10L)

        when(carVariantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(cityPriceRepository.findByCarVariantIdAndCityIgnoreCase(1L, "Lucknow")).thenReturn(Optional.empty());
        when(cityPriceRepository.save(any(CityPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CityPriceRequest req = new CityPriceRequest();
        req.setCarVariantId(1L);
        req.setCity("Lucknow");
        req.setState("Uttar Pradesh");
        req.setRtoTaxPercent(new BigDecimal("10.00")); // 10%
        req.setInsuranceAmount(new BigDecimal("43575.00"));
        req.setAccessoriesAmount(new BigDecimal("25000.00"));
        req.setHandlingCharges(new BigDecimal("5000.00"));
        req.setPriceValidUntil(LocalDate.of(2026, 12, 31));

        CityPriceResponse res = adminService.createCityPrice(req);

        assertNotNull(res);
        // Ex-showroom: 12,45,000.00
        // RTO Tax (10%): 1,24,500.00
        assertEquals(new BigDecimal("124500.00"), res.getRtoTaxAmount());
        // TCS (1% of 12,45,000): 12,450.00
        assertEquals(new BigDecimal("12450.00"), res.getTcsAmount());
        // Total: 1245000 + 124500 + 43575 + 25000 + 5000 + 12450 = 1455525.00
        assertEquals(new BigDecimal("1455525.00"), res.getTotalOnRoadPrice());
    }

    @Test
    @DisplayName("Pricing Engine omits TCS (0.00) for vehicle under 10 Lakhs")
    void shouldOmitTcsForVehicleUnder10Lakhs() {
        CarVariant variant = new CarVariant();
        variant.setId(2L);
        variant.setVariantName("EX Manual Petrol");
        variant.setExShowroomPrice(new BigDecimal("850000.00")); // 8.5 Lakhs (< 10L)

        when(carVariantRepository.findById(2L)).thenReturn(Optional.of(variant));
        when(cityPriceRepository.findByCarVariantIdAndCityIgnoreCase(2L, "Lucknow")).thenReturn(Optional.empty());
        when(cityPriceRepository.save(any(CityPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CityPriceRequest req = new CityPriceRequest();
        req.setCarVariantId(2L);
        req.setCity("Lucknow");
        req.setRtoTaxPercent(new BigDecimal("8.00")); // 8% of 850000 = 68000
        req.setInsuranceAmount(new BigDecimal("30000.00"));
        req.setAccessoriesAmount(new BigDecimal("15000.00"));
        req.setHandlingCharges(new BigDecimal("2000.00"));
        req.setPriceValidUntil(LocalDate.of(2026, 12, 31));

        CityPriceResponse res = adminService.createCityPrice(req);

        assertNotNull(res);
        assertEquals(0, new BigDecimal("68000.00").compareTo(res.getRtoTaxAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(res.getTcsAmount()));
        // Total: 850000 + 68000 + 30000 + 15000 + 2000 + 0 = 965000.00
        assertEquals(0, new BigDecimal("965000.00").compareTo(res.getTotalOnRoadPrice()));
    }
}
