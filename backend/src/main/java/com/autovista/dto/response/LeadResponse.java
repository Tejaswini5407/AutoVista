package com.autovista.dto.response;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class LeadResponse {

    private Long id;
    private String leadRef;
    private Long userId;

    private Long showroomId;
    private String showroomName;
    private String showroomCity;

    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerCity;

    private Long carVariantId;
    private String carModelName;
    private String carVariantName;
    private String vehiclePriceFormatted;

    private Long usedCarId;
    private String usedCarTitle;
    private String usedCarPriceFormatted;

    private String enquiryType;
    private String enquiryMessage;
    private String statedBudget;
    private String purchaseTimeline;
    private String preferredFuel;
    private String preferredTransmission;

    private Boolean consentGiven;
    private Boolean optOut;
    private String status;

    private Long assignedConsultantId;
    private String assignedConsultantName;
    private String assignedConsultantEmail;
    private OffsetDateTime assignedAt;
    private OffsetDateTime responseDueBy;
    private Boolean slaMet;

    private String aiSummary;
    private String aiDraftReply;
    private Boolean consultantApproved;
    private String sentVia;

    private List<LeadStatusHistoryResponse> statusHistory = new ArrayList<>();
    private List<LeadCommunicationResponse> communications = new ArrayList<>();

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public LeadResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLeadRef() { return leadRef; }
    public void setLeadRef(String leadRef) { this.leadRef = leadRef; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getShowroomId() { return showroomId; }
    public void setShowroomId(Long showroomId) { this.showroomId = showroomId; }

    public String getShowroomName() { return showroomName; }
    public void setShowroomName(String showroomName) { this.showroomName = showroomName; }

    public String getShowroomCity() { return showroomCity; }
    public void setShowroomCity(String showroomCity) { this.showroomCity = showroomCity; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerCity() { return customerCity; }
    public void setCustomerCity(String customerCity) { this.customerCity = customerCity; }

    public Long getCarVariantId() { return carVariantId; }
    public void setCarVariantId(Long carVariantId) { this.carVariantId = carVariantId; }

    public String getCarModelName() { return carModelName; }
    public void setCarModelName(String carModelName) { this.carModelName = carModelName; }

    public String getCarVariantName() { return carVariantName; }
    public void setCarVariantName(String carVariantName) { this.carVariantName = carVariantName; }

    public String getVehiclePriceFormatted() { return vehiclePriceFormatted; }
    public void setVehiclePriceFormatted(String vehiclePriceFormatted) { this.vehiclePriceFormatted = vehiclePriceFormatted; }

    public Long getUsedCarId() { return usedCarId; }
    public void setUsedCarId(Long usedCarId) { this.usedCarId = usedCarId; }

    public String getUsedCarTitle() { return usedCarTitle; }
    public void setUsedCarTitle(String usedCarTitle) { this.usedCarTitle = usedCarTitle; }

    public String getUsedCarPriceFormatted() { return usedCarPriceFormatted; }
    public void setUsedCarPriceFormatted(String usedCarPriceFormatted) { this.usedCarPriceFormatted = usedCarPriceFormatted; }

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

    public Long getAssignedConsultantId() { return assignedConsultantId; }
    public void setAssignedConsultantId(Long assignedConsultantId) { this.assignedConsultantId = assignedConsultantId; }

    public String getAssignedConsultantName() { return assignedConsultantName; }
    public void setAssignedConsultantName(String assignedConsultantName) { this.assignedConsultantName = assignedConsultantName; }

    public String getAssignedConsultantEmail() { return assignedConsultantEmail; }
    public void setAssignedConsultantEmail(String assignedConsultantEmail) { this.assignedConsultantEmail = assignedConsultantEmail; }

    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(OffsetDateTime assignedAt) { this.assignedAt = assignedAt; }

    public OffsetDateTime getResponseDueBy() { return responseDueBy; }
    public void setResponseDueBy(OffsetDateTime responseDueBy) { this.responseDueBy = responseDueBy; }

    public Boolean getSlaMet() { return slaMet; }
    public void setSlaMet(Boolean slaMet) { this.slaMet = slaMet; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getAiDraftReply() { return aiDraftReply; }
    public void setAiDraftReply(String aiDraftReply) { this.aiDraftReply = aiDraftReply; }

    public Boolean getConsultantApproved() { return consultantApproved; }
    public void setConsultantApproved(Boolean consultantApproved) { this.consultantApproved = consultantApproved; }

    public String getSentVia() { return sentVia; }
    public void setSentVia(String sentVia) { this.sentVia = sentVia; }

    public List<LeadStatusHistoryResponse> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<LeadStatusHistoryResponse> statusHistory) { this.statusHistory = statusHistory; }

    public List<LeadCommunicationResponse> getCommunications() { return communications; }
    public void setCommunications(List<LeadCommunicationResponse> communications) { this.communications = communications; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
