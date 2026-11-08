# AutoVista — Technical Design Document

**Project:** Automotive Dealership Digital Showroom & Lead Management Platform  
**Target Organization:** Isocode Labs Engineering Evaluation  
**System Version:** 1.0.0-SNAPSHOT  
**Architect:** AutoVista Development Team  
**Date:** November 2026

---

## 1. Problem Statement

Modern automotive retail operations in India face significant operational fragmentation:
1. **Opaque Pricing**: Customers encounter hidden charges and inconsistent on-road pricing estimates that vary substantially across states and municipal jurisdictions (e.g., UP RTO taxes, TCS, registration surcharges).
2. **Disconnected Inventory**: Dealerships struggle to showcase both newly launched models and certified pre-owned vehicles with standardized inspection histories and privacy protections (such as masking vehicle registration marks).
3. **Lead Bottlenecks**: Inbound customer enquiries often languish due to manual lead assignment, lack of response-time SLAs, and insufficient buyer context at the sales consultant's fingertips.
4. **Disjointed Test-Drive Scheduling**: Double-booking of vehicles and sales consultants leads to poor customer experiences and missed high-value opportunities.

**AutoVista** was engineered to solve these problems by providing an integrated full-stack platform connecting buyers, luxury sales consultants, and dealership principals in real time.

---

## 2. Functional Requirements

### 2.1 Customer Facing
- **Dynamic Catalog Discovery**: Interactive browsing of new vehicle models and certified pre-owned stock with multi-parameter filtering (fuel type, maximum price, status).
- **Localized On-Road Pricing Engine**: Calculation of comprehensive on-road vehicle cost for Lucknow and surrounding UP districts, broken down into ex-showroom, road tax, comprehensive insurance, accessories, handling charges, and TCS.
- **Privacy Protocols**: Automatic masking of registration plates for pre-owned stock (e.g., `UP 32 ** ****`).
- **VIP Test-Drive Scheduling**: Booking engine with showroom location selection, date picker, active vehicle availability checking, and conflict resolution.
- **Statutory Consent Compliance**: Explicit opt-in checkbox for customer contact storage and marketing notifications per Indian digital communication regulations.

### 2.2 Sales Consultant Desk
- **Automated Lead Ingestion & Allocation**: Inbound customer vehicle enquiries automatically assigned to active consultants via round-robin distribution with a 5-minute initial contact SLA.
- **Lead Detail & Buyer Profiling**: Inspection of buyer preferences, financing readiness, timeline, and short-listed models.
- **Lifecycle Status Updates**: Transition leads through structured states: `NEW` -> `CONTACTED` -> `TEST_DRIVE_SCHEDULED` -> `NEGOTIATION` -> `WON` / `LOST`.
- **AI-Powered Response Assistant**: Pre-computed context-aware buyer communication drafts incorporating vehicle specifications and financing recommendations.

### 2.3 Dealership Admin Console
- **Executive KPI Dashboard**: Aggregate metrics tracking total models, pre-owned inventory, test-drive volume, lead conversion rates, and SLA adherence.
- **Inventory Lifecycle Management**: CRUD operations on models and variants, availability toggles, and pre-owned certification logging.
- **Pricing Configuration Rules**: Administrative control over regional road tax percentages, mandatory handling charges, and TCS thresholds.
- **Lead Reassignment & Audit Trail**: Manual override of lead assignments with timestamped audit logs for management oversight.

---

## 3. System Architecture

The AutoVista platform follows a decoupled, three-tier enterprise architecture:

```
+-------------------------------------------------------------------------+
|                              PRESENTATION                               |
|                  React 19 SPA (Vite Bundler, Port 5173)                 |
|   +-------------------+  +---------------------+  +------------------+  |
|   |  Customer Portal  |  | Consultant Desk     |  | Dealership Admin |  |
|   +-------------------+  +---------------------+  +------------------+  |
+------------------------------------+------------------------------------+
                                     | HTTPS / JSON (Bearer JWT)
+------------------------------------v------------------------------------+
|                               APPLICATION                               |
|                     Spring Boot 3.4.2 REST API Server                   |
|   +-------------------+  +---------------------+  +------------------+  |
|   | Security & JWT    |  | Pricing Engine      |  | Lead Assignment  |  |
|   +-------------------+  +---------------------+  +------------------+  |
|   | Inventory Service |  | Booking Service     |  | Audit & Notifs   |  |
|   +-------------------+  +---------------------+  +------------------+  |
+------------------------------------+------------------------------------+
                                     | JDBC over SSL (HikariCP)
+------------------------------------v------------------------------------+
|                               PERSISTENCE                               |
|                    PostgreSQL 17 Database (Neon Cloud)                  |
|  [users] [car_models] [car_variants] [used_cars] [leads] [bookings]     |
+-------------------------------------------------------------------------+
```

