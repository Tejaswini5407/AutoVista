# AutoVista — Backend Architecture & Technical Design

## 1. Executive Summary

AutoVista is a production-quality automotive dealership platform built for an enterprise dealership in Lucknow, Uttar Pradesh. The backend is designed with a strict layered architecture in Java 21 and Spring Boot 3.4.2, emphasizing security, maintainability, type safety, and clean separation of concerns.

```
┌────────────────────────────────────────────────────────┐
│                   Client Layer                         │
│           (React 19 + Vite Frontend SPA)               │
└──────────────────────────┬─────────────────────────────┘
                           │ HTTP / JSON (JWT Bearer)
┌──────────────────────────▼─────────────────────────────┐
│                 Security & Filter Chain                │
│    - CorsFilter (Origins: localhost:5173, 3000)        │
│    - JwtAuthenticationFilter (JJWT 0.12.6 validation)  │
│    - SecurityFilterChain (Stateless Session)           │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                   Controller Layer                     │
│    - HealthController      - AuthController            │
│    - CarController         - UsedCarController         │
│    (GlobalExceptionHandler / Validation Advice)        │
└──────────────────────────┬─────────────────────────────┘
                           │ DTOs (Request / Response)
┌──────────────────────────▼─────────────────────────────┐
│                    Service Layer                       │
│    - HealthService         - AuthService               │
│    - CarService            - UsedCarService            │
│    - CarMapper (DTO Transformation & Reg Masking)      │
└──────────────────────────┬─────────────────────────────┘
                           │ Entities (Jakarta JPA)
┌──────────────────────────▼─────────────────────────────┐
│                  Repository Layer                      │
│    - UserRepository        - ShowroomRepository        │
│    - CarModelRepository    - CarVariantRepository      │
│    - UsedCarRepository     - CityPriceRepository       │
│    - TestDriveBookingRepo  - LeadRepository            │
│    - LeadAssignmentRepository                          │
└──────────────────────────┬─────────────────────────────┘
                           │ SQL / Flyway Migrations
┌──────────────────────────▼─────────────────────────────┐
│                  Database Layer                        │
│                 PostgreSQL 17.10                       │
│    (9 relational tables, constraints & indexes)        │
└────────────────────────────────────────────────────────┘
```

---

## 2. Layered Package Structure

The backend source code is organized under `com.autovista` inside `backend/src/main/java`:

| Package | Responsibility | Key Classes |
| :--- | :--- | :--- |
| **`com.autovista`** | Application bootstrap & configuration | `AutoVistaApplication.java` |
| **`config`** | Cross-cutting infrastructure configs | `SecurityConfig.java` (CORS bean, PasswordEncoder) |
| **`controller`** | REST controllers & HTTP request handling | `HealthController.java`, `AuthController.java`, `CarController.java`, `UsedCarController.java` |
| **`service`** | Core business logic & transactional services | `AuthService.java`, `CarService.java`, `UsedCarService.java`, `HealthService.java` |
| **`repository`** | Spring Data JPA persistence interfaces | `UserRepository.java`, `CarModelRepository.java`, `CarVariantRepository.java`, `UsedCarRepository.java`, etc. |
| **`entity`** | JPA Entity definitions & table mappings | `User.java`, `Showroom.java`, `CarModel.java`, `CarVariant.java`, `UsedCar.java`, `CityPrice.java`, `TestDriveBooking.java`, `Lead.java`, `LeadAssignment.java` |
| **`dto.request`** | Validated incoming request payloads | `LoginRequest.java`, `RegisterRequest.java` |
| **`dto.response`** | Standardized outgoing response payloads | `ApiResponse.java`, `ErrorResponse.java`, `AuthResponse.java`, `UserResponse.java`, `CarVariantResponse.java`, `UsedCarResponse.java`, `HealthResponse.java` |
| **`exception`** | Custom exceptions & central error handling | `GlobalExceptionHandler.java`, `ResourceNotFoundException.java`, `DuplicateBookingException.java` |
| **`security`** | Spring Security 6 & JWT architecture | `JwtTokenProvider.java`, `JwtAuthenticationFilter.java`, `UserPrincipal.java`, `CustomUserDetailsService.java` |
| **`mapper`** | Object-to-DTO transformation & masking | `CarMapper.java` (strictly masks vehicle registration numbers) |

---

## 3. Security Architecture

### Role-Based Access Control (RBAC)
Three distinct roles are configured:
1. **`ROLE_CUSTOMER`**: Can browse vehicles, view city pricing, submit leads with consent, and manage personal bookings.
2. **`ROLE_CONSULTANT`**: Accesses the Sales Consultant desk (`/api/consultant/**`), views assigned leads, and reviews AI draft replies.
3. **`ROLE_ADMIN`**: Complete access across dealership administration (`/api/admin/**`), inventory, and city pricing matrices.

### Stateless JWT Authentication Flow
1. User provides credentials to `POST /api/auth/login`.
2. `AuthService` delegates to Spring Security `AuthenticationManager`.
3. Password verified using **BCrypt** with work factor 10.
4. `JwtTokenProvider` signs an HMAC-SHA512 token containing user identity and role claims.
5. Client supplies `Authorization: Bearer <token>` on subsequent requests.
6. `JwtAuthenticationFilter` validates signature and expiration, populating the `SecurityContext`.

---

## 4. Exception Handling Strategy

All controllers are monitored by `GlobalExceptionHandler` (`@RestControllerAdvice`):
- `MethodArgumentNotValidException` ➔ `400 Bad Request` with structured field-level errors map.
- `ResourceNotFoundException` ➔ `404 Not Found`.
- `DuplicateBookingException` & `DataIntegrityViolationException` ➔ `409 Conflict` (intercepts unique constraints such as duplicate time-slot bookings).
- `BadCredentialsException` ➔ `401 Unauthorized`.
- `AccessDeniedException` ➔ `403 Forbidden`.
- `Exception` (unhandled) ➔ `500 Internal Server Error` with sanitized client message and detailed server-side logging.
