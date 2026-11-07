package com.autovista;

import com.autovista.dto.request.CreateBookingRequest;
import com.autovista.dto.request.UpdateBookingStatusRequest;
import com.autovista.entity.*;
import com.autovista.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestDriveBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShowroomRepository showroomRepository;

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CarVariantRepository carVariantRepository;

    @Autowired
    private UsedCarRepository usedCarRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDriveBookingRepository bookingRepository;

    @Autowired
    private MockNotificationRepository notificationRepository;

    @Autowired
    private LeadCommunicationRepository communicationRepository;

    @Autowired
    private LeadStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private LeadAssignmentRepository assignmentRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Showroom testShowroom;
    private CarVariant testVariant;
    private UsedCar testAvailableUsedCar;
    private UsedCar testSoldUsedCar;
    private User testCustomer;
    private User testCustomer2;

    @BeforeEach
    void setUp() {
        communicationRepository.deleteAll();
        statusHistoryRepository.deleteAll();
        assignmentRepository.deleteAll();
        leadRepository.deleteAll();
        notificationRepository.deleteAll();
        bookingRepository.deleteAll();
        usedCarRepository.deleteAll();
        carVariantRepository.deleteAll();
        carModelRepository.deleteAll();
        showroomRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Seed Showroom
        testShowroom = new Showroom();
        testShowroom.setName("AutoVista Gomti Nagar Flagship");
        testShowroom.setCode("AV-GN-01");
        testShowroom.setCity("Lucknow");
        testShowroom.setAddress("Vibhuti Khand, Gomti Nagar, Lucknow, UP");
        testShowroom.setPhone("+91 522 2728001");
        testShowroom.setEmail("gomtinagar@autovista.in");
        testShowroom.setIsActive(true);
        testShowroom = showroomRepository.save(testShowroom);

        // 2. Seed Customer Users
        testCustomer = new User();
        testCustomer.setFullName("Aarav Sharma");
        testCustomer.setEmail("aarav.sharma@example.com");
        testCustomer.setPasswordHash(passwordEncoder.encode("Password@123"));
        testCustomer.setPhone("+91 98765 43210");
        testCustomer.setRole(Role.CUSTOMER);
        testCustomer.setIsActive(true);
        testCustomer = userRepository.save(testCustomer);

        testCustomer2 = new User();
        testCustomer2.setFullName("Neha Rastogi");
        testCustomer2.setEmail("neha.rastogi@example.com");
        testCustomer2.setPasswordHash(passwordEncoder.encode("Password@123"));
        testCustomer2.setPhone("+91 98765 99887");
        testCustomer2.setRole(Role.CUSTOMER);
        testCustomer2.setIsActive(true);
        testCustomer2 = userRepository.save(testCustomer2);

        // 3. Seed New Car Model and Variant
        CarModel model = new CarModel();
        model.setBrand("AutoVista");
        model.setModelName("Model H");
        model.setBodyType("Sedan");
        model.setYear(2026);
        model.setIsActive(true);
        model = carModelRepository.save(model);

        testVariant = new CarVariant();
        testVariant.setCarModel(model);
        testVariant.setVariantName("ZX Automatic");
        testVariant.setFuelType("PETROL");
        testVariant.setTransmission("AUTOMATIC");
        testVariant.setExShowroomPrice(new BigDecimal("1245000.00"));
        testVariant.setIsAvailable(true);
        testVariant = carVariantRepository.save(testVariant);

        // 4. Seed Used Cars (One Available, One Sold)
        testAvailableUsedCar = new UsedCar();
        testAvailableUsedCar.setBrand("AutoVista");
        testAvailableUsedCar.setModelName("Model C");
        testAvailableUsedCar.setVariantName("VX Diesel");
        testAvailableUsedCar.setManufacturingYear(2022);
        testAvailableUsedCar.setPrice(new BigDecimal("845000.00"));
        testAvailableUsedCar.setOdometerKm(41200);
        testAvailableUsedCar.setFuelType("DIESEL");
        testAvailableUsedCar.setTransmission("MANUAL");
        testAvailableUsedCar.setRawRegistrationNumber("UP32AB1234");
        testAvailableUsedCar.setStatus(UsedCarStatus.AVAILABLE);
        testAvailableUsedCar.setShowroom(testShowroom);
        testAvailableUsedCar = usedCarRepository.save(testAvailableUsedCar);

        testSoldUsedCar = new UsedCar();
        testSoldUsedCar.setBrand("AutoVista");
        testSoldUsedCar.setModelName("Model S");
        testSoldUsedCar.setVariantName("LX Petrol");
        testSoldUsedCar.setManufacturingYear(2021);
        testSoldUsedCar.setPrice(new BigDecimal("695000.00"));
        testSoldUsedCar.setOdometerKm(52000);
        testSoldUsedCar.setFuelType("PETROL");
        testSoldUsedCar.setTransmission("MANUAL");
        testSoldUsedCar.setRawRegistrationNumber("UP32CD5678");
        testSoldUsedCar.setStatus(UsedCarStatus.SOLD);
        testSoldUsedCar.setShowroom(testShowroom);
        testSoldUsedCar = usedCarRepository.save(testSoldUsedCar);
    }

    @Test
    @DisplayName("1. Public Slot Availability: GET /api/test-drives/slots returns operational and available slots")
    void testPublicSlotAvailability() throws Exception {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/test-drives/slots")
                        .param("showroomId", testShowroom.getId().toString())
                        .param("date", tomorrow.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.showroomId").value(testShowroom.getId()))
                .andExpect(jsonPath("$.data.operationalSlots", hasSize(6)))
                .andExpect(jsonPath("$.data.availableSlots", hasSize(6)))
                .andExpect(jsonPath("$.data.bookedSlots", hasSize(0)));
    }

    @Test
    @DisplayName("2. Security: Unauthenticated POST /api/test-drives must be rejected with 401 or 403")
    void testUnauthenticatedBookingRejected() throws Exception {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setShowroomId(testShowroom.getId());
        req.setCarVariantId(testVariant.getId());
        req.setCustomerName("Guest User");
        req.setCustomerPhone("+91 99999 88888");
        req.setCustomerEmail("guest@example.com");
        req.setBookingDate(LocalDate.now().plusDays(2));
        req.setTimeSlot("11:00 AM - 12:00 PM");
        req.setConsentGiven(true);

        mockMvc.perform(post("/api/test-drives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    @DisplayName("3. Happy Path: Authenticated customer books new car variant successfully")
    void testSuccessfulNewCarBookingWithNotifications() throws Exception {
        LocalDate bookingDate = LocalDate.now().plusDays(2);
        String slot = "11:00 AM - 12:00 PM";

        CreateBookingRequest req = new CreateBookingRequest();
        req.setShowroomId(testShowroom.getId());
        req.setCarVariantId(testVariant.getId());
        req.setCustomerName("Aarav Sharma");
        req.setCustomerPhone("+91 98765 43210");
        req.setCustomerEmail("aarav.sharma@example.com");
        req.setBookingDate(bookingDate);
        req.setTimeSlot(slot);
        req.setDriveMode("SHOWROOM");
        req.setConsentGiven(true);

        mockMvc.perform(post("/api/test-drives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bookingRef", startsWith("AV-BK-")))
                .andExpect(jsonPath("$.data.timeSlot").value(slot))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.notifications", hasSize(3))); // SMS, Email, WhatsApp

        // Verify slot is now marked as booked
        mockMvc.perform(get("/api/test-drives/slots")
                        .param("showroomId", testShowroom.getId().toString())
                        .param("date", bookingDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookedSlots", hasItem(slot)))
                .andExpect(jsonPath("$.data.availableSlots", not(hasItem(slot))));
    }

    @Test
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    @DisplayName("4. Mandatory Requirement #6: Duplicate booking for same showroom, date, and slot returns HTTP 409 Conflict")
    void testDuplicateSlotBookingReturns409Conflict() throws Exception {
        LocalDate bookingDate = LocalDate.now().plusDays(3);
        String slot = "02:00 PM - 03:00 PM";

        // First Booking: Successful
        CreateBookingRequest firstReq = new CreateBookingRequest();
        firstReq.setShowroomId(testShowroom.getId());
        firstReq.setCarVariantId(testVariant.getId());
        firstReq.setCustomerName("Aarav Sharma");
        firstReq.setCustomerPhone("+91 98765 43210");
        firstReq.setCustomerEmail("aarav.sharma@example.com");
        firstReq.setBookingDate(bookingDate);
        firstReq.setTimeSlot(slot);
        firstReq.setDriveMode("SHOWROOM");
        firstReq.setConsentGiven(true);

        mockMvc.perform(post("/api/test-drives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstReq)))
                .andExpect(status().isCreated());

        // Second Booking: Identical Showroom, Date, and Slot -> Must return HTTP 409 Conflict
        CreateBookingRequest duplicateReq = new CreateBookingRequest();
        duplicateReq.setShowroomId(testShowroom.getId());
        duplicateReq.setCarVariantId(testVariant.getId());
        duplicateReq.setCustomerName("Second Customer");
        duplicateReq.setCustomerPhone("+91 91234 56789");
        duplicateReq.setCustomerEmail("second@example.com");
        duplicateReq.setBookingDate(bookingDate);
        duplicateReq.setTimeSlot(slot);
        duplicateReq.setDriveMode("SHOWROOM");
        duplicateReq.setConsentGiven(true);

        mockMvc.perform(post("/api/test-drives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message", containsString("already booked")));

        // Ensure exactly 1 booking exists in repository
        List<TestDriveBooking> bookings = bookingRepository.findAll();
        assertEquals(1, bookings.size(), "Only the first booking should be persisted; duplicate attempt must not create orphaned record!");
    }

    @Test
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    @DisplayName("5. Vehicle Status Validation: Cannot book test drive for SOLD or BOOKED used vehicle")
    void testBookingForSoldUsedCarReturns409Conflict() throws Exception {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setShowroomId(testShowroom.getId());
        req.setUsedCarId(testSoldUsedCar.getId());
        req.setCustomerName("Aarav Sharma");
        req.setCustomerPhone("+91 98765 43210");
        req.setCustomerEmail("aarav.sharma@example.com");
        req.setBookingDate(LocalDate.now().plusDays(4));
        req.setTimeSlot("03:00 PM - 04:00 PM");
        req.setConsentGiven(true);

        mockMvc.perform(post("/api/test-drives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("no longer available for test drives")));
    }

    @Test
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    @DisplayName("6. Customer Data Isolation: Customer views only their own test drive bookings")
    void testCustomerDataIsolation() throws Exception {
        // Customer 1 Booking
        CreateBookingRequest req1 = new CreateBookingRequest();
        req1.setShowroomId(testShowroom.getId());
        req1.setCarVariantId(testVariant.getId());
        req1.setCustomerName("Aarav Sharma");
        req1.setCustomerPhone("+91 98765 43210");
        req1.setCustomerEmail("aarav.sharma@example.com");
        req1.setBookingDate(LocalDate.now().plusDays(2));
        req1.setTimeSlot("10:00 AM - 11:00 AM");
        req1.setConsentGiven(true);

        mockMvc.perform(post("/api/test-drives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        // View My Bookings as Aarav
        mockMvc.perform(get("/api/test-drives/my-bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].customerEmail").value("aarav.sharma@example.com"));
    }

    @Test
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    @DisplayName("7. Cancellation: Customer can cancel their booking and free the slot")
    void testCustomerCanCancelBooking() throws Exception {
        LocalDate bookingDate = LocalDate.now().plusDays(2);
        String slot = "04:00 PM - 05:00 PM";

        CreateBookingRequest req = new CreateBookingRequest();
        req.setShowroomId(testShowroom.getId());
        req.setCarVariantId(testVariant.getId());
        req.setCustomerName("Aarav Sharma");
        req.setCustomerPhone("+91 98765 43210");
        req.setCustomerEmail("aarav.sharma@example.com");
        req.setBookingDate(bookingDate);
        req.setTimeSlot(slot);
        req.setConsentGiven(true);

        String responseJson = mockMvc.perform(post("/api/test-drives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long bookingId = objectMapper.readTree(responseJson).path("data").path("id").asLong();

        // Customer cancels
        mockMvc.perform(patch("/api/test-drives/" + bookingId + "/cancel")
                        .param("reason", "Rescheduling personal appointment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        // Slot should now be available again!
        mockMvc.perform(get("/api/test-drives/slots")
                        .param("showroomId", testShowroom.getId().toString())
                        .param("date", bookingDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableSlots", hasItem(slot)));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("8. Role Security: Customer calling GET /api/admin/bookings receives 403 Forbidden")
    void testCustomerCannotAccessAdminBookings() throws Exception {
        mockMvc.perform(get("/api/admin/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@autovista.in", roles = "ADMIN")
    @DisplayName("9. Admin Booking Management: Admin views all bookings and updates status")
    void testAdminCanViewAndUpdateBookingStatus() throws Exception {
        // Direct seed a booking
        TestDriveBooking booking = new TestDriveBooking();
        booking.setBookingRef("AV-BK-2026-TEST");
        booking.setShowroom(testShowroom);
        booking.setCarVariant(testVariant);
        booking.setCustomerName("VIP Client");
        booking.setCustomerPhone("+91 98765 00000");
        booking.setCustomerEmail("vip@example.com");
        booking.setBookingDate(LocalDate.now().plusDays(5));
        booking.setTimeSlot("05:00 PM - 06:00 PM");
        booking.setStatus("CONFIRMED");
        booking.setConsentGiven(true);
        booking = bookingRepository.save(booking);

        // Admin retrieves bookings
        mockMvc.perform(get("/api/admin/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].bookingRef").value("AV-BK-2026-TEST"));

        // Admin updates status to COMPLETED
        UpdateBookingStatusRequest updateReq = new UpdateBookingStatusRequest();
        updateReq.setStatus("COMPLETED");
        updateReq.setNotes("Test drive conducted successfully on highway circuit.");

        mockMvc.perform(patch("/api/admin/bookings/" + booking.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("10. Concurrency: Two simultaneous booking requests for same slot -> exactly one succeeds, one fails with 409")
    void testConcurrentBookingRequestsRaceCondition() throws Exception {
        LocalDate bookingDate = LocalDate.now().plusDays(6);
        String slot = "02:00 PM - 03:00 PM";

        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int index = i;
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await(); // Wait for simultaneous release

                    CreateBookingRequest req = new CreateBookingRequest();
                    req.setShowroomId(testShowroom.getId());
                    req.setCarVariantId(testVariant.getId());
                    req.setCustomerName("Concurrent Customer " + index);
                    req.setCustomerPhone("+91 90000 0000" + index);
                    req.setCustomerEmail("concurrent" + index + "@example.com");
                    req.setBookingDate(bookingDate);
                    req.setTimeSlot(slot);
                    req.setConsentGiven(true);

                    // UserPrincipal simulation
                    var result = mockMvc.perform(post("/api/test-drives")
                                    .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("aarav.sharma@example.com").roles("CUSTOMER"))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(req)))
                            .andReturn();

                    int status = result.getResponse().getStatus();
                    if (status == 201) {
                        successCount.incrementAndGet();
                    } else if (status == 409) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // unexpected error
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown(); // Fire both threads at the exact same instant!
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(1, successCount.get(), "Exactly one concurrent request must succeed with 201 Created");
        assertEquals(1, conflictCount.get(), "The concurrent duplicate request must fail with 409 Conflict");
        assertEquals(1, bookingRepository.findByShowroomIdAndBookingDate(testShowroom.getId(), bookingDate).size(),
                "Exactly 1 booking record must exist in the database for this slot!");
    }

    @Test
    @DisplayName("11. Phase 5.1: Cancelled booking releases slot; new customer receives distinct ID, reference, and isolated history")
    void testCancelledBookingReleasesSlotAndAllowsNewBookingWithDistinctIdAndRef() throws Exception {
        LocalDate bookingDate = LocalDate.now().plusDays(7);
        String slot = "11:00 AM - 12:00 PM";

        // Step 1: Customer 1 (Aarav) books slot
        CreateBookingRequest req1 = new CreateBookingRequest();
        req1.setShowroomId(testShowroom.getId());
        req1.setCarVariantId(testVariant.getId());
        req1.setCustomerName("Aarav Sharma");
        req1.setCustomerPhone("+91 98765 43210");
        req1.setCustomerEmail("aarav.sharma@example.com");
        req1.setBookingDate(bookingDate);
        req1.setTimeSlot(slot);
        req1.setConsentGiven(true);

        var res1 = mockMvc.perform(post("/api/test-drives")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("aarav.sharma@example.com").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andReturn();

        String resJson1 = res1.getResponse().getContentAsString();
        Long bookingId1 = objectMapper.readTree(resJson1).path("data").path("id").asLong();
        String bookingRef1 = objectMapper.readTree(resJson1).path("data").path("bookingRef").asText();

        // Step 2: Customer 1 cancels booking
        mockMvc.perform(patch("/api/test-drives/" + bookingId1 + "/cancel?reason=Schedule+conflict")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("aarav.sharma@example.com").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        // Step 3: Verify slot is immediately available
        mockMvc.perform(get("/api/test-drives/slots")
                        .param("showroomId", testShowroom.getId().toString())
                        .param("date", bookingDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableSlots", hasItem(slot)))
                .andExpect(jsonPath("$.data.bookedSlots", not(hasItem(slot))));

        // Step 4: Customer 2 (Neha) books the exact same slot
        CreateBookingRequest req2 = new CreateBookingRequest();
        req2.setShowroomId(testShowroom.getId());
        req2.setCarVariantId(testVariant.getId());
        req2.setCustomerName("Neha Rastogi");
        req2.setCustomerPhone("+91 98765 99887");
        req2.setCustomerEmail("neha.rastogi@example.com");
        req2.setBookingDate(bookingDate);
        req2.setTimeSlot(slot);
        req2.setConsentGiven(true);

        var res2 = mockMvc.perform(post("/api/test-drives")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("neha.rastogi@example.com").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andReturn();

        String resJson2 = res2.getResponse().getContentAsString();
        Long bookingId2 = objectMapper.readTree(resJson2).path("data").path("id").asLong();
        String bookingRef2 = objectMapper.readTree(resJson2).path("data").path("bookingRef").asText();

        // Step 5: Verification of distinct IDs and distinct references
        org.junit.jupiter.api.Assertions.assertNotEquals(bookingId1, bookingId2, "Old and new bookings must have distinct IDs");
        org.junit.jupiter.api.Assertions.assertNotEquals(bookingRef1, bookingRef2, "Old and new bookings must have distinct references");

        // Step 6: Verify Customer 1 history contains only Booking 1 (CANCELLED)
        mockMvc.perform(get("/api/test-drives/my-bookings")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("aarav.sharma@example.com").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(bookingId1))
                .andExpect(jsonPath("$.data[0].bookingRef").value(bookingRef1))
                .andExpect(jsonPath("$.data[0].customerName").value("Aarav Sharma"))
                .andExpect(jsonPath("$.data[0].status").value("CANCELLED"));

        // Step 7: Verify Customer 2 history contains only Booking 2 (CONFIRMED)
        mockMvc.perform(get("/api/test-drives/my-bookings")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("neha.rastogi@example.com").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(bookingId2))
                .andExpect(jsonPath("$.data[0].bookingRef").value(bookingRef2))
                .andExpect(jsonPath("$.data[0].customerName").value("Neha Rastogi"))
                .andExpect(jsonPath("$.data[0].status").value("CONFIRMED"));

        // Step 8: Verify both records exist in DB
        List<TestDriveBooking> allSlotBookings = bookingRepository.findByShowroomIdAndBookingDate(testShowroom.getId(), bookingDate);
        assertEquals(2, allSlotBookings.size(), "Both cancelled and new bookings must be preserved in DB");
    }

    @Test
    @DisplayName("12. Phase 5.1: REJECTED booking releases slot for a new customer booking")
    @WithMockUser(username = "admin@autovista.in", roles = {"ADMIN"})
    void testRejectedBookingReleasesSlotForNewCustomerBooking() throws Exception {
        LocalDate bookingDate = LocalDate.now().plusDays(8);
        String slot = "04:00 PM - 05:00 PM";

        // Step 1: Customer 1 books
        CreateBookingRequest req1 = new CreateBookingRequest();
        req1.setShowroomId(testShowroom.getId());
        req1.setCarVariantId(testVariant.getId());
        req1.setCustomerName("Aarav Sharma");
        req1.setCustomerPhone("+91 98765 43210");
        req1.setCustomerEmail("aarav.sharma@example.com");
        req1.setBookingDate(bookingDate);
        req1.setTimeSlot(slot);
        req1.setConsentGiven(true);

        var res1 = mockMvc.perform(post("/api/test-drives")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("aarav.sharma@example.com").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andReturn();

        Long bookingId1 = objectMapper.readTree(res1.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Step 2: Admin rejects booking
        UpdateBookingStatusRequest rejectReq = new UpdateBookingStatusRequest();
        rejectReq.setStatus("REJECTED");
        rejectReq.setNotes("Vehicle maintenance required");

        mockMvc.perform(patch("/api/admin/bookings/" + bookingId1 + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REJECTED"));

        // Step 3: Slot must be available
        mockMvc.perform(get("/api/test-drives/slots")
                        .param("showroomId", testShowroom.getId().toString())
                        .param("date", bookingDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableSlots", hasItem(slot)));

        // Step 4: Customer 2 books the same slot successfully
        CreateBookingRequest req2 = new CreateBookingRequest();
        req2.setShowroomId(testShowroom.getId());
        req2.setCarVariantId(testVariant.getId());
        req2.setCustomerName("Neha Rastogi");
        req2.setCustomerPhone("+91 98765 99887");
        req2.setCustomerEmail("neha.rastogi@example.com");
        req2.setBookingDate(bookingDate);
        req2.setTimeSlot(slot);
        req2.setConsentGiven(true);

        mockMvc.perform(post("/api/test-drives")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("neha.rastogi@example.com").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("13. Phase 5.1: COMPLETED booking continues occupying slot and rejects new bookings with 409 Conflict")
    @WithMockUser(username = "admin@autovista.in", roles = {"ADMIN"})
    void testCompletedBookingContinuesOccupyingSlotAndBlocksNewBookings() throws Exception {
        LocalDate bookingDate = LocalDate.now().plusDays(9);
        String slot = "10:00 AM - 11:00 AM";

        // Step 1: Customer 1 books
        CreateBookingRequest req1 = new CreateBookingRequest();
        req1.setShowroomId(testShowroom.getId());
        req1.setCarVariantId(testVariant.getId());
        req1.setCustomerName("Aarav Sharma");
        req1.setCustomerPhone("+91 98765 43210");
        req1.setCustomerEmail("aarav.sharma@example.com");
        req1.setBookingDate(bookingDate);
        req1.setTimeSlot(slot);
        req1.setConsentGiven(true);

        var res1 = mockMvc.perform(post("/api/test-drives")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("aarav.sharma@example.com").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andReturn();

        Long bookingId1 = objectMapper.readTree(res1.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Step 2: Admin marks booking as COMPLETED
        UpdateBookingStatusRequest completeReq = new UpdateBookingStatusRequest();
        completeReq.setStatus("COMPLETED");
        completeReq.setNotes("Test drive conducted successfully");

        mockMvc.perform(patch("/api/admin/bookings/" + bookingId1 + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        // Step 3: Slot must still be in bookedSlots
        mockMvc.perform(get("/api/test-drives/slots")
                        .param("showroomId", testShowroom.getId().toString())
                        .param("date", bookingDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookedSlots", hasItem(slot)))
                .andExpect(jsonPath("$.data.availableSlots", not(hasItem(slot))));

        // Step 4: Another customer attempting to book the completed slot must be rejected with 409 Conflict
        CreateBookingRequest req2 = new CreateBookingRequest();
        req2.setShowroomId(testShowroom.getId());
        req2.setCarVariantId(testVariant.getId());
        req2.setCustomerName("Neha Rastogi");
        req2.setCustomerPhone("+91 98765 99887");
        req2.setCustomerEmail("neha.rastogi@example.com");
        req2.setBookingDate(bookingDate);
        req2.setTimeSlot(slot);
        req2.setConsentGiven(true);

        mockMvc.perform(post("/api/test-drives")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("neha.rastogi@example.com").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already booked")));
    }
}