---

## 4. Frontend Architecture

### 4.1 Component & Page Organization
- **Pages**:
  - `HomePage.jsx`: Brand hero, quick search, featured models.
  - `CarsPage.jsx`: New cars catalog with reactive fuel/price filters.
  - `UsedCarsPage.jsx`: Pre-owned vehicles with certification status.
  - `CarDetailsPage.jsx`: Spec sheet, color visualizer, and dynamic pricing breakdown card.
  - `UsedCarDetailsPage.jsx`: 150-point inspection checklist, odometer log.
  - `LoginPage.jsx`: Multi-persona login with loading indicators and error banners.
  - `RegisterPage.jsx`: Account creation with statutory consent opt-in.
  - `BookTestDrivePage.jsx`: Real-time calendar slot selector and model pre-selection.
  - `ConsultantDashboardPage.jsx`: Master-detail lead management workbench.
  - `AdminDashboardPage.jsx`: Tabbed executive dashboard (Inventory, Pre-Owned, Pricing, Bookings, Leads).

### 4.2 State Management & Security
- **Authentication Context (`AuthProvider.jsx`)**:
  - Stores authenticated user object in state and synchronizes with `localStorage`.
  - Roles are **strictly** derived from backend token validation; demo quick-login calls real API endpoints (`/api/auth/login`).
  - Implements route guards restricting `/consultant/*` to `CONSULTANT` and `/admin/*` to `ADMIN`.
- **API Client (`apiClient.js`)**:
  - Dynamically determines the API base URL based on host environment (`localhost:8080` vs Render backend).
  - Enforces a 60-second `AbortController` timeout to accommodate cold-starts on containerized infrastructure.
  - Automatically injects `Authorization: Bearer <token>` into outgoing requests.

---

## 5. Backend Architecture

### 5.1 Technology Foundation
- **Spring Boot 3.4.2** running on **Java 21 LTS** leveraging virtual threads and modern pattern matching.
- **Spring Security 6.4**: Stateless authentication filter pipeline.
  - `JwtAuthenticationFilter`: Extracts and parses JWT from `Authorization` header.
  - `JwtTokenProvider`: Issues tokens signed with HMAC-SHA256 (256-bit secret) with 24-hour expiration.
  - Method-level role enforcement via `@PreAuthorize("hasRole('ADMIN')")` and URL security matchers.
- **Spring Data JPA & Hibernate 6.6**: Entity mapping, transactional boundaries (`@Transactional`), and optimistic locking.

### 5.2 Key Services
1. **PricingEngineService**:
   - Calculates on-road costs using parametric state rules.
   - Computes:
     $$\text{Road Tax} = \text{Ex-Showroom} \times \left(\frac{\text{RTO Rate}}{100}\right)$$
     $$\text{TCS} = \text{Ex-Showroom} \times 0.01 \quad (\text{if Ex-Showroom} > ₹10,00,000)$$
     $$\text{Total On-Road} = \text{Ex-Showroom} + \text{Tax} + \text{Insurance} + \text{Accessories} + \text{Handling} + \text{TCS}$$
2. **LeadAssignmentService**:
   - Dispatches customer enquiries to consultants with lowest current active load.
   - Attaches a strict 5-minute response SLA timestamp (`slaDueAt`).
   - Prevents duplicate enquiries from the same buyer within a 10-minute cooldown window.
3. **TestDriveBookingService**:
   - Validates showroom business hours (09:30 AM – 08:00 PM IST).
   - Enforces unique active booking slot keys at the database level (`showroom_id + car_id + booking_date + time_slot`), eliminating concurrency race conditions.

---

## 6. Database Schema Overview

