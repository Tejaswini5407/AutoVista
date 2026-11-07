package com.autovista.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateBookingRequest {

    @NotNull(message = "Showroom ID is required")
    private Long showroomId;

    private Long carVariantId;

    private Long usedCarId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer phone number is required")
    private String customerPhone;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date cannot be in the past")
    private LocalDate bookingDate;

    @NotBlank(message = "Time slot is required")
    private String timeSlot;

    private String driveMode = "SHOWROOM";

    private String doorstepAddress;

    @NotNull(message = "Consent is required")
    @AssertTrue(message = "Customer consent is mandatory for appointment booking")
    private Boolean consentGiven = true;

    public CreateBookingRequest() {}

    public Long getShowroomId() { return showroomId; }
    public void setShowroomId(Long showroomId) { this.showroomId = showroomId; }

    public Long getCarVariantId() { return carVariantId; }
    public void setCarVariantId(Long carVariantId) { this.carVariantId = carVariantId; }

    public Long getUsedCarId() { return usedCarId; }
    public void setUsedCarId(Long usedCarId) { this.usedCarId = usedCarId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getDriveMode() { return driveMode; }
    public void setDriveMode(String driveMode) { this.driveMode = driveMode; }

    public String getDoorstepAddress() { return doorstepAddress; }
    public void setDoorstepAddress(String doorstepAddress) { this.doorstepAddress = doorstepAddress; }

    public Boolean getConsentGiven() { return consentGiven; }
    public void setConsentGiven(Boolean consentGiven) { this.consentGiven = consentGiven; }
}
