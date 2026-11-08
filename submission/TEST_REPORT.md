# AutoVista — Quality Assurance & Test Execution Report

**Evaluation Cycle:** Isocode Labs Take-Home Technical Review  
**Date:** November 2026  
**Execution Environment:** Windows 11, Node.js v24.14.0, OpenJDK 21.0.9, Maven 3.9.16  
**Backend:** Spring Boot 3.4.2 + PostgreSQL 17 (Neon Cloud)  
**Frontend:** React 19 + Vite 8.3.5

---

## 1. Executive Summary

| Category | Suite / Target | Total Run | Passed | Failed | Status |
|---|---|---|---|---|---|
| **Backend Suite** | `mvn test` (JUnit 5 + Spring Boot Test) | 53 | 53 | 0 | **PASSED** |
| **API Live Integration** | `test_live_api_flows.mjs` (REST over HTTP) | 28 | 28 | 0 | **PASSED** |
| **Frontend Static Quality** | ESLint (`npm run lint`) | 58 modules | 0 errors | 0 warnings | **PASSED** |
| **Frontend Production Build** | Vite Build (`npm run build`) | 58 modules | 58 built | 0 errors | **PASSED** |
| **Automated Browser Workflows** | Puppeteer Chrome Headless Capture | 15 views | 15 captured | 0 failures | **PASSED** |
| **Client-Side Auth Simulation** | `test_auth_flows.mjs` | 9 | 9 | 0 | **PASSED** |

---

## 2. Real Backend Test Results (`mvn test`)

The backend test suite executed against an isolated in-memory H2 database using the `test` profile (`application-test.yml`), ensuring zero data mutation in the persistent Neon PostgreSQL database.

```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.autovista.AdminControllerSecurityTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 28.5 s
[INFO] Running com.autovista.AutoVistaApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.2 s
[INFO] Running com.autovista.CarControllerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.1 s
[INFO] Running com.autovista.HealthControllerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.1 s
[INFO] Running com.autovista.LeadManagementIntegrationTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.8 s
[INFO] Running com.autovista.PricingEngineServiceTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.3 s
[INFO] Running com.autovista.TestDriveBookingControllerTest
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.4 s
[INFO] Running com.autovista.UsedCarControllerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.8 s
[INFO] 
[INFO] Results:
[INFO] Tests run: 53, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time:  01:18 min
[INFO] ------------------------------------------------------------------------
```

### Key Validated Scenarios
- **Role-Based Access Control**: Verified that unauthenticated requests to `/api/admin/**` and `/api/consultant/**` receive `401 Unauthorized`, and unauthorized roles (e.g. `ROLE_CUSTOMER` accessing consultant endpoints) receive `403 Forbidden`.
- **Concurrency & Anti-Double-Booking**: Tested concurrent test-drive bookings for identical vehicle/showroom/time slot; verified that database-level active slot keys reject overlapping slots with `409 Conflict`.
- **Lead Duplicate Prevention**: Verified that duplicate vehicle enquiries from the same buyer within a 10-minute window are safely rejected.
- **On-Road Pricing Calculation**: Verified UP RTO rate mathematics, TCS liability thresholds (> ₹10 Lakhs), insurance formulas, and total price aggregation.

---

## 3. Real Live API Integration Results

The `test_live_api_flows.mjs` test runner executed 28 real HTTP network transactions against the live Spring Boot server connected to Neon PostgreSQL.

| # | Test Assertion | HTTP Method & Path | Expected Result | Status |
|---|---|---|---|---|
| 1 | Health Endpoint Check | `GET /api/health` | 200 OK, `status: UP`, DB Connected | **PASS** |
| 2 | Customer Demo Login | `POST /api/auth/login` | 200 OK, User found | **PASS** |
| 3 | Customer Role Verification | - | `role === 'CUSTOMER'` | **PASS** |
| 4 | Customer JWT Issuance | - | Valid HMAC-SHA256 Token present | **PASS** |
| 5 | Consultant Demo Login | `POST /api/auth/login` | 200 OK, User found | **PASS** |
| 6 | Consultant Role Verification | - | `role === 'CONSULTANT'` | **PASS** |
| 7 | Consultant JWT Issuance | - | Valid JWT token present | **PASS** |
| 8 | Admin Demo Login | `POST /api/auth/login` | 200 OK, User found | **PASS** |
| 9 | Admin Role Verification | - | `role === 'ADMIN'` | **PASS** |
| 10 | Admin JWT Issuance | - | Valid JWT token present | **PASS** |
| 11 | Bad Password Rejection | `POST /api/auth/login` | 401 Unauthorized | **PASS** |
| 12 | Registration Input Validation | `POST /api/auth/register` | 400 Bad Request on invalid fields | **PASS** |
| 13 | Valid Account Registration | `POST /api/auth/register` | 201 Created with JWT | **PASS** |
| 14 | Registered User Role Default | - | Defaults to `ROLE_CUSTOMER` | **PASS** |
| 15 | New Cars Catalog Fetch | `GET /api/cars` | 200 OK with list of models | **PASS** |
| 16 | Catalog Model Count | `GET /api/cars` | Contains 3 distinct models | **PASS** |
| 17 | Model Entity Schema | - | Model has valid ID and name | **PASS** |
| 18 | Single Car Model Lookup | `GET /api/cars/1` | 200 OK with nested variants | **PASS** |
| 19 | Dynamic Variant Pricing | `GET /api/cars/variants/1/pricing` | 200 OK, on-road price > 0 | **PASS** |
| 20 | Used Cars Catalog Fetch | `GET /api/used-cars?status=ALL` | 200 OK with pre-owned list | **PASS** |
| 21 | Pre-Owned Inventory Count | `GET /api/used-cars` | Contains 3 certified vehicles | **PASS** |
| 22 | Single Pre-Owned Lookup | `GET /api/used-cars/1` | 200 OK with inspection details | **PASS** |
| 23 | Consultant Unauth Barrier | `GET /api/consultant/leads` | 401/403 when unauthenticated | **PASS** |
| 24 | Customer Role Rejection | `GET /api/consultant/leads` | 403 Forbidden for `CUSTOMER` | **PASS** |
| 25 | Consultant Access Granted | `GET /api/consultant/leads` | 200 OK with assigned lead list | **PASS** |
| 26 | Admin Unauth Barrier | `GET /api/admin/stats` | 401/403 when unauthenticated | **PASS** |
| 27 | Consultant Role Rejection | `GET /api/admin/stats` | 403 Forbidden for `CONSULTANT` | **PASS** |
| 28 | Admin Access Granted | `GET /api/admin/stats` | 200 OK with executive KPI stats | **PASS** |

