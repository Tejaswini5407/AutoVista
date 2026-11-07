package com.autovista;

import com.autovista.dto.request.LoginRequest;
import com.autovista.dto.response.AuthResponse;
import com.autovista.dto.response.UserResponse;
import com.autovista.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/login with valid payload should return JWT token")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("aarav.sharma@example.com", "Password@123");
        UserResponse userResponse = new UserResponse(1L, "aarav.sharma@example.com", "Aarav Sharma", null, "CUSTOMER", "Lucknow");
        AuthResponse authResponse = new AuthResponse("mocked.jwt.token", userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.data.user.email").value("aarav.sharma@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login with invalid email should return 400 Bad Request")
    void shouldFailValidationOnBadEmail() throws Exception {
        LoginRequest request = new LoginRequest("not-an-email", "Password@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }
}
