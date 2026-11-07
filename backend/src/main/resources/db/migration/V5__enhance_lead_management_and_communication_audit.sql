-- ==============================================================================
-- AutoVista Automotive Platform — Phase 6 Migration
-- Version: V5
-- Description: Enhance lead management, assignment tracking, status history,
--              communication audit, and consultant seed.
-- ==============================================================================

-- 1. ENHANCE LEADS TABLE
ALTER TABLE leads
    ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES users(id),
    ADD COLUMN IF NOT EXISTS showroom_id BIGINT REFERENCES showrooms(id),
    ADD COLUMN IF NOT EXISTS enquiry_message TEXT,
    ADD COLUMN IF NOT EXISTS stated_budget VARCHAR(100),
    ADD COLUMN IF NOT EXISTS purchase_timeline VARCHAR(50),
    ADD COLUMN IF NOT EXISTS preferred_fuel VARCHAR(50),
    ADD COLUMN IF NOT EXISTS preferred_transmission VARCHAR(50),
    ADD COLUMN IF NOT EXISTS last_assigned_consultant_id BIGINT REFERENCES users(id),
    ADD COLUMN IF NOT EXISTS assigned_at TIMESTAMP WITH TIME ZONE;

-- Add index on leads user and showroom
CREATE INDEX IF NOT EXISTS idx_leads_user_id ON leads(user_id);
CREATE INDEX IF NOT EXISTS idx_leads_showroom_id ON leads(showroom_id);
CREATE INDEX IF NOT EXISTS idx_leads_assigned_consultant ON leads(last_assigned_consultant_id);

-- 2. CREATE LEAD STATUS HISTORY TABLE
CREATE TABLE IF NOT EXISTS lead_status_history (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL REFERENCES leads(id) ON DELETE CASCADE,
    changed_by_user_id BIGINT REFERENCES users(id),
    from_status VARCHAR(40),
    to_status VARCHAR(40) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_lead_status_history_lead ON lead_status_history(lead_id);
CREATE INDEX IF NOT EXISTS idx_lead_status_history_created ON lead_status_history(created_at);

-- 3. CREATE LEAD COMMUNICATIONS AUDIT TABLE
CREATE TABLE IF NOT EXISTS lead_communications (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL REFERENCES leads(id) ON DELETE CASCADE,
    consultant_id BIGINT NOT NULL REFERENCES users(id),
    draft_content TEXT NOT NULL,
    approved_content TEXT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    delivery_status VARCHAR(50) NOT NULL DEFAULT 'DELIVERED_SIMULATED',
    is_approved BOOLEAN NOT NULL DEFAULT TRUE,
    approved_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_lead_comms_lead ON lead_communications(lead_id);
CREATE INDEX IF NOT EXISTS idx_lead_comms_consultant ON lead_communications(consultant_id);
CREATE INDEX IF NOT EXISTS idx_lead_comms_sent ON lead_communications(sent_at);

-- 4. SEED SECOND ACTIVE SALES CONSULTANT FOR ROUND-ROBIN TESTING
-- BCrypt hash for 'Password@123': $2a$10$MMQHwM5VyP9GrFhsTtFv7eYHf1ivbn2upC1FtQOTYyvuneQ94OpuS
INSERT INTO users (email, password_hash, full_name, phone, role, city, is_active)
VALUES (
    'neha.consultant@autovista.in',
    '$2a$10$MMQHwM5VyP9GrFhsTtFv7eYHf1ivbn2upC1FtQOTYyvuneQ94OpuS',
    'Neha Rastogi (Senior Luxury Consultant)',
    '+91 98765 00003',
    'CONSULTANT',
    'Lucknow',
    TRUE
) ON CONFLICT (email) DO NOTHING;
