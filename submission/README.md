# AutoVista — Dealership Management & Digital Showroom Platform

**Isocode Labs Take-Home Engineering Submission**  
**Repository:** [https://github.com/Tejaswini5407/AutoVista](https://github.com/Tejaswini5407/AutoVista)  
**Live Backend Service (Render):** `https://autovista-backend.onrender.com`  
**Frontend Demonstration Target:** `http://localhost:5173` (Local Demonstration Prioritized)

---

## 1. Project Overview

AutoVista is a comprehensive, production-grade automotive dealership and digital showroom web application designed specifically for regional automotive retail operations (with Lucknow showroom localization). The system unifies customer vehicle discovery, dynamic on-road price calculation, VIP test-drive scheduling, statutory consent compliance, automated sales consultant lead assignment, and dealership executive management.

### Key Highlights
- **Role-Based Access Control (RBAC)**: Secure multi-persona portal supporting **Customers**, **Sales Consultants**, and **Dealership Admins**.
- **Transparent Localized Pricing**: Real-time regional pricing engine calculating on-road vehicle breakdown (Ex-showroom, UP RTO tax, comprehensive insurance, accessories, handling charges, and TCS).
- **Certified Pre-Owned Inventory**: 150-point inspection records, mileage logs, and customer data privacy via dynamic registration plate masking.
- **Consultant Productivity Desk**: Real-time SLA response countdowns, automated round-robin lead allocation, buyer intent profiling, and AI communication drafting.
- **Executive Admin Console**: Live metrics, inventory lifecycle controls, city tax parameter tuning, test-drive scheduling management, and lead reassignment audit trails.

---

## 2. Technology Stack

### Frontend
- **Framework**: React 19 (React Router v7)
- **Tooling & Bundler**: Vite 8.3
- **Styling**: Modern CSS3 custom properties (design system with dark/luxury aesthetic)
- **HTTP Client**: Native Fetch with dynamic base URL resolution, JWT token injection, and 60-second abort timeouts
- **Linting**: ESLint 10 with React hooks/refresh plugins

### Backend
- **Framework**: Spring Boot 3.4.2 (Java 21 LTS)
- **Security**: Spring Security 6.4 with stateless JWT (JSON Web Token) authentication
- **Persistence & ORM**: Spring Data JPA / Hibernate 6.6
- **Database**: PostgreSQL 17 (Hosted on Neon Cloud with SSL)
- **Testing**: JUnit 5, Mockito, Spring Boot Test, H2 In-Memory Database for isolated CI tests
- **Build Tool**: Apache Maven 3.9

---

## 3. Architecture Summary

```
                      +---------------------------------------+
                      |       React 19 Frontend (Vite)        |
                      | (Customer, Consultant, Admin Portals) |
                      +-------------------+-------------------+
                                          | REST / HTTPS (JWT)
                                          v
                      +---------------------------------------+
                      |       Spring Boot 3.4 REST API        |
                      |  - JwtAuthenticationFilter            |
                      |  - Role-Based Endpoint Interceptors   |
                      |  - Business Services & Domain Logic   |
                      +-------------------+-------------------+
                                          | JDBC + SSL (HikariCP)
                                          v
                      +---------------------------------------+
                      |       Neon PostgreSQL Database        |
                      | (Models, Variants, Leads, Bookings)   |
                      +---------------------------------------+
```

---

## 4. Local Setup Instructions

### Prerequisites
- Node.js 20+ and npm 10+
- Java Development Kit (JDK) 21 LTS
- Apache Maven 3.9+
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/Tejaswini5407/AutoVista.git
cd AutoVista
```

### 2. Configure Environment Variables
Ensure `.env.local` exists in the repository root or provide the environment variables:
```bash
DATABASE_URL=jdbc:postgresql://<neon-host>:5432/<dbname>?sslmode=require
DATABASE_USERNAME=<neon-user>
DATABASE_PASSWORD=<neon-password>
JWT_SECRET=AutoVistaSuperSecretKeyForJwtSigningMustBeAtLeast256BitsLong2026AutoDealershipKey
```

### 3. Start Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```
*The backend starts at `http://localhost:8080/api` with health status at `http://localhost:8080/api/health`.*

### 4. Start Frontend (React/Vite)
In a separate terminal:
```bash
cd frontend
npm install
npm run dev
```
*The frontend starts at `http://localhost:5173`.*

---

## 5. Demo Account Roles (Pre-Seeded)

The database comes pre-seeded with three verified roles. Demo buttons are available on the Sign In page for one-click authentication.

| Role | Email | User Persona | Portal Route |
|---|---|---|---|
| **Dealership Admin** | `admin@autovista.in` | Pooja Verma (Dealership Principal) | `/admin/dashboard` |
| **Sales Consultant** | `vikram.consultant@autovista.in` | Vikram Singh (Senior Luxury Specialist) | `/consultant/dashboard` |
| **Verified Customer** | `aarav.sharma@example.com` | Aarav Sharma (Verified Vehicle Buyer) | `/` |

*Default credential for all pre-seeded accounts: `Password@123`.*

---

## 6. Test & Validation Commands

### Backend Test Suite
Executes 53 unit, integration, and security test cases against an in-memory H2 database:
```bash
cd backend
mvn test
```
*Result: 53 tests run, 0 failures, 0 errors, 0 skipped.*

### Frontend Quality Checks
```bash
cd frontend
npm run lint    # ESLint validation (0 errors, 0 warnings)
npm run build   # Production Vite bundle compilation
```

---

## 7. Submission Screenshots Index

All 15 screenshots were genuinely captured from the running local application via automated browser session and saved to `submission/screenshots/`:

| File | View / Workflow Description | Size |
|---|---|---|
| `01-homepage.png` | Dealership hero section, brand values, featured Lucknow showroom collection | 570.6 KB |
| `02-new-cars-catalogue.png` | New car inventory with fuel type and max-price filtering | 411.8 KB |
| `03-used-cars-catalogue.png` | Certified pre-owned catalog, 150-point inspection tags, masked license plates | 453.9 KB |
| `04-new-car-details-pricing.png` | Detailed car specifications and live UP RTO on-road price calculation | 270.1 KB |
| `05-used-car-details.png` | Pre-owned vehicle condition, odometer log, warranty status | 420.3 KB |
| `06-customer-login.png` | Authentication portal with one-click role demo options | 80.3 KB |
| `07-customer-registration.png` | Customer registration form with statutory communication consent checkbox | 85.2 KB |
| `08-test-drive-booking.png` | VIP test-drive scheduling with live showroom slot selection | 235.3 KB |
| `09-enquiry-confirmation.png` | Digital vehicle enquiry and price quote submission modal | 149.4 KB |
| `10-consultant-dashboard.png` | Sales consultant workbench with assigned customer enquiry pipeline | 157.2 KB |
| `11-consultant-lead-management.png` | Consultant lead detail inspector with buyer summary and SLA countdown | 157.4 KB |
| `12-admin-dashboard.png` | Dealership executive console with KPI statistics and revenue summaries | 99.4 KB |
| `13-admin-inventory-management.png` | Admin catalog management for adding and toggling car model availability | 110.1 KB |
| `14-admin-pricing-management.png` | Regional on-road pricing engine configuration (RTO %, handling, TCS) | 117.0 KB |
| `15-admin-bookings-leads.png` | Dealership-wide test-drive bookings and consultant lead reassignment | 107.1 KB |

---

## 8. Known Limitations & Notes

1. **Remote Cloud Deployment Status**:
   - The backend API is hosted on Render free tier (`https://autovista-backend.onrender.com`). Render instances spin down after inactivity and take 45–55 seconds to boot on first request.
   - For submission review and evaluation, the **local running application** (`http://localhost:5173` frontend + `http://localhost:8080` backend) is prioritized and completely functional.
2. **PostgreSQL Database Isolation**:
   - Automated unit tests run on H2 in-memory databases (`application-test.yml`) so local test suites never mutate persistent records in Neon PostgreSQL.