```sql
-- 1. Identity & RBAC
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL, -- 'CUSTOMER', 'CONSULTANT', 'ADMIN'
    city VARCHAR(100) DEFAULT 'Lucknow',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. New Car Catalog
CREATE TABLE car_models (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    body_type VARCHAR(50),
    year INTEGER NOT NULL,
    hero_image_url TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE car_variants (
    id BIGSERIAL PRIMARY KEY,
    car_model_id BIGINT REFERENCES car_models(id),
    variant_name VARCHAR(100) NOT NULL,
    fuel_type VARCHAR(20) NOT NULL,
    transmission VARCHAR(20) NOT NULL,
    ex_showroom_price NUMERIC(12, 2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

-- 3. Regional Pricing
CREATE TABLE city_prices (
    id BIGSERIAL PRIMARY KEY,
    car_variant_id BIGINT REFERENCES car_variants(id),
    city VARCHAR(100) NOT NULL,
    rto_tax_percent NUMERIC(5, 2) NOT NULL,
    insurance_amount NUMERIC(10, 2) NOT NULL,
    accessories_amount NUMERIC(10, 2) DEFAULT 0,
    handling_charges NUMERIC(10, 2) DEFAULT 0,
    tcs_amount NUMERIC(10, 2) DEFAULT 0,
    total_on_road_price NUMERIC(12, 2) NOT NULL
);

-- 4. Certified Pre-Owned
CREATE TABLE used_cars (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    manufacturing_year INTEGER NOT NULL,
    odometer_km INTEGER NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    masked_registration_number VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE'
);

-- 5. Customer Leads & Assignments
CREATE TABLE leads (
    id BIGSERIAL PRIMARY KEY,
    lead_ref VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT REFERENCES users(id),
    car_model_id BIGINT REFERENCES car_models(id),
    status VARCHAR(30) DEFAULT 'NEW',
    assigned_consultant_id BIGINT REFERENCES users(id),
    sla_due_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. Test-Drive Bookings
CREATE TABLE test_drive_bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_ref VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT REFERENCES users(id),
    car_variant_id BIGINT REFERENCES car_variants(id),
    showroom_id BIGINT REFERENCES showrooms(id),
    booking_date DATE NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    status VARCHAR(30) DEFAULT 'CONFIRMED',
    active_slot_key VARCHAR(100) UNIQUE
);
```

---

## 7. Important Business Workflows

### 7.1 Customer Vehicle Enquiry to Consultant Lead
1. Customer views `/cars/1` and clicks **"Enquire for Lucknow Showroom"**.
2. If unauthenticated, user is prompted to sign in or use one-click demo login.
3. Form payload `{ carModelId, preferredColor, financingRequired, comments }` is submitted to `POST /api/leads`.
4. `LeadService` verifies the 10-minute duplicate submission window.
5. `LeadAssignmentService` finds an available consultant (`vikram.consultant@autovista.in`) and computes `slaDueAt = now() + 5 minutes`.
6. Lead record is committed with reference `AV-LD-YYYY-XXXXXXXX`.
7. Consultant refreshes workbench or logs in to inspect new lead and begin customer outreach.

### 7.2 Double-Booking Prevention in Test Drives
1. Customer initiates test-drive booking for Model H ZX at Gomti Nagar Showroom on date `2026-11-15` at slot `02:00 PM - 03:00 PM`.
2. Backend computes synthetic slot key: `LKO-01:VAR-1:2026-11-15:SLOT-02`.
3. Database unique constraint on `active_slot_key` guarantees atomicity. If a concurrent request attempts to reserve the identical vehicle-date-slot, PostgreSQL throws a constraint violation which Spring Boot's `GlobalExceptionHandler` converts into an informative `409 Conflict: Slot already booked`.

---

## 8. Testing Strategy

1. **Unit Testing (JUnit 5 & Mockito)**:
   - Verifies business calculations (pricing breakdowns, tax slabs, token signing).
2. **Spring Boot Security Tests (`AdminControllerSecurityTest`)**:
   - Asserts role protection on administrative and consultant routes. Unauthenticated and non-admin requests receive HTTP 401 or 403.
3. **Integration Testing (`LeadManagementIntegrationTest`)**:
   - Validates end-to-end database transactions, lead assignment round-robin logic, and SLA timestamp calculations against in-memory H2.
4. **Live API Verification (`test_live_api_flows.mjs`)**:
   - 28 real HTTP requests against the active Spring Boot backend running with Neon PostgreSQL.
5. **Headless Browser Automated Workflows**:
   - Navigates through all 15 key screens, verifies UI rendering, checks tab switches, and captures visual artifacts.

---

## 9. Design Decisions & Limitations

1. **PostgreSQL on Neon vs Local H2**:
   - Neon cloud PostgreSQL is used for the active application instance to guarantee persistent data fidelity and realistic network latency.
   - H2 is restricted strictly to the Maven test profile (`application-test.yml`) to ensure test repeatability without altering production data.
2. **Local Demonstration Priority**:
   - Due to Render free-tier cold-start latency (45–55 seconds) and Vercel routing complexities during evaluation windows, the project is configured for immediate local execution (`http://localhost:5173`).
3. **Data Privacy**:
   - Sensitive customer data and vehicle registration numbers are masked in all public catalog representations.
