package com.autovista.service;

import com.autovista.dto.request.CreateBookingRequest;
import com.autovista.dto.response.BookingResponse;
import com.autovista.dto.response.MockNotificationResponse;
import com.autovista.dto.response.SlotAvailabilityResponse;
import com.autovista.entity.CarVariant;
import com.autovista.entity.Showroom;
import com.autovista.entity.TestDriveBooking;
import com.autovista.entity.UsedCar;
import com.autovista.entity.UsedCarStatus;
import com.autovista.entity.User;
import com.autovista.exception.DuplicateBookingException;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.CarVariantRepository;
import com.autovista.repository.ShowroomRepository;
import com.autovista.repository.TestDriveBookingRepository;
import com.autovista.repository.UsedCarRepository;
import com.autovista.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestDriveBookingService {

    private static final Logger log = LoggerFactory.getLogger(TestDriveBookingService.class);

    public static final List<String> OPERATIONAL_TIME_SLOTS = List.of(
            "10:00 AM - 11:00 AM",
            "11:00 AM - 12:00 PM",
            "02:00 PM - 03:00 PM",
            "03:00 PM - 04:00 PM",
            "04:00 PM - 05:00 PM",
            "05:00 PM - 06:00 PM"
    );

    private final TestDriveBookingRepository bookingRepository;
    private final ShowroomRepository showroomRepository;
    private final CarVariantRepository carVariantRepository;
    private final UsedCarRepository usedCarRepository;
    private final UserRepository userRepository;
    private final MockNotificationService mockNotificationService;
    private final CarMapper carMapper;

    public TestDriveBookingService(
            TestDriveBookingRepository bookingRepository,
            ShowroomRepository showroomRepository,
            CarVariantRepository carVariantRepository,
            UsedCarRepository usedCarRepository,
            UserRepository userRepository,
            MockNotificationService mockNotificationService,
            CarMapper carMapper) {
        this.bookingRepository = bookingRepository;
        this.showroomRepository = showroomRepository;
        this.carVariantRepository = carVariantRepository;
        this.usedCarRepository = usedCarRepository;
        this.userRepository = userRepository;
        this.mockNotificationService = mockNotificationService;
        this.carMapper = carMapper;
    }

    // ==========================================
    // 1. AVAILABLE SLOT MANAGEMENT
    // ==========================================
    @Transactional(readOnly = true)
    public SlotAvailabilityResponse getSlotAvailability(Long showroomId, LocalDate date) {
        Showroom showroom = showroomRepository.findById(showroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Showroom", "id", showroomId));

        List<TestDriveBooking> dayBookings = bookingRepository.findByShowroomIdAndBookingDate(showroomId, date);

        List<String> bookedSlots = dayBookings.stream()
                .filter(b -> !"CANCELLED".equalsIgnoreCase(b.getStatus()) && !"REJECTED".equalsIgnoreCase(b.getStatus()))
                .map(TestDriveBooking::getTimeSlot)
                .collect(Collectors.toList());

        List<String> availableSlots = OPERATIONAL_TIME_SLOTS.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());

        return new SlotAvailabilityResponse(
                showroom.getId(),
                showroom.getName(),
                date,
                OPERATIONAL_TIME_SLOTS,
                bookedSlots,
                availableSlots
        );
    }

    // ==========================================
    // 2. CUSTOMER BOOKING CREATION & CONCURRENCY
    // ==========================================
    public BookingResponse createBooking(CreateBookingRequest request, String userEmail) {
        User user = null;
        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail)
                    .orElse(null);
        }

        // 1. Showroom validation
        Showroom showroom = showroomRepository.findById(request.getShowroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Showroom", "id", request.getShowroomId()));

        // 2. Vehicle validation
        CarVariant carVariant = null;
        UsedCar usedCar = null;
        String vehicleDisplayName;

        boolean hasVariant = request.getCarVariantId() != null;
        boolean hasUsedCar = request.getUsedCarId() != null;

        if (hasVariant && hasUsedCar) {
            throw new IllegalArgumentException("Cannot book both a new car variant and pre-owned car in a single booking request.");
        }
        if (!hasVariant && !hasUsedCar) {
            throw new IllegalArgumentException("Please select either a new car variant or a certified pre-owned car.");
        }

        if (hasVariant) {
            carVariant = carVariantRepository.findById(request.getCarVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("CarVariant", "id", request.getCarVariantId()));
            if (!Boolean.TRUE.equals(carVariant.getIsAvailable())) {
                throw new IllegalStateException("Selected vehicle variant is currently unavailable for test drives.");
            }
            vehicleDisplayName = String.format("%s %s %s (%d New)",
                    carVariant.getCarModel() != null ? carVariant.getCarModel().getBrand() : "AutoVista",
                    carVariant.getCarModel() != null ? carVariant.getCarModel().getModelName() : "",
                    carVariant.getVariantName(),
                    carVariant.getCarModel() != null ? carVariant.getCarModel().getYear() : 2026).trim();
        } else {
            usedCar = usedCarRepository.findById(request.getUsedCarId())
                    .orElseThrow(() -> new ResourceNotFoundException("UsedCar", "id", request.getUsedCarId()));
            if (usedCar.getStatus() != UsedCarStatus.AVAILABLE) {
                throw new DuplicateBookingException("This pre-owned vehicle is no longer available for test drives (Status: " + usedCar.getStatus() + ").");
            }
            vehicleDisplayName = String.format("%s %s %s (%d Pre-Owned)",
                    usedCar.getBrand(),
                    usedCar.getModelName(),
                    usedCar.getVariantName(),
                    usedCar.getManufacturingYear()).trim();
        }

        // 3. Time slot validation
        String normalizedSlot = request.getTimeSlot().trim();
        if (!OPERATIONAL_TIME_SLOTS.contains(normalizedSlot)) {
            throw new IllegalArgumentException("Invalid time slot selected. Available slots: " + String.join(", ", OPERATIONAL_TIME_SLOTS));
        }

        // 4. Duplicate active slot check (First Line of Defense - Application level)
        // CONFIRMED and COMPLETED bookings occupy the slot.
        // CANCELLED and REJECTED bookings release the slot.
        List<TestDriveBooking> dayBookings = bookingRepository.findByShowroomIdAndBookingDate(
                showroom.getId(), request.getBookingDate()
        );

        boolean slotOccupied = dayBookings.stream()
                .anyMatch(b -> normalizedSlot.equalsIgnoreCase(b.getTimeSlot())
                        && !"CANCELLED".equalsIgnoreCase(b.getStatus())
                        && !"REJECTED".equalsIgnoreCase(b.getStatus()));

        if (slotOccupied) {
            throw new DuplicateBookingException(String.format(
                    "This test-drive time slot (%s) is already booked for %s on %s. Please choose an alternate slot.",
                    normalizedSlot, showroom.getName(), request.getBookingDate()
            ));
        }

        // 5. Generate Unique Booking Reference
        String bookingRef = String.format("AV-BK-%d-%s",
                request.getBookingDate().getYear(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Phase 5.1: Every booking creates a distinct entity record with its own primary key ID and reference
        TestDriveBooking booking = new TestDriveBooking();
        booking.setBookingRef(bookingRef);
        booking.setShowroom(showroom);
        booking.setCarVariant(carVariant);
        booking.setUsedCar(usedCar);
        booking.setUser(user);
        booking.setCustomerName(request.getCustomerName().trim());
        booking.setCustomerPhone(request.getCustomerPhone().trim());
        booking.setCustomerEmail(request.getCustomerEmail().trim());
        booking.setBookingDate(request.getBookingDate());
        booking.setTimeSlot(normalizedSlot);
        booking.setDriveMode(request.getDriveMode() != null ? request.getDriveMode().trim().toUpperCase() : "SHOWROOM");
        booking.setDoorstepAddress(request.getDoorstepAddress() != null ? request.getDoorstepAddress().trim() : null);
        booking.setConsentGiven(Boolean.TRUE.equals(request.getConsentGiven()));
        booking.setStatus("CONFIRMED");

        // 6. Concurrency-Safe Database Persistence (Second Line of Defense - Partial Unique Index uq_test_drive_active_slot)
        TestDriveBooking savedBooking;
        try {
            savedBooking = bookingRepository.saveAndFlush(booking);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Database partial unique index caught duplicate active booking race condition for slot {}: {}", normalizedSlot, ex.getMessage());
            throw new DuplicateBookingException(String.format(
                    "This test-drive time slot (%s) is already booked for %s on %s. Please choose an alternate slot.",
                    normalizedSlot, showroom.getName(), request.getBookingDate()
            ));
        }

        // 7. Dispatch Simulated Mock Notifications
        List<MockNotificationResponse> notifications = mockNotificationService.dispatchBookingConfirmation(savedBooking, vehicleDisplayName);

        BookingResponse response = carMapper.toBookingResponse(savedBooking);
        response.setNotifications(notifications);
        return response;
    }

    // ==========================================
    // 3. CUSTOMER BOOKING HISTORY & ISOLATION
    // ==========================================
    @Transactional(readOnly = true)
    public List<BookingResponse> getCustomerBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return bookingRepository.findByUserIdOrderByBookingDateDescCreatedAtDesc(user.getId()).stream()
                .map(b -> {
                    BookingResponse res = carMapper.toBookingResponse(b);
                    res.setNotifications(mockNotificationService.getNotificationsByBookingId(b.getId()));
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id, String userEmail, boolean isAdmin) {
        TestDriveBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestDriveBooking", "id", id));

        // Strict customer isolation
        if (!isAdmin && (booking.getUser() == null || !booking.getUser().getEmail().equalsIgnoreCase(userEmail))) {
            throw new AccessDeniedException("You do not have permission to view this booking record.");
        }

        BookingResponse res = carMapper.toBookingResponse(booking);
        res.setNotifications(mockNotificationService.getNotificationsByBookingId(booking.getId()));
        return res;
    }

    public BookingResponse cancelCustomerBooking(Long id, String reason, String userEmail, boolean isAdmin) {
        TestDriveBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestDriveBooking", "id", id));

        if (!isAdmin && (booking.getUser() == null || !booking.getUser().getEmail().equalsIgnoreCase(userEmail))) {
            throw new AccessDeniedException("You do not have permission to modify this booking record.");
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("This test drive booking is already cancelled.");
        }

        String oldStatus = booking.getStatus();
        booking.setStatus("CANCELLED");
        TestDriveBooking updated = bookingRepository.save(booking);

        List<MockNotificationResponse> notifications = mockNotificationService.dispatchStatusUpdateNotification(
                updated, oldStatus, "CANCELLED", reason != null ? reason : "Cancelled by customer request"
        );

        BookingResponse res = carMapper.toBookingResponse(updated);
        res.setNotifications(notifications);
        return res;
    }

    // ==========================================
    // 4. ADMIN BOOKING MANAGEMENT
    // ==========================================
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings(LocalDate date, Long showroomId, String status) {
        List<TestDriveBooking> all = bookingRepository.findAllByOrderByBookingDateDescCreatedAtDesc();

        return all.stream()
                .filter(b -> date == null || date.equals(b.getBookingDate()))
                .filter(b -> showroomId == null || (b.getShowroom() != null && showroomId.equals(b.getShowroom().getId())))
                .filter(b -> status == null || status.isBlank() || status.equalsIgnoreCase(b.getStatus()))
                .map(b -> {
                    BookingResponse res = carMapper.toBookingResponse(b);
                    res.setNotifications(mockNotificationService.getNotificationsByBookingId(b.getId()));
                    return res;
                })
                .collect(Collectors.toList());
    }

    public BookingResponse updateBookingStatus(Long id, String newStatus, String notes) {
        TestDriveBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestDriveBooking", "id", id));

        String targetStatus = newStatus.trim().toUpperCase();
        if (!List.of("CONFIRMED", "PENDING", "CANCELLED", "REJECTED", "COMPLETED").contains(targetStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus + ". Allowed statuses: CONFIRMED, PENDING, CANCELLED, REJECTED, COMPLETED");
        }

        String oldStatus = booking.getStatus();
        booking.setStatus(targetStatus);
        TestDriveBooking updated = bookingRepository.save(booking);

        List<MockNotificationResponse> notifications = mockNotificationService.dispatchStatusUpdateNotification(
                updated, oldStatus, targetStatus, notes
        );

        BookingResponse res = carMapper.toBookingResponse(updated);
        res.setNotifications(notifications);
        return res;
    }
}
