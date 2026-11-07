package com.autovista.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "used_cars")
public class UsedCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showroom_id", nullable = false)
    private Showroom showroom;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "variant_name", nullable = false, length = 100)
    private String variantName;

    @Column(name = "manufacturing_year", nullable = false)
    private Integer manufacturingYear;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "odometer_km", nullable = false)
    private Integer odometerKm;

    @Column(name = "raw_registration_number", nullable = false, length = 50)
    private String rawRegistrationNumber;

    @Column(name = "fuel_type", nullable = false, length = 30)
    private String fuelType;

    @Column(nullable = false, length = 30)
    private String transmission;

    @Column(name = "owners_count", nullable = false)
    private Integer ownersCount = 1;

    @Column(name = "insurance_validity", length = 100)
    private String insuranceValidity;

    @Column(name = "inspection_points_passed", nullable = false)
    private Integer inspectionPointsPassed = 150;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UsedCarStatus status = UsedCarStatus.AVAILABLE;

    @Column(name = "hero_image_url", columnDefinition = "TEXT")
    private String heroImageUrl;

    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(length = 255)
    private String tagline;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public UsedCar() {}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Showroom getShowroom() { return showroom; }
    public void setShowroom(Showroom showroom) { this.showroom = showroom; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public Integer getManufacturingYear() { return manufacturingYear; }
    public void setManufacturingYear(Integer manufacturingYear) { this.manufacturingYear = manufacturingYear; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getOdometerKm() { return odometerKm; }
    public void setOdometerKm(Integer odometerKm) { this.odometerKm = odometerKm; }

    public String getRawRegistrationNumber() { return rawRegistrationNumber; }
    public void setRawRegistrationNumber(String rawRegistrationNumber) { this.rawRegistrationNumber = rawRegistrationNumber; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public Integer getOwnersCount() { return ownersCount; }
    public void setOwnersCount(Integer ownersCount) { this.ownersCount = ownersCount; }

    public String getInsuranceValidity() { return insuranceValidity; }
    public void setInsuranceValidity(String insuranceValidity) { this.insuranceValidity = insuranceValidity; }

    public Integer getInspectionPointsPassed() { return inspectionPointsPassed; }
    public void setInspectionPointsPassed(Integer inspectionPointsPassed) { this.inspectionPointsPassed = inspectionPointsPassed; }

    public UsedCarStatus getStatus() { return status; }
    public void setStatus(UsedCarStatus status) { this.status = status; }

    public String getHeroImageUrl() { return heroImageUrl; }
    public void setHeroImageUrl(String heroImageUrl) { this.heroImageUrl = heroImageUrl; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
