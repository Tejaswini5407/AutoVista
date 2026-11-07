-- ==============================================================================
-- AutoVista Automotive Platform — Phase 2 Database Schema
-- Version: V1
-- Description: Core 9 tables, relational foreign keys, constraints, and indexes
-- ==============================================================================

-- 1. USERS TABLE
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(30) NOT NULL, -- 'CUSTOMER', 'CONSULTANT', 'ADMIN'
    city VARCHAR(50) DEFAULT 'Lucknow',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- 2. SHOWROOMS TABLE
CREATE TABLE showrooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(150),
    operating_hours VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_showrooms_code UNIQUE (code)
);

CREATE INDEX idx_showrooms_city ON showrooms(city);

-- 3. CAR MODELS TABLE
CREATE TABLE car_models (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    body_type VARCHAR(50) NOT NULL, -- e.g. 'Premium Sedan', 'Executive SUV'
    year INT NOT NULL,
    description TEXT,
    hero_image_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_car_models_brand_model_year UNIQUE (brand, model_name, year)
);

CREATE INDEX idx_car_models_brand ON car_models(brand);

-- 4. CAR VARIANTS TABLE
CREATE TABLE car_variants (
    id BIGSERIAL PRIMARY KEY,
    car_model_id BIGINT NOT NULL,
    variant_name VARCHAR(100) NOT NULL,
    fuel_type VARCHAR(30) NOT NULL, -- 'PETROL', 'DIESEL', 'ELECTRIC', 'HYBRID'
    transmission VARCHAR(30) NOT NULL, -- 'AUTOMATIC', 'MANUAL'
    engine_specs VARCHAR(150),
    power_specs VARCHAR(100),
    torque_specs VARCHAR(100),
    seating_capacity INT NOT NULL DEFAULT 5,
    ex_showroom_price NUMERIC(12, 2) NOT NULL,
    tagline VARCHAR(255),
    features TEXT,
    colors TEXT,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_car_variants_model FOREIGN KEY (car_model_id) REFERENCES car_models(id) ON DELETE CASCADE,
    CONSTRAINT uq_variant_model_name UNIQUE (car_model_id, variant_name)
);

CREATE INDEX idx_car_variants_model ON car_variants(car_model_id);
CREATE INDEX idx_car_variants_fuel ON car_variants(fuel_type);

-- 5. USED CARS TABLE
CREATE TABLE used_cars (
    id BIGSERIAL PRIMARY KEY,
    showroom_id BIGINT NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    variant_name VARCHAR(100) NOT NULL,
    manufacturing_year INT NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    odometer_km INT NOT NULL,
    raw_registration_number VARCHAR(50) NOT NULL, -- Sensitive: masked in API responses
    fuel_type VARCHAR(30) NOT NULL,
    transmission VARCHAR(30) NOT NULL,
    owners_count INT NOT NULL DEFAULT 1,
    insurance_validity VARCHAR(100),
    inspection_points_passed INT NOT NULL DEFAULT 150,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE', -- 'AVAILABLE', 'BOOKED', 'SOLD'
    hero_image_url TEXT,
    features TEXT,
    tagline VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_used_cars_showroom FOREIGN KEY (showroom_id) REFERENCES showrooms(id)
);

CREATE INDEX idx_used_cars_status ON used_cars(status);
CREATE INDEX idx_used_cars_showroom ON used_cars(showroom_id);

-- 6. CITY-WISE PRICING CONFIGURATION TABLE
CREATE TABLE city_prices (
    id BIGSERIAL PRIMARY KEY,
    car_variant_id BIGINT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL DEFAULT 'Uttar Pradesh',
    rto_tax_percent NUMERIC(5, 2) NOT NULL,
    rto_tax_amount NUMERIC(12, 2) NOT NULL,
    insurance_amount NUMERIC(12, 2) NOT NULL,
    accessories_amount NUMERIC(12, 2) NOT NULL DEFAULT 25000.00,
    handling_charges NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    tcs_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    total_on_road_price NUMERIC(12, 2) NOT NULL,
    price_valid_until DATE NOT NULL,
    disclaimer TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_city_prices_variant FOREIGN KEY (car_variant_id) REFERENCES car_variants(id) ON DELETE CASCADE,
    CONSTRAINT uq_city_prices_variant_city UNIQUE (car_variant_id, city)
);

