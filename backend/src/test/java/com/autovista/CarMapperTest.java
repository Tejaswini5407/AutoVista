package com.autovista;

import com.autovista.dto.response.UsedCarResponse;
import com.autovista.entity.Showroom;
import com.autovista.entity.UsedCar;
import com.autovista.entity.UsedCarStatus;
import com.autovista.mapper.CarMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarMapperTest {

    private CarMapper carMapper;

    @BeforeEach
    void setUp() {
        carMapper = new CarMapper();
    }

    @Test
    @DisplayName("Mandatory Requirement #7: Mask standard UP registration numbers")
    void shouldMaskStandardRegistrationNumber() {
        String raw = "UP32 BK 8921";
        String masked = carMapper.maskRegistrationNumber(raw);
        assertEquals("UP32 •• ••••", masked);
    }

    @Test
    @DisplayName("Mandatory Requirement #7: Mask hyphenated registration numbers")
    void shouldMaskHyphenatedRegistrationNumber() {
        String raw = "UP-32-DA-4410";
        String masked = carMapper.maskRegistrationNumber(raw);
        assertEquals("UP32 •• ••••", masked);
    }

    @Test
    @DisplayName("UsedCarResponse must expose masked registration number and never raw string")
    void shouldMapUsedCarWithMaskedRegistration() {
        Showroom showroom = new Showroom();
        showroom.setId(1L);
        showroom.setName("AutoVista Flagship Gomti Nagar");
        showroom.setCity("Lucknow");

        UsedCar usedCar = new UsedCar();
        usedCar.setId(10L);
        usedCar.setShowroom(showroom);
        usedCar.setBrand("Model C");
        usedCar.setModelName("Model C");
        usedCar.setVariantName("VX Diesel");
        usedCar.setManufacturingYear(2022);
        usedCar.setPrice(new BigDecimal("845000.00"));
        usedCar.setOdometerKm(41200);
        usedCar.setRawRegistrationNumber("UP32 BK 8921");
        usedCar.setFuelType("DIESEL");
        usedCar.setTransmission("MANUAL");
        usedCar.setStatus(UsedCarStatus.AVAILABLE);

        UsedCarResponse response = carMapper.toUsedCarResponse(usedCar);

        assertNotNull(response);
        assertEquals("UP32 •• ••••", response.getMaskedRegistrationNumber());
        assertFalse(response.getMaskedRegistrationNumber().contains("8921"));
    }
}
