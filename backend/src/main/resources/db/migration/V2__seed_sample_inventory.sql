-- ==============================================================================
-- AutoVista Automotive Platform — Phase 2 Seed Data
-- Version: V2
-- Description: Mandated sample inventory, showrooms, pricing rules, and users
-- ==============================================================================

-- 1. SHOWROOM SEED (Flagship Lucknow Facility)
INSERT INTO showrooms (id, name, code, city, address, phone, email, operating_hours, is_active)
VALUES (
    1,
    'AutoVista Flagship Experience Centre',
    'LKO-GOMTI-01',
    'Lucknow',
    'Shaheed Path, Vibhuti Khand, Gomti Nagar, Lucknow, Uttar Pradesh – 226010',
    '+91 (0522) 490-8800',
    'concierge@autovista-lucknow.in',
    '09:30 AM - 08:00 PM IST',
    TRUE
) ON CONFLICT (code) DO NOTHING;

-- 2. SYNTHETIC USERS (Password: Password@123)
-- BCrypt hash for 'Password@123': $2a$10$MMQHwM5VyP9GrFhsTtFv7eYHf1ivbn2upC1FtQOTYyvuneQ94OpuS
INSERT INTO users (id, email, password_hash, full_name, phone, role, city, is_active)
VALUES
(
    1,
    'admin@autovista.in',
    '$2a$10$MMQHwM5VyP9GrFhsTtFv7eYHf1ivbn2upC1FtQOTYyvuneQ94OpuS',
    'Pooja Verma (Dealership Admin)',
    '+91 98765 00001',
    'ADMIN',
    'Lucknow',
    TRUE
),
(
    2,
    'vikram.consultant@autovista.in',
    '$2a$10$MMQHwM5VyP9GrFhsTtFv7eYHf1ivbn2upC1FtQOTYyvuneQ94OpuS',
    'Vikram Singh (Senior Sales Specialist)',
    '+91 98765 00002',
    'CONSULTANT',
    'Lucknow',
    TRUE
),
(
    3,
    'aarav.sharma@example.com',
    '$2a$10$MMQHwM5VyP9GrFhsTtFv7eYHf1ivbn2upC1FtQOTYyvuneQ94OpuS',
    'Aarav Sharma (Verified Buyer)',
    '+91 98765 11223',
    'CUSTOMER',
    'Lucknow',
    TRUE
) ON CONFLICT (email) DO NOTHING;

-- 3. CAR MODELS
INSERT INTO car_models (id, brand, model_name, body_type, year, description, hero_image_url, is_active)
VALUES
(
    1,
    'Model H',
    'Model H',
    'Premium Sedan',
    2026,
    'Flagship executive hybrid sedan offering world-class fuel efficiency, ADAS safety, and peerless ride comfort.',
    'https://images.unsplash.com/photo-1617788138017-80ad40651399?auto=format&fit=crop&w=1200&q=80',
    TRUE
),
(
    2,
    'Model X',
    'Model X',
    'Executive SUV',
    2026,
    'High-stance luxury sport utility vehicle with intelligent AWD and turbocharged capability.',
    'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=1200&q=80',
    TRUE
),
(
    3,
    'Model E',
    'Model E',
    'Electric Crossover',
    2026,
    'Next-generation long-range electric crossover with ultra-fast charging capability.',
    'https://images.unsplash.com/photo-1563720223185-11003d516935?auto=format&fit=crop&w=1200&q=80',
    TRUE
) ON CONFLICT (brand, model_name, year) DO NOTHING;

-- 4. CAR VARIANTS (Mandated sample: New Model H ZX Automatic Petrol 2026: ₹12,45,000 ex-showroom)
INSERT INTO car_variants (id, car_model_id, variant_name, fuel_type, transmission, engine_specs, power_specs, torque_specs, seating_capacity, ex_showroom_price, tagline, features, colors, is_available)
VALUES
(
    1,
    1,
    'ZX Automatic Petrol',
    'PETROL',
    'AUTOMATIC',
    '1.5L i-VTEC Smart Hybrid',
    '121 PS @ 6,600 rpm',
    '145 Nm @ 4,300 rpm',
    5,
    1245000.00,
    'Refined craftsmanship meets intelligent hybrid performance.',
    'Panoramic Sunroof,ADAS Level 2 Autonomous Safety,10.2-inch Touchscreen Infotainment,Wireless Apple CarPlay,Ventilated Seats,6 Airbags',
    'Radiant Red Metallic,Obsidian Black,Platinum White Pearl,Lunar Silver',
    TRUE
),
(
    2,
    2,
    'GT Luxury Turbo Petrol',
    'PETROL',
    'AUTOMATIC',
    '2.0L Turbocharged Dual VVT',
    '188 PS @ 5,500 rpm',
    '320 Nm @ 1,500-4,000 rpm',
    7,
    1980000.00,
    'Dominate every road with uncompromising power and presence.',
    'All-Wheel Drive,Bose 12-Speaker Audio,360-Degree Camera,Heated Seats',
    'Carbon Black,Nardo Grey,Imperial Blue',
    TRUE
),
(
    3,
    3,
    'Long Range EV Tech Pack',
    'ELECTRIC',
    'AUTOMATIC',
    '72 kWh Synchronous Motor',
    '215 hp',
    '350 Nm Instant',
    5,
    2450000.00,
    'Silent luxury and zero-emission dynamism for modern cities.',
    '520 km Range,Fast DC Charging,Regenerative Braking,Clean Air Purifier',
    'Emerald Frost,Monaco White,Cosmic Blue',
    TRUE
) ON CONFLICT (car_model_id, variant_name) DO NOTHING;

