package com.autovista;

import com.autovista.dto.request.CarModelRequest;
import com.autovista.dto.request.CityPriceRequest;
import com.autovista.dto.request.UsedCarRequest;
import com.autovista.dto.response.AdminStatsResponse;
import com.autovista.dto.response.AdminUsedCarResponse;
import com.autovista.dto.response.CarModelResponse;
import com.autovista.dto.response.CityPriceResponse;
import com.autovista.entity.UsedCarStatus;
import com.autovista.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Test
    @DisplayName("Unauthenticated request to /api/admin/stats should be rejected with 401 or 403")
    void unauthenticatedAccessShouldFail() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("CUSTOMER role accessing /api/admin/stats should receive 403 Forbidden")
    void customerRoleCannotAccessAdminStats() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "consultant@autovista.in", roles = "CONSULTANT")
    @DisplayName("CONSULTANT role accessing /api/admin/stats should receive 403 Forbidden")
    void consultantRoleCannotAccessAdminStats() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@autovista.in", roles = "ADMIN")
    @DisplayName("ADMIN role accessing /api/admin/stats should return 200 OK with dashboard stats")
    void adminCanAccessAdminStats() throws Exception {
        AdminStatsResponse stats = new AdminStatsResponse(4, 12, 8, 6, 1, 1, 12);
        when(adminService.getAdminStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalNewModels").value(4))
                .andExpect(jsonPath("$.data.totalNewVariants").value(12))
                .andExpect(jsonPath("$.data.usedAvailable").value(6));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("CUSTOMER role calling POST /api/admin/cars should receive 403 Forbidden")
    void customerCannotCreateCarModel() throws Exception {
        CarModelRequest req = new CarModelRequest();
        req.setBrand("Model H");
        req.setModelName("Model H");
        req.setBodyType("Sedan");
        req.setYear(2026);

        mockMvc.perform(post("/api/admin/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@autovista.in", roles = "ADMIN")
    @DisplayName("ADMIN role creating new car model should return 201 Created")
    void adminCanCreateCarModel() throws Exception {
        CarModelRequest req = new CarModelRequest();
        req.setBrand("AutoVista");
        req.setModelName("Imperial GT");
        req.setBodyType("Sedan");
        req.setYear(2026);
        req.setIsActive(true);

        CarModelResponse res = new CarModelResponse();
        res.setId(99L);
        res.setBrand("AutoVista");
        res.setModelName("Imperial GT");
        res.setBodyType("Sedan");
        res.setYear(2026);
        res.setIsActive(true);

        when(adminService.createModel(any(CarModelRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/admin/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(99))
                .andExpect(jsonPath("$.data.modelName").value("Imperial GT"));
    }

    @Test
    @WithMockUser(username = "admin@autovista.in", roles = "ADMIN")
    @DisplayName("ADMIN calling POST /api/admin/cars with invalid data should return 400 Bad Request")
    void adminCreateCarModelValidationFails() throws Exception {
        CarModelRequest req = new CarModelRequest();
        // Missing brand and modelName and year

        mockMvc.perform(post("/api/admin/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@autovista.in", roles = "ADMIN")
    @DisplayName("ADMIN updating used car status to SOLD should return 200 OK")
    void adminCanUpdateUsedCarStatus() throws Exception {
        AdminUsedCarResponse res = new AdminUsedCarResponse();
        res.setId(1L);
        res.setModelName("City V");
        res.setStatus("SOLD");
        res.setMaskedRegistrationNumber("UP32 •• ••••");
        res.setRawRegistrationNumber("UP32 AB 1234");

        when(adminService.updateUsedCarStatus(eq(1L), eq(UsedCarStatus.SOLD))).thenReturn(res);

        mockMvc.perform(patch("/api/admin/used-cars/1/status?status=SOLD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SOLD"))
                .andExpect(jsonPath("$.data.maskedRegistrationNumber").value("UP32 •• ••••"));
    }

    @Test
    @WithMockUser(username = "admin@autovista.in", roles = "ADMIN")
    @DisplayName("ADMIN configuring city pricing should return 201 Created with computed breakdown")
    void adminCanConfigurePricing() throws Exception {
        CityPriceRequest req = new CityPriceRequest();
        req.setCarVariantId(1L);
        req.setCity("Lucknow");
        req.setState("Uttar Pradesh");
        req.setRtoTaxPercent(new BigDecimal("10.00"));
        req.setInsuranceAmount(new BigDecimal("43575.00"));
        req.setAccessoriesAmount(new BigDecimal("25000.00"));
        req.setHandlingCharges(new BigDecimal("5000.00"));
        req.setPriceValidUntil(LocalDate.now().plusMonths(1));

        CityPriceResponse res = new CityPriceResponse();
        res.setId(1L);
        res.setCarVariantId(1L);
        res.setCity("Lucknow");
        res.setExShowroomPrice(new BigDecimal("1245000.00"));
        res.setRtoTaxPercent(new BigDecimal("10.00"));
        res.setRtoTaxAmount(new BigDecimal("124500.00"));
        res.setInsuranceAmount(new BigDecimal("43575.00"));
        res.setAccessoriesAmount(new BigDecimal("25000.00"));
        res.setHandlingCharges(new BigDecimal("5000.00"));
        res.setTcsAmount(new BigDecimal("12450.00"));
        res.setTotalOnRoadPrice(new BigDecimal("1455525.00"));

        when(adminService.createCityPrice(any(CityPriceRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/admin/pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.city").value("Lucknow"))
                .andExpect(jsonPath("$.data.tcsAmount").value(12450.00))
                .andExpect(jsonPath("$.data.totalOnRoadPrice").value(1455525.00));
    }
}
