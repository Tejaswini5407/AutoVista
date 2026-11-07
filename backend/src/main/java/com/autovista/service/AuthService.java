package com.autovista.service;

import com.autovista.dto.request.LoginRequest;
import com.autovista.dto.request.RegisterRequest;
import com.autovista.dto.response.AuthResponse;
import com.autovista.dto.response.UserResponse;
import com.autovista.entity.Role;
import com.autovista.entity.User;
import com.autovista.exception.DuplicateBookingException;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.repository.UserRepository;
import com.autovista.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateBookingException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getPhone(),
                Role.CUSTOMER,
                request.getCity() != null ? request.getCity() : "Lucknow"
        );

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateTokenFromEmail(
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getId()
        );

        UserResponse userResponse = new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getPhone(),
                savedUser.getRole().name(),
                savedUser.getCity()
        );

        return new AuthResponse(token, userResponse);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name(),
                user.getCity()
        );

        return new AuthResponse(token, userResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name(),
                user.getCity()
        );
    }
}
