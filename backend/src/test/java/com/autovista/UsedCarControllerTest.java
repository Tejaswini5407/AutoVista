package com.autovista;

import com.autovista.dto.response.UsedCarResponse;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.service.UsedCarService;
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
class UsedCarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsedCarService usedCarService;

    @Test
    @DisplayName("GET /api/used-cars should return used cars list with masked registration")
    void shouldReturnUsedCarsList() throws Exception {
        UsedCarResponse usedCar = new UsedCarResponse();
        usedCar.setId(1L);
        usedCar.setBrand("Model C");
        usedCar.setVariantName("VX Diesel");
        usedCar.setPrice(new BigDecimal("845000.00"));
        usedCar.setOdometerKm(41200);
        usedCar.setMaskedRegistrationNumber("UP32 •• ••••");
        usedCar.setStatus("AVAILABLE");

        when(usedCarService.getUsedCarsByStatus("ALL")).thenReturn(List.of(usedCar));

        mockMvc.perform(get("/api/used-cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].maskedRegistrationNumber").value("UP32 •• ••••"))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("GET /api/used-cars/{id} should return single vehicle details")
    void shouldReturnUsedCarDetails() throws Exception {
        UsedCarResponse usedCar = new UsedCarResponse();
        usedCar.setId(2L);
        usedCar.setBrand("Model S");
        usedCar.setVariantName("LX Petrol");
        usedCar.setPrice(new BigDecimal("512000.00"));
        usedCar.setOdometerKm(58600);
        usedCar.setMaskedRegistrationNumber("UP32 •• ••••");
        usedCar.setStatus("BOOKED");

        when(usedCarService.getUsedCarById(2L)).thenReturn(usedCar);

        mockMvc.perform(get("/api/used-cars/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.status").value("BOOKED"));
    }

    @Test
    @DisplayName("GET /api/used-cars/{id} with missing ID should return 404")
    void shouldReturn404WhenNotFound() throws Exception {
        when(usedCarService.getUsedCarById(999L))
                .thenThrow(new ResourceNotFoundException("Pre-Owned Car", "id", 999L));

        mockMvc.perform(get("/api/used-cars/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
