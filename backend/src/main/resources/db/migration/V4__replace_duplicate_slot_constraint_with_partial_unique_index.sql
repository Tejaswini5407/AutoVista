-- AutoVista Phase 5.1: Replace unconditional duplicate slot constraint with partial unique index
-- This allows cancelled/rejected bookings to remain in history while releasing the slot for new bookings.

-- 1. Drop existing unconditional constraint on (showroom_id, booking_date, time_slot)
ALTER TABLE test_drive_bookings DROP CONSTRAINT IF EXISTS uq_test_drive_duplicate_slot;

-- 2. Drop index if it exists under the same or older name
DROP INDEX IF EXISTS uq_test_drive_active_slot;
DROP INDEX IF EXISTS uq_test_drive_duplicate_slot;

-- 3. Create partial unique index on active/consumed slots only
-- CONFIRMED and COMPLETED bookings occupy the slot.
-- CANCELLED and REJECTED bookings release the slot and will not block new bookings.
CREATE UNIQUE INDEX uq_test_drive_active_slot
ON test_drive_bookings (showroom_id, booking_date, time_slot)
WHERE status NOT IN ('CANCELLED', 'REJECTED');
