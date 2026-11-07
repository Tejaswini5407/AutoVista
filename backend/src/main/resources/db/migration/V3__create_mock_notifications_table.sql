-- ==============================================================================
-- AutoVista Automotive Platform — Phase 5 Mock Notifications Schema
-- Version: V3
-- Description: Stores simulated SMS, Email and WhatsApp notification events for
--              audit and local demonstrability without paid third-party dependencies.
-- ==============================================================================

CREATE TABLE mock_notifications (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT,
    booking_ref VARCHAR(50) NOT NULL,
    recipient_name VARCHAR(100),
    recipient_email VARCHAR(150),
    recipient_phone VARCHAR(50),
    notification_type VARCHAR(50) NOT NULL, -- 'BOOKING_CONFIRMED', 'STATUS_UPDATED', 'BOOKING_CANCELLED', 'BOOKING_REJECTED'
    channel VARCHAR(30) NOT NULL,          -- 'SMS_SIMULATED', 'EMAIL_SIMULATED', 'WHATSAPP_SIMULATED'
    subject VARCHAR(255),
    message_body TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DELIVERED_SIMULATED',
    sent_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_booking FOREIGN KEY (booking_id) REFERENCES test_drive_bookings(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_booking_id ON mock_notifications(booking_id);
CREATE INDEX idx_notifications_booking_ref ON mock_notifications(booking_ref);
CREATE INDEX idx_notifications_recipient_email ON mock_notifications(recipient_email);
