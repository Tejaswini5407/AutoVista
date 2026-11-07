package com.autovista.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lead_ref", nullable = false, unique = true, length = 50)
    private String leadRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showroom_id")
    private Showroom showroom;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "customer_email", nullable = false, length = 150)
    private String customerEmail;

    @Column(name = "customer_city", length = 50)
    private String customerCity = "Lucknow";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interested_car_variant_id")
    private CarVariant interestedCarVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interested_used_car_id")
    private UsedCar interestedUsedCar;

    @Column(name = "enquiry_type", nullable = false, length = 50)
    private String enquiryType = "GENERAL_ENQUIRY";

    @Column(name = "enquiry_message", columnDefinition = "TEXT")
    private String enquiryMessage;

    @Column(name = "stated_budget", length = 100)
    private String statedBudget;

    @Column(name = "purchase_timeline", length = 50)
    private String purchaseTimeline;

    @Column(name = "preferred_fuel", length = 50)
    private String preferredFuel;

    @Column(name = "preferred_transmission", length = 50)
    private String preferredTransmission;

    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven = true;

    @Column(name = "opt_out", nullable = false)
    private Boolean optOut = false;

    // Statuses: NEW, CONTACTED, FOLLOW_UP, TEST_DRIVE_SCHEDULED, CONVERTED, LOST
    @Column(nullable = false, length = 40)
    private String status = "NEW";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_assigned_consultant_id")
    private User lastAssignedConsultant;

    @Column(name = "assigned_at")
    private OffsetDateTime assignedAt;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "ai_draft_reply", columnDefinition = "TEXT")
    private String aiDraftReply;

    @Column(name = "consultant_approved", nullable = false)
    private Boolean consultantApproved = false;

    @Column(name = "sent_via", length = 50)
    private String sentVia;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LeadAssignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LeadStatusHistory> statusHistory = new ArrayList<>();

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LeadCommunication> communications = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Lead() {}

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLeadRef() { return leadRef; }
    public void setLeadRef(String leadRef) { this.leadRef = leadRef; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Showroom getShowroom() { return showroom; }
    public void setShowroom(Showroom showroom) { this.showroom = showroom; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerCity() { return customerCity; }
    public void setCustomerCity(String customerCity) { this.customerCity = customerCity; }

    public CarVariant getInterestedCarVariant() { return interestedCarVariant; }
    public void setInterestedCarVariant(CarVariant interestedCarVariant) { this.interestedCarVariant = interestedCarVariant; }

    public UsedCar getInterestedUsedCar() { return interestedUsedCar; }
    public void setInterestedUsedCar(UsedCar interestedUsedCar) { this.interestedUsedCar = interestedUsedCar; }

    public String getEnquiryType() { return enquiryType; }
    public void setEnquiryType(String enquiryType) { this.enquiryType = enquiryType; }

    public String getEnquiryMessage() { return enquiryMessage; }
    public void setEnquiryMessage(String enquiryMessage) { this.enquiryMessage = enquiryMessage; }

    public String getStatedBudget() { return statedBudget; }
    public void setStatedBudget(String statedBudget) { this.statedBudget = statedBudget; }

    public String getPurchaseTimeline() { return purchaseTimeline; }
    public void setPurchaseTimeline(String purchaseTimeline) { this.purchaseTimeline = purchaseTimeline; }

    public String getPreferredFuel() { return preferredFuel; }
    public void setPreferredFuel(String preferredFuel) { this.preferredFuel = preferredFuel; }

    public String getPreferredTransmission() { return preferredTransmission; }
    public void setPreferredTransmission(String preferredTransmission) { this.preferredTransmission = preferredTransmission; }

    public Boolean getConsentGiven() { return consentGiven; }
    public void setConsentGiven(Boolean consentGiven) { this.consentGiven = consentGiven; }

    public Boolean getOptOut() { return optOut; }
    public void setOptOut(Boolean optOut) { this.optOut = optOut; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getLastAssignedConsultant() { return lastAssignedConsultant; }
    public void setLastAssignedConsultant(User lastAssignedConsultant) { this.lastAssignedConsultant = lastAssignedConsultant; }

    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(OffsetDateTime assignedAt) { this.assignedAt = assignedAt; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getAiDraftReply() { return aiDraftReply; }
    public void setAiDraftReply(String aiDraftReply) { this.aiDraftReply = aiDraftReply; }

    public Boolean getConsultantApproved() { return consultantApproved; }
    public void setConsultantApproved(Boolean consultantApproved) { this.consultantApproved = consultantApproved; }

    public String getSentVia() { return sentVia; }
    public void setSentVia(String sentVia) { this.sentVia = sentVia; }

    public List<LeadAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<LeadAssignment> assignments) { this.assignments = assignments; }

    public List<LeadStatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<LeadStatusHistory> statusHistory) { this.statusHistory = statusHistory; }

    public List<LeadCommunication> getCommunications() { return communications; }
    public void setCommunications(List<LeadCommunication> communications) { this.communications = communications; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
