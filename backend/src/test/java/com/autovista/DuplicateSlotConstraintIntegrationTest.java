package com.autovista;

import com.autovista.entity.Showroom;
import com.autovista.entity.TestDriveBooking;
import com.autovista.repository.ShowroomRepository;
import com.autovista.repository.TestDriveBookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DuplicateSlotConstraintIntegrationTest {

    @Autowired
    private TestDriveBookingRepository bookingRepository;

    @Autowired
    private ShowroomRepository showroomRepository;

    @Test
    @DisplayName("Requirement #6: Database partial unique index prevents duplicate active booking for same showroom, date, and slot")
    void shouldEnforceDatabaseLevelDuplicateSlotConstraint() {
        Showroom showroom = new Showroom();
        showroom.setName("AutoVista Gomti Nagar Test");
        showroom.setCode("TEST-LKO-01");
        showroom.setCity("Lucknow");
        showroom.setAddress("Gomti Nagar");
        showroom = showroomRepository.saveAndFlush(showroom);

        LocalDate bookingDate = LocalDate.of(2026, 11, 10);
        String timeSlot = "11:00 AM - 12:00 PM";

        // First Booking: Confirmed and saved
        TestDriveBooking booking1 = new TestDriveBooking();
        booking1.setBookingRef("BK-TEST-001");
        booking1.setShowroom(showroom);
        booking1.setCustomerName("First Customer");
        booking1.setCustomerPhone("+91 98765 00001");
        booking1.setCustomerEmail("first@example.com");
        booking1.setBookingDate(bookingDate);
        booking1.setTimeSlot(timeSlot);
        booking1.setDriveMode("SHOWROOM");
        booking1.setStatus("CONFIRMED");
        booking1.setConsentGiven(true);
        bookingRepository.saveAndFlush(booking1);

        // Second Booking: Identical showroom, date, and slot -> Must trigger partial unique index violation!
        TestDriveBooking booking2 = new TestDriveBooking();
        booking2.setBookingRef("BK-TEST-002");
        booking2.setShowroom(showroom);
        booking2.setCustomerName("Conflicting Customer");
        booking2.setCustomerPhone("+91 98765 00002");
        booking2.setCustomerEmail("conflict@example.com");
        booking2.setBookingDate(bookingDate);
        booking2.setTimeSlot(timeSlot);
        booking2.setDriveMode("SHOWROOM");
        booking2.setStatus("CONFIRMED");
        booking2.setConsentGiven(true);

        assertThrows(DataIntegrityViolationException.class, () -> {
            bookingRepository.saveAndFlush(booking2);
        }, "Database partial unique index 'uq_test_drive_active_slot' must reject duplicate active bookings!");
    }

    @Test
    @DisplayName("Phase 5.1: Cancelled booking releases the slot and allows a new booking record to be inserted into the database")
    void shouldAllowNewBookingWhenPreviousBookingIsCancelled() {
        Showroom showroom = new Showroom();
        showroom.setName("AutoVista Gomti Nagar Release Test");
        showroom.setCode("TEST-LKO-02");
        showroom.setCity("Lucknow");
        showroom.setAddress("Gomti Nagar");
        showroom = showroomRepository.saveAndFlush(showroom);

        LocalDate bookingDate = LocalDate.of(2026, 11, 15);
        String timeSlot = "04:00 PM - 05:00 PM";

        // Booking 1: Created and then cancelled
        TestDriveBooking booking1 = new TestDriveBooking();
        booking1.setBookingRef("BK-TEST-CANCEL-001");
        booking1.setShowroom(showroom);
        booking1.setCustomerName("Customer A");
        booking1.setCustomerPhone("+91 98765 00003");
        booking1.setCustomerEmail("customera@example.com");
        booking1.setBookingDate(bookingDate);
        booking1.setTimeSlot(timeSlot);
        booking1.setDriveMode("SHOWROOM");
        booking1.setStatus("CANCELLED");
        booking1.setConsentGiven(true);
        booking1 = bookingRepository.saveAndFlush(booking1);

        // Booking 2: New customer books the same slot -> Must succeed and persist as a distinct row!
        TestDriveBooking booking2 = new TestDriveBooking();
        booking2.setBookingRef("BK-TEST-NEW-002");
        booking2.setShowroom(showroom);
        booking2.setCustomerName("Customer B");
        booking2.setCustomerPhone("+91 98765 00004");
        booking2.setCustomerEmail("customerb@example.com");
        booking2.setBookingDate(bookingDate);
        booking2.setTimeSlot(timeSlot);
        booking2.setDriveMode("SHOWROOM");
        booking2.setStatus("CONFIRMED");
        booking2.setConsentGiven(true);
        booking2 = bookingRepository.saveAndFlush(booking2);

        org.junit.jupiter.api.Assertions.assertNotEquals(booking1.getId(), booking2.getId(), "Old and new bookings must have distinct database IDs");
        org.junit.jupiter.api.Assertions.assertNotEquals(booking1.getBookingRef(), booking2.getBookingRef(), "Old and new bookings must have distinct references");
        org.junit.jupiter.api.Assertions.assertEquals(2, bookingRepository.findByShowroomIdAndBookingDate(showroom.getId(), bookingDate).size(),
                "Both cancelled and new bookings must remain in the database table");
    }

    @Test
    @DisplayName("Phase 5.1: COMPLETED booking continues occupying slot at database level")
    void shouldPreventBookingWhenPreviousBookingIsCompleted() {
        Showroom showroom = new Showroom();
        showroom.setName("AutoVista Gomti Nagar Completed Test");
        showroom.setCode("TEST-LKO-03");
        showroom.setCity("Lucknow");
        showroom.setAddress("Gomti Nagar");
        showroom = showroomRepository.saveAndFlush(showroom);

        LocalDate bookingDate = LocalDate.of(2026, 11, 16);
        String timeSlot = "10:00 AM - 11:00 AM";

        TestDriveBooking completedBooking = new TestDriveBooking();
        completedBooking.setBookingRef("BK-TEST-COMP-001");
        completedBooking.setShowroom(showroom);
        completedBooking.setCustomerName("Completed Customer");
        completedBooking.setCustomerPhone("+91 98765 00005");
        completedBooking.setCustomerEmail("completed@example.com");
        completedBooking.setBookingDate(bookingDate);
        completedBooking.setTimeSlot(timeSlot);
        completedBooking.setDriveMode("SHOWROOM");
        completedBooking.setStatus("COMPLETED");
        completedBooking.setConsentGiven(true);
        bookingRepository.saveAndFlush(completedBooking);

        TestDriveBooking newBooking = new TestDriveBooking();
        newBooking.setBookingRef("BK-TEST-NEW-COMP-002");
        newBooking.setShowroom(showroom);
        newBooking.setCustomerName("Second Customer");
        newBooking.setCustomerPhone("+91 98765 00006");
        newBooking.setCustomerEmail("second@example.com");
        newBooking.setBookingDate(bookingDate);
        newBooking.setTimeSlot(timeSlot);
        newBooking.setDriveMode("SHOWROOM");
        newBooking.setStatus("CONFIRMED");
        newBooking.setConsentGiven(true);

        assertThrows(DataIntegrityViolationException.class, () -> {
            bookingRepository.saveAndFlush(newBooking);
        }, "COMPLETED booking must continue occupying the slot in the database partial unique index!");
    }
}
