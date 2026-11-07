package com.autovista.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
    name = "test_drive_bookings",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_bookings_ref", columnNames = {"booking_ref"})
    }
)
public class TestDriveBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_ref", nullable = false, unique = true, length = 50)
    private String bookingRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showroom_id", nullable = false)
    private Showroom showroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_variant_id")
    private CarVariant carVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_car_id")
    private UsedCar usedCar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "customer_email", nullable = false, length = 150)
    private String customerEmail;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "time_slot", nullable = false, length = 50)
    private String timeSlot;

    @Column(name = "drive_mode", nullable = false, length = 30)
    private String driveMode = "SHOWROOM";

    @Column(name = "doorstep_address", columnDefinition = "TEXT")
    private String doorstepAddress;

    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven = true;

    @Column(nullable = false, length = 30)
    private String status = "CONFIRMED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public TestDriveBooking() {}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }

    public Showroom getShowroom() { return showroom; }
    public void setShowroom(Showroom showroom) { this.showroom = showroom; }

    public CarVariant getCarVariant() { return carVariant; }
    public void setCarVariant(CarVariant carVariant) { this.carVariant = carVariant; }

    public UsedCar getUsedCar() { return usedCar; }
    public void setUsedCar(UsedCar usedCar) { this.usedCar = usedCar; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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
}
