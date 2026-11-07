package com.autovista.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_variants")
public class CarVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

    @Column(name = "variant_name", nullable = false, length = 100)
    private String variantName;

    @Column(name = "fuel_type", nullable = false, length = 30)
    private String fuelType;

    @Column(nullable = false, length = 30)
    private String transmission;

    @Column(name = "engine_specs", length = 150)
    private String engineSpecs;

    @Column(name = "power_specs", length = 100)
    private String powerSpecs;

    @Column(name = "torque_specs", length = 100)
    private String torqueSpecs;

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity = 5;

    @Column(name = "ex_showroom_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal exShowroomPrice;

    @Column(length = 255)
    private String tagline;

    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(columnDefinition = "TEXT")
    private String colors;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @OneToMany(mappedBy = "carVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CityPrice> cityPrices = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public CarVariant() {}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CarModel getCarModel() { return carModel; }
    public void setCarModel(CarModel carModel) { this.carModel = carModel; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getEngineSpecs() { return engineSpecs; }
    public void setEngineSpecs(String engineSpecs) { this.engineSpecs = engineSpecs; }

    public String getPowerSpecs() { return powerSpecs; }
    public void setPowerSpecs(String powerSpecs) { this.powerSpecs = powerSpecs; }

    public String getTorqueSpecs() { return torqueSpecs; }
    public void setTorqueSpecs(String torqueSpecs) { this.torqueSpecs = torqueSpecs; }

    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    public BigDecimal getExShowroomPrice() { return exShowroomPrice; }
    public void setExShowroomPrice(BigDecimal exShowroomPrice) { this.exShowroomPrice = exShowroomPrice; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public List<CityPrice> getCityPrices() { return cityPrices; }
    public void setCityPrices(List<CityPrice> cityPrices) { this.cityPrices = cityPrices; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