---

## 4. Frontend Lint and Production Build

### ESLint Validation
```bash
npm --prefix frontend run lint
```
Output:
```text
> frontend@0.0.0 lint
> eslint .
Exit code: 0
```
*Zero syntax errors, zero unused variables, full compliance with React hooks rules.*

### Vite Production Build
```bash
npm --prefix frontend run build
```
Output:
```text
> frontend@0.0.0 build
> vite build

vite v8.3.5 building client environment for production...
transforming...
✓ 58 modules transformed.
rendering chunks...
computing gzip size...
dist/index.html                   1.08 kB │ gzip:   0.59 kB
dist/assets/index-DqYSok4l.css   35.71 kB │ gzip:   6.86 kB
dist/assets/index-VMKZSHN6.js   448.04 kB │ gzip: 123.11 kB
✓ built in 547ms
```

---

## 5. Automated Browser Workflow Test Results

A full headless Chrome session navigated the local application (`http://localhost:5173`) and captured genuine screenshot artifacts to `submission/screenshots/`:

| Index | Artifact File | Verified Elements & State | File Size |
|---|---|---|---|
| **01** | `01-homepage.png` | Hero banner, value propositions, featured inventory carousel | 570.6 KB |
| **02** | `02-new-cars-catalogue.png` | Model grid, powertrain filter pills, max-price slider | 411.8 KB |
| **03** | `03-used-cars-catalogue.png` | Pre-owned cards, 150-point inspection tags, masked plates | 453.9 KB |
| **04** | `04-new-car-details-pricing.png` | Spec sheet, variant selector, live on-road price quote card | 270.1 KB |
| **05** | `05-used-car-details.png` | Vehicle condition, verified odometer reading, service history | 420.3 KB |
| **06** | `06-customer-login.png` | Form inputs, role-based quick demo access buttons | 80.3 KB |
| **07** | `07-customer-registration.png` | Full registration form, mandatory statutory consent checkbox | 85.2 KB |
| **08** | `08-test-drive-booking.png` | Location selector, calendar date picker, time slot buttons | 235.3 KB |
| **09** | `09-enquiry-confirmation.png` | Modal dialog, color selector, financing toggle, notes field | 149.4 KB |
| **10** | `10-consultant-dashboard.png` | Sales consultant workbench, lead list, status badges | 157.2 KB |
| **11** | `11-consultant-lead-management.png` | Lead inspector, buyer profile, response SLA countdown timer | 157.4 KB |
| **12** | `12-admin-dashboard.png` | Executive metrics, test-drive counts, lead conversion charts | 99.4 KB |
| **13** | `13-admin-inventory-management.png` | Model list, variant configuration, active availability switches | 110.1 KB |
| **14** | `14-admin-pricing-management.png` | City on-road pricing engine parameter adjustment interface | 117.0 KB |
| **15** | `15-admin-bookings-leads.png` | Central test-drive bookings table, consultant reassignment | 107.1 KB |

---

## 6. Distinction: Real Executed Tests vs. Simulated Tests

- **Real Executed Tests**:
  - `mvn test`: 53 unit and integration tests executed by the Maven surefire plugin against real Java classes and an in-memory SQL database.
  - `test_live_api_flows.mjs`: 28 real HTTP REST requests made via Node.js `fetch()` against the active Spring Boot backend and Neon PostgreSQL database.
  - Headless Browser Capture: Real Chrome instance launched, loaded DOM nodes, made HTTP network requests, and rendered pixels.
  - ESLint & Vite Build: Real node tools executed and generated production JS/CSS assets.
- **Simulated Tests**:
  - `test_auth_flows.mjs`: A lightweight mock-fetch test harness used during early UI prototyping to verify React Router transition logic independently of backend availability.

---

## 7. Known Limitations & Environment Considerations

1. **Free-Tier Cloud Latency**:
   - The remote backend on Render will enter a sleep state after periods of inactivity. An initial request may take up to 55 seconds to respond. The frontend has been configured with a 60-second timeout to handle this gracefully.
2. **Evaluation Recommendation**:
   - For immediate, deterministic evaluation, run the application locally on `http://localhost:5173` with backend on `http://localhost:8080`.
