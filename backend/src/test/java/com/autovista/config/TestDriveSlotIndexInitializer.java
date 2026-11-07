package com.autovista.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Initializes slot uniqueness enforcement for H2 in-memory test profiles.
 * In production PostgreSQL, Flyway V4 manages the native partial unique index:
 * uq_test_drive_active_slot ON (showroom_id, booking_date, time_slot) WHERE status NOT IN ('CANCELLED', 'REJECTED').
 * In H2 test profiles with Hibernate ddl-auto: create-drop, this runner attaches a generated
 * active slot key column with a unique index to emulate identical database-level concurrency locking.
 */
@Component
public class TestDriveSlotIndexInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TestDriveSlotIndexInitializer.class);
    private final JdbcTemplate jdbcTemplate;

    public TestDriveSlotIndexInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            String dbProduct = jdbcTemplate.execute((ConnectionCallback<String>) conn -> conn.getMetaData().getDatabaseProductName());
            if (dbProduct != null && dbProduct.toLowerCase().contains("h2")) {
                jdbcTemplate.execute(
                        "ALTER TABLE test_drive_bookings ADD COLUMN IF NOT EXISTS active_slot_key VARCHAR(100) " +
                        "GENERATED ALWAYS AS (CASE WHEN status NOT IN ('CANCELLED', 'REJECTED') " +
                        "THEN CAST(showroom_id AS VARCHAR) || '_' || CAST(booking_date AS VARCHAR) || '_' || time_slot " +
                        "ELSE NULL END)"
                );
                jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uq_active_slot_key ON test_drive_bookings (active_slot_key)");
                log.info("Initialized H2 database-level unique index on generated active_slot_key");
            }
        } catch (Exception ex) {
            log.warn("Notice on test-drive index setup: {}", ex.getMessage());
        }
    }
}
