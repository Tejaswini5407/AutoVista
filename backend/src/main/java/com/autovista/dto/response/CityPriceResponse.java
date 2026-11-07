package com.autovista.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CityPriceResponse {

    private Long id;
    private Long carVariantId;
    private String variantName;
    private String city;
    private String state;
    private BigDecimal exShowroomPrice;
    private BigDecimal rtoTaxPercent;
    private BigDecimal rtoTaxAmount;
    private BigDecimal insuranceAmount;
    private BigDecimal accessoriesAmount;
    private BigDecimal handlingCharges;
    private BigDecimal tcsAmount;
    private BigDecimal totalOnRoadPrice;
    private LocalDate priceValidUntil;
    private String disclaimer;

    public CityPriceResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCarVariantId() { return carVariantId; }
    public void setCarVariantId(Long carVariantId) { this.carVariantId = carVariantId; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public BigDecimal getExShowroomPrice() { return exShowroomPrice; }
    public void setExShowroomPrice(BigDecimal exShowroomPrice) { this.exShowroomPrice = exShowroomPrice; }

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
}
