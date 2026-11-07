# AutoVista — Database Schema & Entity Relationship (ER) Model

## 1. Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    users ||--o{ test_drive_bookings : "makes"
    users ||--o{ lead_assignments : "handled_by"
    showrooms ||--o{ used_cars : "stocks"
    showrooms ||--o{ test_drive_bookings : "hosts"
    car_models ||--|{ car_variants : "has_variants"
    car_variants ||--o{ city_prices : "configured_in"
    car_variants ||--o{ test_drive_bookings : "booked_for"
    car_variants ||--o{ leads : "inquired_about"
    used_cars ||--o{ test_drive_bookings : "booked_for"
    used_cars ||--o{ leads : "inquired_about"
    leads ||--o{ lead_assignments : "assigned_to"

    users {
        BIGSERIAL id PK
        VARCHAR email UK "NOT NULL"
        VARCHAR password_hash "NOT NULL"
        VARCHAR full_name "NOT NULL"
        VARCHAR phone
        VARCHAR role "NOT NULL (CUSTOMER, CONSULTANT, ADMIN)"
        VARCHAR city "DEFAULT 'Lucknow'"
        BOOLEAN is_active "DEFAULT TRUE"
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    showrooms {
        BIGSERIAL id PK
        VARCHAR name "NOT NULL"
        VARCHAR code UK "NOT NULL (e.g. LKO-GOMTI-01)"
        VARCHAR city "NOT NULL"
        TEXT address "NOT NULL"
        VARCHAR phone
        VARCHAR email
        VARCHAR operating_hours
        BOOLEAN is_active "DEFAULT TRUE"
    }

    car_models {
        BIGSERIAL id PK
        VARCHAR brand "NOT NULL"
        VARCHAR model_name "NOT NULL"
        VARCHAR body_type "NOT NULL"
        INT year "NOT NULL"
        TEXT description
        TEXT hero_image_url
        BOOLEAN is_active "DEFAULT TRUE"
    }

    car_variants {
        BIGSERIAL id PK
        BIGINT car_model_id FK
        VARCHAR variant_name "NOT NULL"
        VARCHAR fuel_type "NOT NULL"
        VARCHAR transmission "NOT NULL"
        VARCHAR engine_specs
        VARCHAR power_specs
        VARCHAR torque_specs
        INT seating_capacity "DEFAULT 5"
        NUMERIC ex_showroom_price "NOT NULL (12,2)"
        VARCHAR tagline
        TEXT features
        TEXT colors
        BOOLEAN is_available "DEFAULT TRUE"
    }

    used_cars {
        BIGSERIAL id PK
        BIGINT showroom_id FK
        VARCHAR brand "NOT NULL"
        VARCHAR model_name "NOT NULL"
        VARCHAR variant_name "NOT NULL"
        INT manufacturing_year "NOT NULL"
        NUMERIC price "NOT NULL (12,2)"
        INT odometer_km "NOT NULL"
        VARCHAR raw_registration_number "NOT NULL (SENSITIVE)"
        VARCHAR fuel_type "NOT NULL"
        VARCHAR transmission "NOT NULL"
        INT owners_count "DEFAULT 1"
        VARCHAR insurance_validity
        INT inspection_points_passed "DEFAULT 150"
        VARCHAR status "NOT NULL (AVAILABLE, BOOKED, SOLD)"
        TEXT hero_image_url
    }

    city_prices {
        BIGSERIAL id PK
        BIGINT car_variant_id FK
        VARCHAR city "NOT NULL"
        VARCHAR state "DEFAULT 'Uttar Pradesh'"
        NUMERIC rto_tax_percent "NOT NULL"
        NUMERIC rto_tax_amount "NOT NULL"
        NUMERIC insurance_amount "NOT NULL"
        NUMERIC accessories_amount "DEFAULT 25000.00"
        NUMERIC handling_charges "DEFAULT 0.00"
        NUMERIC tcs_amount "DEFAULT 0.00"
        NUMERIC total_on_road_price "NOT NULL"
        DATE price_valid_until "NOT NULL"
        TEXT disclaimer "NOT NULL"
    }

    test_drive_bookings {
        BIGSERIAL id PK
        VARCHAR booking_ref UK "NOT NULL"
        BIGINT showroom_id FK
        BIGINT car_variant_id FK
        BIGINT used_car_id FK
        BIGINT user_id FK
        VARCHAR customer_name "NOT NULL"
        VARCHAR customer_phone "NOT NULL"
        VARCHAR customer_email "NOT NULL"
        DATE booking_date "NOT NULL"
        VARCHAR time_slot "NOT NULL"
        VARCHAR drive_mode "DEFAULT 'SHOWROOM'"
        TEXT doorstep_address
        BOOLEAN consent_given "DEFAULT TRUE"
        VARCHAR status "DEFAULT 'CONFIRMED'"
    }

    leads {
        BIGSERIAL id PK
        VARCHAR lead_ref UK "NOT NULL"
        VARCHAR customer_name "NOT NULL"
        VARCHAR customer_phone "NOT NULL"
        VARCHAR customer_email "NOT NULL"
        VARCHAR customer_city "DEFAULT 'Lucknow'"
        BIGINT interested_car_variant_id FK
        BIGINT interested_used_car_id FK
        VARCHAR enquiry_type "DEFAULT 'TEST_DRIVE'"
        BOOLEAN consent_given "DEFAULT TRUE"
        BOOLEAN opt_out "DEFAULT FALSE"
        VARCHAR status "DEFAULT 'NEW'"
        TEXT ai_summary
        TEXT ai_draft_reply
        BOOLEAN consultant_approved "DEFAULT FALSE"
        VARCHAR sent_via
    }

    lead_assignments {
        BIGSERIAL id PK
        BIGINT lead_id FK
        BIGINT consultant_id FK
        TIMESTAMPTZ assigned_at "DEFAULT NOW"
        TIMESTAMPTZ response_due_by "NOT NULL (Target <= 5 min)"
        TIMESTAMPTZ responded_at
        BOOLEAN sla_met
        VARCHAR status "DEFAULT 'PENDING'"
        TEXT notes
    }
```

---

## 2. Table Catalog & Constraint Specifications

### 1. `users`
- Stores credentials, identities, contact numbers, and system roles.
- **Constraints**: `uq_users_email` UNIQUE (`email`).
- **Indexes**: `idx_users_email`, `idx_users_role`.

### 2. `showrooms`
- Physical dealership facilities in Lucknow and regional branches.
- **Constraints**: `uq_showrooms_code` UNIQUE (`code`).
- **Indexes**: `idx_showrooms_city`.

### 3. `car_models`
- Vehicle brand and model lines (e.g. Model H, Model X, Model E).
- **Constraints**: `uq_car_models_brand_model_year` UNIQUE (`brand`, `model_name`, `year`).
- **Indexes**: `idx_car_models_brand`.

### 4. `car_variants`
- Specific trim levels, powertrains, and ex-showroom price points.
- **Constraints**: `fk_car_variants_model` FOREIGN KEY (`car_model_id`) REFERENCES `car_models(id)` ON DELETE CASCADE; `uq_variant_model_name` UNIQUE (`car_model_id`, `variant_name`).
- **Indexes**: `idx_car_variants_model`, `idx_car_variants_fuel`.

### 5. `used_cars`
- Pre-owned vehicle inventory with verified mileage and inspection metrics.
- **Sensitive Field Privacy Rule**: `raw_registration_number` is stored in the database for dealership titling, but is **strictly masked** (`maskRegistrationNumber()`) before leaving the backend API.
- **Indexes**: `idx_used_cars_status`, `idx_used_cars_showroom`.

### 6. `city_prices`
- Administrative pricing matrices configuring RTO taxes, comprehensive insurance, standard accessories, and TCS.
- **Constraints**: `uq_city_prices_variant_city` UNIQUE (`car_variant_id`, `city`).
- **Indexes**: `idx_city_prices_city`.

### 7. `test_drive_bookings`
- Appointment booking logs for showroom visits or doorstep vehicle trials.
- **Mandatory Business Rule #6 Database Constraint**:
  ```sql
  CONSTRAINT uq_test_drive_duplicate_slot UNIQUE (showroom_id, booking_date, time_slot);
  ```
  Guarantees at the relational database level that double-bookings for the same showroom, date, and time slot are impossible, preventing race conditions.

### 8. `leads`
- Customer enquiries originating from test drives, on-road quotes, and general requests.
- **Consent Tracking**: Tracks explicit customer consent (`consent_given = TRUE`) and immediate opt-out capability (`opt_out = TRUE`).
- **AI Integration Foundation**: Contains `ai_summary` and `ai_draft_reply` columns with a mandatory consultant review flag (`consultant_approved = FALSE` by default).

### 9. `lead_assignments`
- Automated routing of customer leads to sales specialists.
- **Mandatory 5-Minute SLA**: Enforces `response_due_by = assigned_at + INTERVAL '5 minutes'` to track and guarantee dealership response standards.
