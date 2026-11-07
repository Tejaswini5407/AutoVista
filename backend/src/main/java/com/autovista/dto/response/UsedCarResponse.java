package com.autovista.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class UsedCarResponse {

    private Long id;
    private Long showroomId;
    private String showroomName;
    private String showroomCity;
    private String brand;
    private String modelName;
    private String variantName;
    private Integer manufacturingYear;
    private BigDecimal price;
    private Integer odometerKm;

    // MANDATORY REQUIREMENT #7:
    // Masked registration number (e.g., "UP32 •• ••••"). Raw registration is NEVER exposed in the API.
    private String maskedRegistrationNumber;

    private String fuelType;
    private String transmission;
    private Integer ownersCount;
    private String insuranceValidity;
    private Integer inspectionPointsPassed;
    private String status;
    private String heroImageUrl;
    private List<String> features;
    private String tagline;

    public UsedCarResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShowroomId() { return showroomId; }
    public void setShowroomId(Long showroomId) { this.showroomId = showroomId; }

    public String getShowroomName() { return showroomName; }
    public void setShowroomName(String showroomName) { this.showroomName = showroomName; }

    public String getShowroomCity() { return showroomCity; }
    public void setShowroomCity(String showroomCity) { this.showroomCity = showroomCity; }

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

    public String getMaskedRegistrationNumber() { return maskedRegistrationNumber; }
    public void setMaskedRegistrationNumber(String maskedRegistrationNumber) { this.maskedRegistrationNumber = maskedRegistrationNumber; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHeroImageUrl() { return heroImageUrl; }
    public void setHeroImageUrl(String heroImageUrl) { this.heroImageUrl = heroImageUrl; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
}
