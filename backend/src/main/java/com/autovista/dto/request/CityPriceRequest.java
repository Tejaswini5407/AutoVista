package com.autovista.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CityPriceRequest {

    @NotNull(message = "Car variant ID is required")
    private Long carVariantId;

    @NotBlank(message = "City is required")
    private String city;

    private String state = "Uttar Pradesh";

    @NotNull(message = "RTO tax percentage is required")
    @DecimalMin(value = "0.0", message = "Tax percent cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax percent cannot exceed 100")
    private BigDecimal rtoTaxPercent;

    @NotNull(message = "Insurance amount is required")
    @DecimalMin(value = "0.0", message = "Insurance cannot be negative")
    private BigDecimal insuranceAmount;

    @DecimalMin(value = "0.0", message = "Accessories cannot be negative")
    private BigDecimal accessoriesAmount = new BigDecimal("25000.00");

    @DecimalMin(value = "0.0", message = "Handling charges cannot be negative")
    private BigDecimal handlingCharges = BigDecimal.ZERO;

    @NotNull(message = "Price validity date is required")
    private LocalDate priceValidUntil;

    private String disclaimer = "Ex-showroom price is indicative. Mandatory charges include RTO registration, road tax, and insurance. TCS (1%) applies if ex-showroom exceeds ₹10,00,000 as per Section 206C(1F) of IT Act. Prices are subject to change without prior notice.";

    public CityPriceRequest() {}

    public Long getCarVariantId() { return carVariantId; }
    public void setCarVariantId(Long carVariantId) { this.carVariantId = carVariantId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public BigDecimal getRtoTaxPercent() { return rtoTaxPercent; }
    public void setRtoTaxPercent(BigDecimal rtoTaxPercent) { this.rtoTaxPercent = rtoTaxPercent; }

    public BigDecimal getInsuranceAmount() { return insuranceAmount; }
    public void setInsuranceAmount(BigDecimal insuranceAmount) { this.insuranceAmount = insuranceAmount; }

    public BigDecimal getAccessoriesAmount() { return accessoriesAmount; }
    public void setAccessoriesAmount(BigDecimal accessoriesAmount) { this.accessoriesAmount = accessoriesAmount; }

    public BigDecimal getHandlingCharges() { return handlingCharges; }
    public void setHandlingCharges(BigDecimal handlingCharges) { this.handlingCharges = handlingCharges; }

    public LocalDate getPriceValidUntil() { return priceValidUntil; }
    public void setPriceValidUntil(LocalDate priceValidUntil) { this.priceValidUntil = priceValidUntil; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
}
