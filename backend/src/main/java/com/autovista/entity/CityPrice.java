package com.autovista.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "city_prices")
public class CityPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_variant_id", nullable = false)
    private CarVariant carVariant;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String state = "Uttar Pradesh";

    @Column(name = "rto_tax_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal rtoTaxPercent;

    @Column(name = "rto_tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal rtoTaxAmount;

    @Column(name = "insurance_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal insuranceAmount;

    @Column(name = "accessories_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal accessoriesAmount = new BigDecimal("25000.00");

    @Column(name = "handling_charges", nullable = false, precision = 12, scale = 2)
    private BigDecimal handlingCharges = BigDecimal.ZERO;

    @Column(name = "tcs_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal tcsAmount = BigDecimal.ZERO;

    @Column(name = "total_on_road_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalOnRoadPrice;

    @Column(name = "price_valid_until", nullable = false)
    private LocalDate priceValidUntil;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String disclaimer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public CityPrice() {}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CarVariant getCarVariant() { return carVariant; }
    public void setCarVariant(CarVariant carVariant) { this.carVariant = carVariant; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public BigDecimal getRtoTaxPercent() { return rtoTaxPercent; }
    public void setRtoTaxPercent(BigDecimal rtoTaxPercent) { this.rtoTaxPercent = rtoTaxPercent; }

    public BigDecimal getRtoTaxAmount() { return rtoTaxAmount; }
    public void setRtoTaxAmount(BigDecimal rtoTaxAmount) { this.rtoTaxAmount = rtoTaxAmount; }

    public BigDecimal getInsuranceAmount() { return insuranceAmount; }
    public void setInsuranceAmount(BigDecimal insuranceAmount) { this.insuranceAmount = insuranceAmount; }

    public BigDecimal getAccessoriesAmount() { return accessoriesAmount; }
    public void setAccessoriesAmount(BigDecimal accessoriesAmount) { this.accessoriesAmount = accessoriesAmount; }

    public BigDecimal getHandlingCharges() { return handlingCharges; }
    public void setHandlingCharges(BigDecimal handlingCharges) { this.handlingCharges = handlingCharges; }

    public BigDecimal getTcsAmount() { return tcsAmount; }
    public void setTcsAmount(BigDecimal tcsAmount) { this.tcsAmount = tcsAmount; }

    public BigDecimal getTotalOnRoadPrice() { return totalOnRoadPrice; }
    public void setTotalOnRoadPrice(BigDecimal totalOnRoadPrice) { this.totalOnRoadPrice = totalOnRoadPrice; }

    public LocalDate getPriceValidUntil() { return priceValidUntil; }
    public void setPriceValidUntil(LocalDate priceValidUntil) { this.priceValidUntil = priceValidUntil; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