-- 5. USED CARS (Mandated sample inventory)
-- - Used Model C VX Diesel 2022: 41,200 km, ₹8,45,000
-- - Used Model S LX Petrol 2021: 58,600 km, ₹5,12,000, status: Booked
INSERT INTO used_cars (id, showroom_id, brand, model_name, variant_name, manufacturing_year, price, odometer_km, raw_registration_number, fuel_type, transmission, owners_count, insurance_validity, inspection_points_passed, status, hero_image_url, features, tagline)
VALUES
(
    1,
    1,
    'Model C',
    'Model C',
    'VX Diesel',
    2022,
    845000.00,
    41200,
    'UP32 BK 8921',
    'DIESEL',
    'MANUAL',
    1,
    'Aug 2027 (Comprehensive)',
    150,
    'AVAILABLE',
    'https://images.unsplash.com/photo-1550355291-bbee04a92027?auto=format&fit=crop&w=1200&q=80',
    'Complete Service History,No Accident Record,Cruise Control,Touchscreen Infotainment,Rear Camera',
    'Pristine single-owner diesel sedan with exceptional highway mileage.'
),
(
    2,
    1,
    'Model S',
    'Model S',
    'LX Petrol',
    2021,
    512000.00,
    58600,
    'UP32 DA 4410',
    'PETROL',
    'MANUAL',
    1,
    'Nov 2026 (Third Party)',
    150,
    'BOOKED', -- Mandated exact status: Booked
    'https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?auto=format&fit=crop&w=1200&q=80',
    'Verified Odometer Mileage,Original Manufacturer Paintwork,Dual Front Airbags,Automatic Climate Control',
    'Reliable, well-maintained city cruiser currently reserved under deposit.'
),
(
    3,
    1,
    'Model V',
    'Model V',
    'ZX Luxury Automatic',
    2023,
    1120000.00,
    22400,
    'UP32 EZ 1988',
    'PETROL',
    'AUTOMATIC',
    1,
    'Jan 2028 (Zero Depreciation)',
    150,
    'AVAILABLE',
    'https://images.unsplash.com/photo-1502877338535-766e1452684a?auto=format&fit=crop&w=1200&q=80',
    'Under Factory Warranty,360 Parking Assist,Leatherette Upholstery',
    'Nearly new luxury crossover with pristine cabin and low mileage.'
) ON CONFLICT (id) DO NOTHING;

-- 6. CITY-WISE PRICING CONFIGURATION (For Lucknow and UP cities)
INSERT INTO city_prices (id, car_variant_id, city, state, rto_tax_percent, rto_tax_amount, insurance_amount, accessories_amount, handling_charges, tcs_amount, total_on_road_price, price_valid_until, disclaimer)
VALUES
(
    1,
    1,
    'Lucknow',
    'Uttar Pradesh',
    10.00,
    124500.00, -- 10% of 12,45,000
    43575.00,  -- 3.5% of 12,45,000
    25000.00,  -- Standard accessories kit
    0.00,
    12450.00,  -- 1% TCS (> 10L threshold)
    1450525.00, -- Total on-road
    '2026-12-31',
    'Price Validity Disclaimer: Displayed prices reflect current Lucknow RTO norms and are subject to state revisions. Final on-road quote provided at showroom booking.'
),
(
    2,
    2,
    'Lucknow',
    'Uttar Pradesh',
    10.00,
    198000.00,
    69300.00,
    25000.00,
    0.00,
    19800.00,
    2292100.00,
    '2026-12-31',
    'Price Validity Disclaimer: Displayed prices reflect current Lucknow RTO norms and are subject to state revisions.'
) ON CONFLICT (car_variant_id, city) DO NOTHING;

-- Reset sequence IDs to prevent duplicate key errors on future inserts
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('showrooms_id_seq', (SELECT MAX(id) FROM showrooms));
SELECT setval('car_models_id_seq', (SELECT MAX(id) FROM car_models));
SELECT setval('car_variants_id_seq', (SELECT MAX(id) FROM car_variants));
SELECT setval('used_cars_id_seq', (SELECT MAX(id) FROM used_cars));
SELECT setval('city_prices_id_seq', (SELECT MAX(id) FROM city_prices));
