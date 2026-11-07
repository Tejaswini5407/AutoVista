package com.autovista.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateLeadRequest {

    private Long carVariantId;
    private Long usedCarId;
    private Long showroomId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    private String customerEmail;

    private String customerCity;
    private String enquiryType = "GENERAL_ENQUIRY";
    private String enquiryMessage;
    private String statedBudget;
    private String purchaseTimeline;
    private String preferredFuel;
    private String preferredTransmission;

    @NotNull(message = "Explicit consent is required before submitting an enquiry")
    private Boolean consentGiven;

    private Boolean optOut = false;

    public CreateLeadRequest() {}

    public Long getCarVariantId() { return carVariantId; }
    public void setCarVariantId(Long carVariantId) { this.carVariantId = carVariantId; }

    public Long getUsedCarId() { return usedCarId; }
    public void setUsedCarId(Long usedCarId) { this.usedCarId = usedCarId; }

    public Long getShowroomId() { return showroomId; }
    public void setShowroomId(Long showroomId) { this.showroomId = showroomId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerCity() { return customerCity; }
    public void setCustomerCity(String customerCity) { this.customerCity = customerCity; }

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
}
