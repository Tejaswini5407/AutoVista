package com.autovista.dto.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class BookingResponse {

    private Long id;
    private String bookingRef;
    private Long showroomId;
    private String showroomName;
    private String showroomCity;
    private String showroomAddress;

    private String vehicleType; // "NEW" or "USED"
    private Long carVariantId;
    private Long usedCarId;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleVariant;
    private Integer vehicleYear;
    private String vehicleDisplayName;

    private String customerName;
    private String customerPhone;
    private String customerEmail;

    private LocalDate bookingDate;
    private String timeSlot;
    private String driveMode;
    private String doorstepAddress;
    private Boolean consentGiven;
    private String status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<MockNotificationResponse> notifications;

    public BookingResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }

    public Long getShowroomId() { return showroomId; }
    public void setShowroomId(Long showroomId) { this.showroomId = showroomId; }

    public String getShowroomName() { return showroomName; }
    public void setShowroomName(String showroomName) { this.showroomName = showroomName; }

    public String getShowroomCity() { return showroomCity; }
    public void setShowroomCity(String showroomCity) { this.showroomCity = showroomCity; }

    public String getShowroomAddress() { return showroomAddress; }
    public void setShowroomAddress(String showroomAddress) { this.showroomAddress = showroomAddress; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Long getCarVariantId() { return carVariantId; }
    public void setCarVariantId(Long carVariantId) { this.carVariantId = carVariantId; }

    public Long getUsedCarId() { return usedCarId; }
    public void setUsedCarId(Long usedCarId) { this.usedCarId = usedCarId; }

    public String getVehicleBrand() { return vehicleBrand; }
    public void setVehicleBrand(String vehicleBrand) { this.vehicleBrand = vehicleBrand; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleVariant() { return vehicleVariant; }
    public void setVehicleVariant(String vehicleVariant) { this.vehicleVariant = vehicleVariant; }

    public Integer getVehicleYear() { return vehicleYear; }
    public void setVehicleYear(Integer vehicleYear) { this.vehicleYear = vehicleYear; }

    public String getVehicleDisplayName() { return vehicleDisplayName; }
    public void setVehicleDisplayName(String vehicleDisplayName) { this.vehicleDisplayName = vehicleDisplayName; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<MockNotificationResponse> getNotifications() { return notifications; }
    public void setNotifications(List<MockNotificationResponse> notifications) { this.notifications = notifications; }
}