CREATE INDEX idx_city_prices_city ON city_prices(city);

-- 7. TEST DRIVE BOOKINGS TABLE (WITH DATABASE-LEVEL DUPLICATE SLOT CONSTRAINT)
CREATE TABLE test_drive_bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_ref VARCHAR(50) NOT NULL,
    showroom_id BIGINT NOT NULL,
    car_variant_id BIGINT,
    used_car_id BIGINT,
    user_id BIGINT,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(150) NOT NULL,
    booking_date DATE NOT NULL,
    time_slot VARCHAR(50) NOT NULL, -- e.g. '11:00 AM - 12:00 PM'
    drive_mode VARCHAR(30) NOT NULL DEFAULT 'SHOWROOM', -- 'SHOWROOM', 'DOORSTEP'
    doorstep_address TEXT,
    consent_given BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED', -- 'PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_showroom FOREIGN KEY (showroom_id) REFERENCES showrooms(id),
    CONSTRAINT fk_bookings_variant FOREIGN KEY (car_variant_id) REFERENCES car_variants(id),
    CONSTRAINT fk_bookings_used_car FOREIGN KEY (used_car_id) REFERENCES used_cars(id),
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_bookings_ref UNIQUE (booking_ref),
    -- MANDATORY BUSINESS REQUIREMENT #6:
    -- Prevent duplicate test-drive bookings for the same showroom, date, and time slot
    CONSTRAINT uq_test_drive_duplicate_slot UNIQUE (showroom_id, booking_date, time_slot)
);

CREATE INDEX idx_bookings_date ON test_drive_bookings(booking_date);
CREATE INDEX idx_bookings_user ON test_drive_bookings(user_id);

-- 8. LEADS TABLE (CUSTOMER ENQUIRIES, CONSENT & AI WORKFLOW DRAFTS)
CREATE TABLE leads (
    id BIGSERIAL PRIMARY KEY,
    lead_ref VARCHAR(50) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(150) NOT NULL,
    customer_city VARCHAR(50) DEFAULT 'Lucknow',
    interested_car_variant_id BIGINT,
    interested_used_car_id BIGINT,
    enquiry_type VARCHAR(50) NOT NULL DEFAULT 'TEST_DRIVE', -- 'TEST_DRIVE', 'PRICE_QUOTE', 'GENERAL'
    consent_given BOOLEAN NOT NULL DEFAULT TRUE,
    opt_out BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(40) NOT NULL DEFAULT 'NEW', -- 'NEW', 'ASSIGNED', 'IN_DISCUSSION', 'DRAFT_APPROVED', 'COMPLETED', 'CLOSED'
    ai_summary TEXT,
    ai_draft_reply TEXT,
    consultant_approved BOOLEAN NOT NULL DEFAULT FALSE,
    sent_via VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_leads_variant FOREIGN KEY (interested_car_variant_id) REFERENCES car_variants(id),
    CONSTRAINT fk_leads_used_car FOREIGN KEY (interested_used_car_id) REFERENCES used_cars(id),
    CONSTRAINT uq_leads_ref UNIQUE (lead_ref)
);

CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_created ON leads(created_at);

-- 9. LEAD ASSIGNMENTS TABLE (AUTOMATED DISPATCH & 5-MINUTE SLA TRACKING)
CREATE TABLE lead_assignments (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL,
    consultant_id BIGINT NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    response_due_by TIMESTAMPTZ NOT NULL, -- Target <= 5 minutes from assigned_at
    responded_at TIMESTAMPTZ,
    sla_met BOOLEAN,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'CONTACTED', 'RESOLVED'
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignments_lead FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignments_consultant FOREIGN KEY (consultant_id) REFERENCES users(id)
);

CREATE INDEX idx_assignments_consultant ON lead_assignments(consultant_id);
CREATE INDEX idx_assignments_status ON lead_assignments(status);
