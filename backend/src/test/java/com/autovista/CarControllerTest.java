package com.autovista;

import com.autovista.dto.response.CarModelResponse;
import com.autovista.dto.response.CarVariantResponse;
import com.autovista.dto.response.CityPriceResponse;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.service.CarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    @DisplayName("GET /api/cars should return list of active car models")
    void shouldReturnCarModelsList() throws Exception {
        CarModelResponse model = new CarModelResponse();
        model.setId(1L);
        model.setBrand("Model H");
        model.setModelName("Model H");
        model.setYear(2026);

        when(carService.getAllCarModels()).thenReturn(List.of(model));

        mockMvc.perform(get("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].modelName").value("Model H"));
    }

    @Test
    @DisplayName("GET /api/cars/{id} should return car model details")
    void shouldReturnCarModelDetails() throws Exception {
        CarModelResponse model = new CarModelResponse();
        model.setId(1L);
        model.setBrand("Model H");
        model.setModelName("Model H");
        model.setYear(2026);

        CarVariantResponse variant = new CarVariantResponse();
        variant.setId(1L);
        variant.setVariantName("ZX Automatic Petrol");
        variant.setExShowroomPrice(new BigDecimal("1245000.00"));
        variant.setFuelType("PETROL");
        variant.setTransmission("AUTOMATIC");
        model.setVariants(List.of(variant));

        when(carService.getCarModelById(1L)).thenReturn(model);

        mockMvc.perform(get("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.variants[0].variantName").value("ZX Automatic Petrol"))
                .andExpect(jsonPath("$.data.variants[0].exShowroomPrice").value(1245000.00));
    }

    @Test
    @DisplayName("GET /api/cars/{id} with invalid id should return 404")
    void shouldReturn404WhenModelNotFound() throws Exception {
        when(carService.getCarModelById(999L))
                .thenThrow(new ResourceNotFoundException("Car Model", "id", 999L));

        mockMvc.perform(get("/api/cars/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/cars/variants/{id}/pricing should return on-road calculation breakdown")
    void shouldReturnOnRoadPricingBreakdown() throws Exception {
        CityPriceResponse pricing = new CityPriceResponse();
        pricing.setId(1L);
        pricing.setCarVariantId(1L);
        pricing.setVariantName("ZX Automatic Petrol");
        pricing.setCity("Lucknow");
        pricing.setExShowroomPrice(new BigDecimal("1245000.00"));
        pricing.setRtoTaxAmount(new BigDecimal("124500.00"));
        pricing.setInsuranceAmount(new BigDecimal("43575.00"));
        pricing.setTotalOnRoadPrice(new BigDecimal("1450525.00"));

        when(carService.getPricingForVariantAndCity(1L, "Lucknow")).thenReturn(pricing);

        mockMvc.perform(get("/api/cars/variants/1/pricing?city=Lucknow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.city").value("Lucknow"))
                .andExpect(jsonPath("$.data.totalOnRoadPrice").value(1450525.00));
    }
}
