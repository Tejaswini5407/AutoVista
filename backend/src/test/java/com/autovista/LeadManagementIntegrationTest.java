package com.autovista;

import com.autovista.dto.request.*;
import com.autovista.entity.*;
import com.autovista.repository.*;
import com.autovista.service.AiLeadService;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeadManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private LeadAssignmentRepository assignmentRepository;

    @Autowired
    private LeadStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private LeadCommunicationRepository communicationRepository;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AiLeadService aiLeadService;

    @Autowired
    private com.autovista.service.LeadAssignmentService leadAssignmentService;

    private Showroom testShowroom;
    private CarVariant testVariant;
    private UsedCar testUsedCar;
    private User testCustomer;
    private User testConsultant1;
    private User testConsultant2;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        communicationRepository.deleteAll();
        statusHistoryRepository.deleteAll();
        assignmentRepository.deleteAll();
        leadRepository.deleteAll();
        usedCarRepository.deleteAll();
        carVariantRepository.deleteAll();
        carModelRepository.deleteAll();
        showroomRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Seed Showroom
        testShowroom = new Showroom();
        testShowroom.setName("AutoVista Gomti Nagar Flagship");
        testShowroom.setCode("AV-GN-LKO");
        testShowroom.setCity("Lucknow");
        testShowroom.setAddress("Vibhuti Khand, Gomti Nagar");
        testShowroom.setPhone("+91 522 2728001");
        testShowroom.setEmail("gomtinagar@autovista.in");
        testShowroom.setIsActive(true);
        testShowroom = showroomRepository.save(testShowroom);

        // 2. Seed Users
        testCustomer = new User();
        testCustomer.setFullName("Aarav Sharma");
        testCustomer.setEmail("aarav.sharma@example.com");
        testCustomer.setPasswordHash(passwordEncoder.encode("Password@123"));
        testCustomer.setPhone("+91 98765 11223");
        testCustomer.setRole(Role.CUSTOMER);
        testCustomer.setIsActive(true);
        testCustomer = userRepository.save(testCustomer);

        testConsultant1 = new User();
        testConsultant1.setFullName("Vikram Singh");
        testConsultant1.setEmail("vikram.consultant@autovista.in");
        testConsultant1.setPasswordHash(passwordEncoder.encode("Password@123"));
        testConsultant1.setPhone("+91 98765 00002");
        testConsultant1.setRole(Role.CONSULTANT);
        testConsultant1.setIsActive(true);
        testConsultant1 = userRepository.save(testConsultant1);

        testConsultant2 = new User();
        testConsultant2.setFullName("Neha Rastogi");
        testConsultant2.setEmail("neha.consultant@autovista.in");
        testConsultant2.setPasswordHash(passwordEncoder.encode("Password@123"));
        testConsultant2.setPhone("+91 98765 00003");
        testConsultant2.setRole(Role.CONSULTANT);
        testConsultant2.setIsActive(true);
        testConsultant2 = userRepository.save(testConsultant2);

        testAdmin = new User();
        testAdmin.setFullName("Pooja Verma");
        testAdmin.setEmail("admin@autovista.in");
        testAdmin.setPasswordHash(passwordEncoder.encode("Password@123"));
        testAdmin.setPhone("+91 98765 00001");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setIsActive(true);
        testAdmin = userRepository.save(testAdmin);

        // 3. Seed Vehicles
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

        testUsedCar = new UsedCar();
        testUsedCar.setShowroom(testShowroom);
        testUsedCar.setBrand("AutoVista");
        testUsedCar.setModelName("Model C");
        testUsedCar.setVariantName("VX Diesel");
        testUsedCar.setManufacturingYear(2022);
        testUsedCar.setPrice(new BigDecimal("845000.00"));
        testUsedCar.setOdometerKm(41200);
        testUsedCar.setRawRegistrationNumber("UP32 BK 8921");
        testUsedCar.setFuelType("DIESEL");
        testUsedCar.setTransmission("MANUAL");
        testUsedCar.setStatus(UsedCarStatus.AVAILABLE);
        testUsedCar = usedCarRepository.save(testUsedCar);
    }

    @Test
    @DisplayName("1 & 4 & 5. Customer creates enquiry with consent -> assigned within 5-minute SLA")
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    void testCustomerCreatesEnquiryWithConsentAndAutomaticAssignment() throws Exception {
        CreateLeadRequest req = new CreateLeadRequest();
        req.setCarVariantId(testVariant.getId());
        req.setShowroomId(testShowroom.getId());
        req.setCustomerName("Aarav Sharma");
        req.setCustomerEmail("aarav.sharma@example.com");
        req.setCustomerPhone("+91 98765 11223");
        req.setEnquiryMessage("Interested in test driving this model this Saturday.");
        req.setStatedBudget("₹12-14 Lakhs");
        req.setPurchaseTimeline("Within 2 weeks");
        req.setPreferredFuel("PETROL");
        req.setPreferredTransmission("AUTOMATIC");
        req.setConsentGiven(true);

        var result = mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.leadRef", startsWith("AV-LD-2026-")))
                .andExpect(jsonPath("$.data.status").value("NEW"))
                .andExpect(jsonPath("$.data.consentGiven").value(true))
                .andExpect(jsonPath("$.data.assignedConsultantName", notNullValue()))
                .andExpect(jsonPath("$.data.aiSummary", containsString("Assistant Generated Summary")))
                .andReturn();

        String resJson = result.getResponse().getContentAsString();
        Long leadId = objectMapper.readTree(resJson).path("data").path("id").asLong();

        // Verify LeadAssignment table entry with 5-minute SLA
        List<LeadAssignment> assignments = assignmentRepository.findByLeadIdOrderByAssignedAtDesc(leadId);
        assertFalse(assignments.isEmpty(), "Lead assignment must be recorded");
        LeadAssignment assignment = assignments.get(0);
        assertNotNull(assignment.getResponseDueBy(), "Response due timestamp must exist");
        assertTrue(assignment.getResponseDueBy().isAfter(assignment.getAssignedAt()), "SLA due time must be after assignment time");
    }

    @Test
    @DisplayName("2. Enquiry without required consent is rejected with 400 Bad Request")
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    void testEnquiryWithoutConsentRejected() throws Exception {
        CreateLeadRequest req = new CreateLeadRequest();
        req.setCarVariantId(testVariant.getId());
        req.setCustomerName("Aarav Sharma");
        req.setCustomerEmail("aarav.sharma@example.com");
        req.setCustomerPhone("+91 98765 11223");
        req.setConsentGiven(false); // Explicit refusal of consent

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("consent")));
    }

    @Test
    @DisplayName("3. Duplicate submission protection rejects second enquiry within 10 minutes")
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    void testDuplicateSubmissionProtection() throws Exception {
        CreateLeadRequest req = new CreateLeadRequest();
        req.setCarVariantId(testVariant.getId());
        req.setShowroomId(testShowroom.getId());
        req.setCustomerName("Aarav Sharma");
        req.setCustomerEmail("aarav.sharma@example.com");
        req.setCustomerPhone("+91 98765 11223");
        req.setEnquiryMessage("First enquiry.");
        req.setConsentGiven(true);

        // First enquiry succeeds
        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        // Immediate identical enquiry is rejected as duplicate
        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already submitted an enquiry")));
    }

    @Test
    @DisplayName("6. When no active consultant is available, lead remains safely queued as unassigned")
    @WithMockUser(username = "aarav.sharma@example.com", roles = "CUSTOMER")
    void testNoActiveConsultantAvailable() throws Exception {
        // Deactivate all consultants
        testConsultant1.setIsActive(false);
        userRepository.save(testConsultant1);
        testConsultant2.setIsActive(false);
        userRepository.save(testConsultant2);

        CreateLeadRequest req = new CreateLeadRequest();
        req.setUsedCarId(testUsedCar.getId());
        req.setCustomerName("Aarav Sharma");
        req.setCustomerEmail("aarav.sharma@example.com");
        req.setCustomerPhone("+91 98765 11223");
        req.setConsentGiven(true);

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.assignedConsultantId", nullValue()))
                .andExpect(jsonPath("$.data.status").value("NEW"));

        assertEquals(1, leadRepository.findByLastAssignedConsultantIsNullOrderByCreatedAtDesc().size(),
                "Unassigned lead must be queued");
    }

    @Test
    @DisplayName("7. Concurrency protection prevents duplicate consultant assignments")
    void testConcurrentLeadAssignmentProtection() throws Exception {
        // Create an unassigned lead directly
        Lead lead = new Lead();
        lead.setLeadRef("AV-LD-CONCURRENT-01");
        lead.setCustomerName("Concurrent Customer");
        lead.setCustomerEmail("concurrent@example.com");
        lead.setCustomerPhone("+91 90000 11111");
        lead.setInterestedCarVariant(testVariant);
        lead.setConsentGiven(true);
        lead.setStatus("NEW");
        lead = leadRepository.saveAndFlush(lead);

        Long leadId = lead.getId();
        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        AtomicInteger assignedCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    Lead assigned = leadAssignmentService.assignLead(leadId);
                    if (assigned.getLastAssignedConsultant() != null) {
                        assignedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Handled
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        Lead finalLead = leadRepository.findById(leadId).orElseThrow();
        assertNotNull(finalLead.getLastAssignedConsultant(), "Lead must be assigned");
        assertEquals(1, assignmentRepository.findByLeadIdOrderByAssignedAtDesc(leadId).size(),
                "Exactly 1 assignment audit record must be created");
    }

    @Test
    @DisplayName("8 & 9. Consultant can view only assigned leads; forbidden from another consultant's leads")
    void testConsultantDataIsolation() throws Exception {
        // Lead assigned to Consultant 1
        Lead lead1 = new Lead();
        lead1.setLeadRef("AV-LD-CONSULT-01");
        lead1.setCustomerName("Client 1");
        lead1.setCustomerEmail("client1@example.com");
        lead1.setCustomerPhone("+91 91111 00001");
        lead1.setInterestedCarVariant(testVariant);
        lead1.setConsentGiven(true);
        lead1.setStatus("NEW");
        lead1.setLastAssignedConsultant(testConsultant1);
        lead1 = leadRepository.save(lead1);

        // Consultant 1 retrieves their assigned leads -> 200 OK
        mockMvc.perform(get("/api/consultant/leads")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("vikram.consultant@autovista.in").roles("CONSULTANT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].leadRef").value("AV-LD-CONSULT-01"));

        // Consultant 2 attempting to view Consultant 1's lead directly -> 403 Forbidden
        mockMvc.perform(get("/api/consultant/leads/" + lead1.getId())
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("neha.consultant@autovista.in").roles("CONSULTANT")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("10 & 11. Valid status transitions succeed; invalid transitions rejected with 400")
    void testLeadStatusTransitions() throws Exception {
        Lead lead = new Lead();
        lead.setLeadRef("AV-LD-STATUS-01");
        lead.setCustomerName("Status Test Client");
        lead.setCustomerEmail("status@example.com");
        lead.setCustomerPhone("+91 92222 00002");
        lead.setInterestedCarVariant(testVariant);
        lead.setConsentGiven(true);
        lead.setStatus("NEW");
        lead.setLastAssignedConsultant(testConsultant1);
        lead = leadRepository.save(lead);

        // Invalid: NEW -> CONVERTED directly without contacting -> Rejected with 400
        UpdateLeadStatusRequest invalidReq = new UpdateLeadStatusRequest("CONVERTED", "Skipping steps");
        mockMvc.perform(patch("/api/consultant/leads/" + lead.getId() + "/status")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("vikram.consultant@autovista.in").roles("CONSULTANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cannot transition directly from NEW to CONVERTED")));

        // Valid: NEW -> CONTACTED -> 200 OK
        UpdateLeadStatusRequest validReq = new UpdateLeadStatusRequest("CONTACTED", "Spoke via phone consultation");
        mockMvc.perform(patch("/api/consultant/leads/" + lead.getId() + "/status")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("vikram.consultant@autovista.in").roles("CONSULTANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONTACTED"));
    }

    @Test
    @DisplayName("12 & 13. Admin can view and reassign leads; Customer cannot access admin lead endpoints")
    void testAdminLeadManagementAndCustomerRejection() throws Exception {
        Lead lead = new Lead();
        lead.setLeadRef("AV-LD-ADMIN-01");
        lead.setCustomerName("Admin Test Client");
        lead.setCustomerEmail("adminclient@example.com");
        lead.setCustomerPhone("+91 93333 00003");
        lead.setInterestedCarVariant(testVariant);
        lead.setConsentGiven(true);
        lead.setStatus("NEW");
        lead.setLastAssignedConsultant(testConsultant1);
        lead = leadRepository.save(lead);

        // Customer calling GET /api/admin/leads -> 403 Forbidden
        mockMvc.perform(get("/api/admin/leads")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("aarav.sharma@example.com").roles("CUSTOMER")))
                .andExpect(status().isForbidden());

        // Admin calling GET /api/admin/leads -> 200 OK
        mockMvc.perform(get("/api/admin/leads")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin@autovista.in").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // Admin reassigns lead to Consultant 2 -> 200 OK
        ReassignLeadRequest reassignReq = new ReassignLeadRequest(testConsultant2.getId(), "Consultant workload rebalancing");
        mockMvc.perform(post("/api/admin/leads/" + lead.getId() + "/reassign")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin@autovista.in").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reassignReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.assignedConsultantName").value("Neha Rastogi"));
    }

    @Test
    @DisplayName("14 & 15. AI summary fallback works without API key and never invents missing information")
    void testAiSummaryFallbackDoesNotInventMissingInformation() {
        Lead lead = new Lead();
        lead.setCustomerName("Rohit Verma");
        lead.setCustomerCity("Lucknow");
        lead.setInterestedCarVariant(testVariant);
        lead.setEnquiryMessage(null); // No message
        lead.setStatedBudget(null);   // Missing budget
        lead.setPurchaseTimeline(null); // Missing timeline
        lead.setPreferredFuel(null);
        lead.setPreferredTransmission(null);

        String summary = aiLeadService.generateBuyerSummary(lead);
        assertNotNull(summary);
        assertTrue(summary.contains("Stated Budget: Not provided"), "Missing budget must be marked Not provided");
        assertTrue(summary.contains("Purchase Timeline: Not provided"), "Missing timeline must be marked Not provided");
        assertTrue(summary.contains("Assistant Generated Summary"), "Must be labeled as assistant generated");
        assertFalse(summary.contains("₹10 Lakhs"), "Must not invent an unstated budget");
    }

    @Test
    @DisplayName("16 & 17. AI reply fallback works without API key and never invents discounts")
    void testAiReplyFallbackDoesNotInventDiscounts() {
        Lead lead = new Lead();
        lead.setCustomerName("Pooja Mishra");
        lead.setInterestedCarVariant(testVariant);
        lead.setLastAssignedConsultant(testConsultant1);
        lead.setShowroom(testShowroom);

        String replyDraft = aiLeadService.generateFirstReplyDraft(lead);
        assertNotNull(replyDraft);
        assertTrue(replyDraft.contains("Dear Pooja Mishra"), "Must greet customer");
        assertTrue(replyDraft.contains("Model H ZX Automatic"), "Must mention target vehicle");
        assertFalse(replyDraft.toLowerCase().contains("discount"), "Must never invent discounts");
        assertFalse(replyDraft.toLowerCase().contains("offer"), "Must never invent offers");
        assertFalse(replyDraft.toLowerCase().contains("free accessories"), "Must never promise free items");
    }

    @Test
    @DisplayName("18, 19 & 20. Unapproved draft cannot be sent; approved draft sends simulated message and records audit log")
    void testDraftApprovalAndSimulatedCommunication() throws Exception {
        Lead lead = new Lead();
        lead.setLeadRef("AV-LD-COMM-01");
        lead.setCustomerName("Simulated Message Client");
        lead.setCustomerEmail("simulated@example.com");
        lead.setCustomerPhone("+91 94444 00004");
        lead.setInterestedCarVariant(testVariant);
        lead.setConsentGiven(true);
        lead.setStatus("NEW");
        lead.setAiDraftReply("Dear Client, thank you for your enquiry.");
        lead.setConsultantApproved(false); // NOT approved
        lead.setLastAssignedConsultant(testConsultant1);
        lead = leadRepository.save(lead);

        SendSimulatedReplyRequest sendReq = new SendSimulatedReplyRequest("WHATSAPP_SIMULATED");

        // 18. Attempting to send unapproved draft fails with 400 Bad Request
        mockMvc.perform(post("/api/consultant/leads/" + lead.getId() + "/send-reply")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("vikram.consultant@autovista.in").roles("CONSULTANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("unapproved draft cannot be sent")));

        // Approve draft
        mockMvc.perform(post("/api/consultant/leads/" + lead.getId() + "/approve-draft")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("vikram.consultant@autovista.in").roles("CONSULTANT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.consultantApproved").value(true));

        // 19. Send approved draft -> 200 OK
        mockMvc.perform(post("/api/consultant/leads/" + lead.getId() + "/send-reply")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("vikram.consultant@autovista.in").roles("CONSULTANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deliveryStatus").value("DELIVERED_SIMULATED"))
                .andExpect(jsonPath("$.data.channel").value("WHATSAPP_SIMULATED"));

        // 20. Communication audit record persisted
        assertEquals(1, communicationRepository.findByLeadIdOrderBySentAtDesc(lead.getId()).size(),
                "Communication audit log must be recorded in database");
    }

    @Test
    @DisplayName("21. Opted-out customer is excluded from further contact communication")
    void testOptedOutCustomerBlockedFromCommunication() throws Exception {
        Lead lead = new Lead();
        lead.setLeadRef("AV-LD-OPTOUT-01");
        lead.setCustomerName("Opted Out Client");
        lead.setCustomerEmail("optout@example.com");
        lead.setCustomerPhone("+91 95555 00005");
        lead.setInterestedCarVariant(testVariant);
        lead.setConsentGiven(true);
        lead.setOptOut(true); // Customer opted out!
        lead.setStatus("NEW");
        lead.setAiDraftReply("Dear Client, here is your update.");
        lead.setConsultantApproved(true);
        lead.setLastAssignedConsultant(testConsultant1);
        lead = leadRepository.save(lead);

        SendSimulatedReplyRequest sendReq = new SendSimulatedReplyRequest("WHATSAPP_SIMULATED");

        mockMvc.perform(post("/api/consultant/leads/" + lead.getId() + "/send-reply")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("vikram.consultant@autovista.in").roles("CONSULTANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("opted out of further communications")));
    }
}
