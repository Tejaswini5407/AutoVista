# AutoVista — Setup and Run Guide

This guide provides step-by-step instructions to configure, build, test, and run the **AutoVista** platform locally on Windows, macOS, or Linux.

---

## 1. Prerequisites & Environment

AutoVista is built using modern, stable, open-source technologies:

| Component | Required Version | Local Verification Command |
|---|---|---|
| **Java Development Kit** | Java 21 (Temurin / Oracle / OpenJDK) | `java -version` |
| **Apache Maven** | 3.9+ | `mvn -version` |
| **Node.js** | 20+ (24.x tested) | `node -v` |
| **npm** | 10+ (11.x tested) | `npm -v` |
| **PostgreSQL** | 17.x | `psql -V` |
| **Git** | 2.x | `git --version` |

---

## 2. Database Setup (PostgreSQL 17)

### Step 2.1: Start PostgreSQL Service
Ensure your local PostgreSQL server is running.
On Windows:
```powershell
Get-Service -Name postgresql*
# If stopped:
Start-Service postgresql-x64-17
```

### Step 2.2: Create the `autovista_db` Database
Connect to PostgreSQL using `psql`:
```bash
psql -U postgres
```
Execute the SQL command to create the application database:
```sql
CREATE DATABASE autovista_db;
\q
```

---

## 3. Environment Configuration

AutoVista uses zero-leakage configuration principles. Never commit secrets to version control.

### Step 3.1: Configure Backend Environment Variables
A template is provided at [`.env.example`](file:///c:/Users/tejac/OneDrive/Desktop/Cars/AutoVista/.env.example) and [`backend/.env.example`](file:///c:/Users/tejac/OneDrive/Desktop/Cars/AutoVista/backend/.env.example).

You can supply these variables as system environment variables, in your IDE run configuration, or create a local `.env` file (which is git-ignored):

```env
# Database Credentials
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/autovista_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_local_postgres_password

# JWT Authentication Secret (Minimum 256 bits / 32 characters)
JWT_SECRET=AutoVistaSuperSecretKeyForJwtSigningMustBeAtLeast256BitsLong2026AutoDealershipKey
JWT_EXPIRATION_MS=86400000

# Server Port
PORT=8080

# Active Spring Profile
SPRING_PROFILES_ACTIVE=dev
```

> [!NOTE]
> Spring Boot is configured in `application.yml` with sensible local development defaults (`postgres` / `postgres`). If your local password matches, no additional environment variables are required.

---

## 4. Backend Build, Test & Run

All backend commands are run from the `backend/` directory.

### Step 4.1: Run Backend Tests
The test suite includes Spring context verification, DTO mapper validations (including registration number masking), controller mock tests, and database constraint tests:
```powershell
cd backend
mvn clean test
```
**Expected Result**: All tests pass (`BUILD SUCCESS`, 0 failures, 0 errors).

### Step 4.2: Run Spring Boot Application
```powershell
cd backend
mvn spring-boot:run
```
Upon startup:
1. **Flyway** automatically runs `V1__create_initial_schema.sql` (creating 9 relational tables, indexes, and unique constraints).
2. **Flyway** executes `V2__seed_sample_inventory.sql` (inserting the flagship showroom, Model H ZX, Model C VX, Model S LX, city pricing rules, and default accounts).
3. The server starts listening on **`http://localhost:8080`**.

### Step 4.3: Verify Backend Health
In a separate terminal or browser:
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/health -Method GET | ConvertTo-Json
```
**Expected Response**:
```json
{
  "status": "UP",
  "message": "AutoVista backend is healthy and connected to PostgreSQL",
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

## 5. Frontend Build & Run

All frontend commands are run from the `frontend/` directory.

### Step 5.1: Install Dependencies
```powershell
cd frontend
npm install
```

### Step 5.2: Start Vite Development Server
```powershell
cd frontend
npm run dev
```
Open **`http://localhost:5173`** in your browser.

### Step 5.3: Verify Production Build & Lints
```powershell
cd frontend
npm run build
npm run lint
```
**Expected Result**: Build succeeds cleanly (Vite bundle generated in `dist/` in ~600ms, 0 lint errors).

---

## 6. Seed Accounts & Synthetic Data

The platform comes pre-seeded with synthetic test accounts (password for all seeded accounts is `Password@123`):

| Role | Email | Password | Purpose |
|---|---|---|---|
| **Admin** | `admin@autovista.in` | `Password@123` | Inventory, variants, city pricing management |
| **Sales Consultant** | `vikram.consultant@autovista.in` | `Password@123` | Lead management, AI summary & reply approval |
| **Customer** | `aarav.sharma@example.com` | `Password@123` | Test-drive bookings, quotation calculator |

### Mandatory Sample Inventory Verification
Verify the client-mandated sample inventory via the API:
```powershell
# 1. New Model H ZX 2026 (Ex-showroom ₹12,45,000)
Invoke-RestMethod -Uri "http://localhost:8080/api/cars"

# 2. Used Model C VX (₹8,45,000) & Model S LX (₹5,12,000, Booked) with masked plates
Invoke-RestMethod -Uri "http://localhost:8080/api/used-cars"

# 3. Lucknow On-Road Pricing for Model H ZX (Total: ₹14,50,525)
Invoke-RestMethod -Uri "http://localhost:8080/api/cars/variants/1/pricing?city=Lucknow"
```

---

## 7. Troubleshooting

- **PostgreSQL Connection Refused**:
  - Check if PostgreSQL service is running: `Get-Service postgresql*`
  - Verify port 5432 is listening: `netstat -ano | findstr :5432`
- **Duplicate Slot Constraint**:
  - Test-drive booking uniqueness is guaranteed at the database level by `uq_test_drive_duplicate_slot`. Any attempt to double-book the same showroom, date, and time slot will be rejected with HTTP 409 Conflict.
- **Registration Number Privacy**:
  - Raw vehicle registration numbers are masked in all public API responses (`UP32 •• ••••`) by `CarMapper`.
