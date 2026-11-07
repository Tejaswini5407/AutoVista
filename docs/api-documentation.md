# AutoVista API Documentation

This document describes the RESTful API endpoints for the **AutoVista – Premium Automotive Dealership Platform**.

- **Base URL (Local Development)**: `http://localhost:8080`
- **Default Format**: JSON (`Content-Type: application/json`)
- **Authentication**: Bearer Token in `Authorization` header (`Authorization: Bearer <jwt-token>`)

---

## 1. Response & Error Schema Standards

### Standard Success Response (`ApiResponse<T>`)
```json
{
  "success": true,
  "message": "Operation successful description",
  "data": { ... },
  "timestamp": "2026-11-07T08:30:00.000Z"
}
```

### Standard Error Response (`ErrorResponse`)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/api/...",
  "validationErrors": {
    "email": "Email must be a valid email address",
    "password": "Password must be at least 8 characters"
  },
  "timestamp": "2026-11-07T08:30:00.000Z"
}
```

---

## 2. Phase 2 Implemented Endpoints

### 2.1 System & Health

#### `GET /api/health`
Health check endpoint reporting application readiness, database connectivity, and record counts.

- **Access**: Public
- **Response**: `200 OK`
```json
{
  "status": "UP",
  "message": "AutoVista backend is healthy and connected to PostgreSQL",
  "timestamp": "2026-11-07T08:30:00.000Z",
  "databaseConnected": true,
  "databaseProduct": "PostgreSQL",
  "databaseVersion": "17.10",
  "totalCarModels": 3,
  "totalCarVariants": 3,
  "totalUsedCars": 3,
  "totalShowrooms": 1
}
```

---

### 2.2 Authentication & Authorization

#### `POST /api/auth/register`
Register a new customer account.

- **Access**: Public
- **Request Body**:
```json
{
  "name": "Aarav Sharma",
  "email": "aarav.sharma@example.com",
  "phone": "+91 98765 43210",
  "password": "SecurePassword123!"
}
```
- **Validation**:
  - `name`: 2 to 100 characters, required.
  - `email`: Valid RFC 5322 format, required.
  - `phone`: Required, Indian/international format.
  - `password`: Minimum 8 characters, required.
- **Response**: `201 Created`
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 4,
      "name": "Aarav Sharma",
      "email": "aarav.sharma@example.com",
      "phone": "+91 98765 43210",
      "role": "ROLE_CUSTOMER"
    }
  },
  "timestamp": "2026-11-07T08:30:00.000Z"
}
```

#### `POST /api/auth/login`
Authenticate with email and password to receive a JWT.

- **Access**: Public
- **Request Body**:
```json
{
  "email": "customer@autovista.in",
  "password": "Password123!"
}
```
- **Response**: `200 OK` (Same schema as registration)
- **Error Responses**:
  - `401 Unauthorized`: "Invalid email or password"

#### `GET /api/auth/me`
Retrieve profile of currently authenticated user.

- **Access**: Authenticated (`CUSTOMER`, `CONSULTANT`, or `ADMIN`)
- **Headers**: `Authorization: Bearer <jwt-token>`
- **Response**: `200 OK`
```json
{
  "success": true,
  "message": "Current user profile fetched successfully",
  "data": {
    "id": 3,
    "name": "Amit Verma",
    "email": "customer@autovista.in",
    "phone": "+91 98765 43210",
    "role": "ROLE_CUSTOMER"
  },
  "timestamp": "2026-11-07T08:30:00.000Z"
}
```

---

### 2.3 New Cars Inventory & Pricing Breakdown

#### `GET /api/cars`
List all active car models along with their variants, fuel types, transmissions, and pricing.

- **Access**: Public
- **Query Parameters**:
  - `fuelType` *(optional, string)*: Filter by `PETROL`, `DIESEL`, `ELECTRIC`, `HYBRID`
  - `transmission` *(optional, string)*: Filter by `MANUAL`, `AUTOMATIC`
- **Response**: `200 OK`
```json
{
  "success": true,
  "message": "Active car models fetched successfully",
  "data": [
    {
      "id": 1,
      "modelCode": "MOD-H-2026",
      "name": "Model H",
      "tagline": "The Pinnacle of Urban Luxury",
      "bodyType": "SUV",
      "year": 2026,
      "minExShowroomPrice": 1245000.00,
      "maxExShowroomPrice": 1245000.00,
      "imageUrl": "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&q=80&w=1200",
      "active": true,
      "variants": [
        {
          "id": 1,
          "variantName": "ZX Automatic",
          "fuelType": "PETROL",
          "transmission": "AUTOMATIC",
          "engineCapacityCc": 1498,
          "exShowroomPrice": 1245000.00,
          "keyFeatures": [
            "Panoramic Sunroof",
            "ADAS Level 2",
            "10.25-inch Touchscreen",
            "Ventilated Leather Seats",
            "6 Airbags Standard"
          ],
          "active": true
        }
      ]
    }
  ],
  "timestamp": "2026-11-07T08:30:00.000Z"
}
```

#### `GET /api/cars/{id}`
Retrieve a specific car model by ID with all associated variants.

- **Access**: Public
- **Response**: `200 OK` (Single `CarModelResponse`)
- **Error Responses**:
  - `404 Not Found`: "Car model not found with ID: {id}"

#### `GET /api/cars/variants/{variantId}/pricing`
Fetch city-wise on-road price calculation breakdown for a specific car variant.

