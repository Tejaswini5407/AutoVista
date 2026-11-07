package com.autovista.controller;

import com.autovista.dto.request.LoginRequest;
import com.autovista.dto.request.RegisterRequest;
import com.autovista.dto.response.ApiResponse;
import com.autovista.dto.response.AuthResponse;
import com.autovista.dto.response.UserResponse;
import com.autovista.security.UserPrincipal;
import com.autovista.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(ApiResponse.ok("Registration successful", response), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponse response = authService.getCurrentUser(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Current user profile retrieved", response));
    }
}
