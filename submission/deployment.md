# AutoVista — Deployment & Operational Guide

**Target Environment:** Local Demonstration & Production Evaluation  
**Frontend Local Port:** `5173`  
**Backend Local Port:** `8080`  
**Cloud Backend Target (Render):** `https://autovista-backend.onrender.com`  
**Cloud Frontend Target (Vercel):** `https://auto-vista-chi.vercel.app`

---

## 1. Prerequisites

Before running the AutoVista application locally, verify the following dependencies are installed:
- **Node.js**: `v20.x` or later (Tested with Node.js v24.14.0)
- **npm**: `v10.x` or later
- **Java Development Kit (JDK)**: `21 LTS` (Tested with OpenJDK 21.0.9)
- **Apache Maven**: `v3.9.x`
- **Git**: `v2.x`

---

## 2. Environment Variables Configuration

Create a file named `.env.local` in the project root:
`C:\Users\tejac\OneDrive\Desktop\Cars\AutoVista\.env.local`

```properties
# ===================================================================
# Database Configuration (Neon PostgreSQL Cloud Instance)
# ===================================================================
DATABASE_URL=postgresql://<username>:<password>@<neon-host-url>/<database-name>?sslmode=require

# ===================================================================
# JWT Authentication Configuration
# Must be at least 256 bits (32 characters) for HMAC-SHA256
# ===================================================================
JWT_SECRET=AutoVistaSuperSecretKeyForJwtSigningMustBeAtLeast256BitsLong2026AutoDealershipKey
JWT_EXPIRATION_HOURS=24

# ===================================================================
# Frontend Configuration (Optional Override)
# If omitted, frontend defaults to http://localhost:8080/api on localhost
# and https://autovista-backend.onrender.com/api on production domains
# ===================================================================
VITE_API_BASE_URL=http://localhost:8080/api
```

> **Security Note:** Never commit `.env.local` or raw database credentials into version control. Ensure `.env.local` is listed in `.gitignore`.

---

## 3. Database Configuration

The application is pre-configured to connect to a Neon serverless PostgreSQL database with SSL encryption enabled (`sslmode=require`).
The schema and seed records (car models, variants, pre-owned cars, pricing rules, and default user personas) are initialized automatically through Spring Data JPA Hibernate DDL validations.

---

## 4. Local Execution Instructions

### Step 4.1: Start Spring Boot Backend
Open a terminal in the project directory:

```powershell
# Set environment variables from .env.local and launch
$lines = Get-Content -Path ".env.local"
foreach ($line in $lines) {
    if ($line -match '^([^#=]+)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim())
    }
}

cd backend
mvn spring-boot:run
```

**Verification:**
Test that the backend is responding and database is connected:
```bash
curl http://localhost:8080/api/health
```
Expected response:
```json
{
  "success": true,
  "message": "AutoVista system health status",
  "data": {
    "status": "UP",
    "applicationName": "autovista-backend",
    "environment": "development",
    "databaseConnected": true,
    "databaseProductName": "PostgreSQL"
  }
}
```

---

### Step 4.2: Start React Frontend
In a separate terminal window:

```bash
cd frontend
npm install
npm run dev
```

The terminal will report:
```text
  VITE v8.3.5  ready in 450 ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

Open `http://localhost:5173/` in your browser.

---

## 5. Build and Test Commands

### Execute Backend Test Suite
```bash
cd backend
mvn clean test
```
*Runs 53 automated unit, integration, and security tests using an isolated in-memory H2 database profile (`application-test.yml`).*

### Execute Frontend Lint & Build
```bash
cd frontend
npm run lint    # ESLint check across all JSX/JS files
npm run build   # Production Vite compilation to dist/
```

---

## 6. Deployment Status & Platform Clarification

### Local Demonstration (Confirmed & Ready for Evaluation)
- Both Spring Boot and Vite dev servers run cleanly on `localhost:8080` and `localhost:5173`.
- All authentication flows, customer browsing, consultant lead workflows, and administrative consoles are verified locally.

### Remote Cloud Services Status
- **Render (Backend):**
  - Hosted at `https://autovista-backend.onrender.com`.
  - Operates on Render free-tier compute. When inactive for >15 minutes, containers spin down to zero and require 45–55 seconds to spin up upon receiving an initial request.
- **Vercel (Frontend):**
  - Hosted at `https://auto-vista-chi.vercel.app`.
  - Configured with `frontend/vercel.json` SPA fallbacks.
  - **Evaluation Recommendation**: For immediate evaluation without cold-start delays or cloud latency, **please prioritize the local running demonstration** (`http://localhost:5173`).