- **Access**: Public
- **Query Parameters**:
  - `city` *(optional, string, default: `Lucknow`)*
- **Response**: `200 OK`
```json
{
  "success": true,
  "message": "On-road pricing calculated for city: Lucknow",
  "data": {
    "variantId": 1,
    "variantName": "ZX Automatic",
    "modelName": "Model H",
    "cityName": "Lucknow",
    "stateName": "Uttar Pradesh",
    "exShowroomPrice": 1245000.00,
    "roadTaxRto": 124500.00,
    "insurance": 56025.00,
    "registrationHandling": 5000.00,
    "standardAccessories": 20000.00,
    "totalOnRoadPrice": 1450525.00,
    "validUntil": "2026-12-31",
    "disclaimer": "Prices are indicative and subject to change without prior notice as per state RTO norms."
  },
  "timestamp": "2026-11-07T08:30:00.000Z"
}
```

---

### 2.4 Certified Used Cars

#### `GET /api/used-cars`
Retrieve certified pre-owned inventory.

> [!IMPORTANT]
> **Privacy Rule Enforcement**: Vehicle registration numbers are masked with bullets (`UP32 •• ••••`) and raw registration numbers are never exposed in any public response.

- **Access**: Public
- **Query Parameters**:
  - `status` *(optional, string)*: `AVAILABLE`, `BOOKED`, `SOLD` (defaults to available and booked)
  - `maxPrice` *(optional, numeric)*: Filter by maximum selling price
- **Response**: `200 OK`
```json
{
  "success": true,
  "message": "Used cars inventory fetched successfully",
  "data": [
    {
      "id": 1,
      "title": "Model C VX Diesel 2022",
      "modelName": "Model C",
      "variantName": "VX Diesel",
      "year": 2022,
      "mileageKm": 41200,
      "fuelType": "DIESEL",
      "transmission": "MANUAL",
      "price": 845000.00,
      "status": "AVAILABLE",
      "conditionScore": 4.6,
      "ownership": "First Owner",
      "maskedRegistrationNumber": "UP32 •• ••••",
      "city": "Lucknow",
      "imageUrl": "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&q=80&w=1200",
      "certificationDetails": "AutoVista 140-Point Inspection Passed. Comprehensive Warranty Included."
    },
    {
      "id": 2,
      "title": "Model S LX Petrol 2021",
      "modelName": "Model S",
      "variantName": "LX Petrol",
      "year": 2021,
      "mileageKm": 58600,
      "fuelType": "PETROL",
      "transmission": "MANUAL",
      "price": 512000.00,
      "status": "BOOKED",
      "conditionScore": 4.2,
      "ownership": "Second Owner",
      "maskedRegistrationNumber": "UP32 •• ••••",
      "city": "Lucknow",
      "imageUrl": "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&q=80&w=1200",
      "certificationDetails": "AutoVista 140-Point Inspection Passed."
    }
  ],
  "timestamp": "2026-11-07T08:30:00.000Z"
}
```

#### `GET /api/used-cars/{id}`
Retrieve a single pre-owned vehicle by ID.

- **Access**: Public
- **Response**: `200 OK` (Single `UsedCarResponse` with masked registration number)
- **Error Responses**:
  - `404 Not Found`: "Used car not found with ID: {id}"

---

## 3. Future Phase API Specification

The following endpoints represent the contract planned for subsequent phases. The database schema and entities created in Phase 2 already support all of these workflows.

### Phase 4: Pricing Engine & Admin Configuration
- `POST /api/pricing/calculate`
  - Dynamic on-road price calculation supporting optional accessories, fast-track registration, and warranty packages.
- `GET /api/admin/pricing/cities`
  - Manage tax percentages, RTO charges, and insurance formulas per city.
- `PUT /api/admin/pricing/cities/{id}`
  - Admin update of road tax rate, insurance rate, and accessories baseline.

### Phase 5: Test-Drive Booking & Anti-Collision Engine
- `POST /api/bookings`
  - Book a test drive slot for a car variant or used car.
  - Enforces database constraint: showroom, date, and slot must be unique.
  - Returns `409 Conflict` if duplicate slot is selected.
- `GET /api/bookings/my`
  - Customer view of active and past bookings.
- `GET /api/admin/bookings`
  - Admin/consultant schedule overview for showroom management.

### Phase 6: Lead Management & Gemini AI Integration
- `POST /api/leads`
  - Public enquiry creation with mandatory consent flag (`consentGiven: true`).
  - Automatically triggers lead assignment worker (under 5 minutes).
- `GET /api/consultant/leads`
  - Sales consultant dashboard view for assigned leads.
- `POST /api/ai/summary`
  - Backend-secured Google Gemini 1.5 call to generate concise buyer profile summary from enquiry text.
- `POST /api/ai/reply`
  - Generate polite, grounded first-reply draft without inventing discounts.
- `PUT /api/consultant/leads/{leadId}/approve-reply`
  - Human-in-the-loop: Consultant reviews, edits, and approves AI reply before simulated dispatch.

### Phase 7: Admin Inventory Management
- `POST /api/admin/cars` / `PUT /api/admin/cars/{id}`
- `POST /api/admin/used-cars` / `PUT /api/admin/used-cars/{id}`
- Status transitions for used cars (`AVAILABLE` -> `BOOKED` -> `SOLD`).
