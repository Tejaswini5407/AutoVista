package com.autovista.repository;

import com.autovista.entity.TestDriveBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestDriveBookingRepository extends JpaRepository<TestDriveBooking, Long> {

    Optional<TestDriveBooking> findByBookingRef(String bookingRef);

    boolean existsByShowroomIdAndBookingDateAndTimeSlot(Long showroomId, LocalDate bookingDate, String timeSlot);

    Optional<TestDriveBooking> findByShowroomIdAndBookingDateAndTimeSlot(Long showroomId, LocalDate bookingDate, String timeSlot);

    List<TestDriveBooking> findByUserId(Long userId);

    List<TestDriveBooking> findByUserIdOrderByBookingDateDescCreatedAtDesc(Long userId);

    List<TestDriveBooking> findByShowroomIdAndBookingDate(Long showroomId, LocalDate bookingDate);

    List<TestDriveBooking> findAllByOrderByBookingDateDescCreatedAtDesc();

    List<TestDriveBooking> findByBookingDate(LocalDate bookingDate);

    List<TestDriveBooking> findByShowroomId(Long showroomId);

    List<TestDriveBooking> findByStatus(String status);
}
