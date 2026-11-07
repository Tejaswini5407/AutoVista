package com.autovista.dto.request;

import com.autovista.entity.UsedCarStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UsedCarRequest {

    private Long showroomId;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model name is required")
    private String modelName;

    @NotBlank(message = "Variant name is required")
    private String variantName;

    @NotNull(message = "Manufacturing year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    private Integer manufacturingYear;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1000.00", message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Odometer reading is required")
    @Min(value = 0, message = "Odometer reading cannot be negative")
    private Integer odometerKm;

    @NotBlank(message = "Registration number is required")
    private String rawRegistrationNumber;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    @NotBlank(message = "Transmission is required")
    private String transmission;

    @Min(value = 1, message = "Owner count must be at least 1")
    private Integer ownersCount = 1;

    private String insuranceValidity;

    private Integer inspectionPointsPassed = 150;

    private UsedCarStatus status = UsedCarStatus.AVAILABLE;

    private String heroImageUrl;

    private String features;

    private String tagline;

    public UsedCarRequest() {}

    public Long getShowroomId() { return showroomId; }
    public void setShowroomId(Long showroomId) { this.showroomId = showroomId; }

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
}
