package com.autovista.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CarVariantRequest {

    private Long carModelId;

    @NotBlank(message = "Variant name is required")
    private String variantName;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    @NotBlank(message = "Transmission is required")
    private String transmission;

    private String engineSpecs;

    private String powerSpecs;

    private String torqueSpecs;

    private Integer seatingCapacity = 5;

    @NotNull(message = "Ex-showroom price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal exShowroomPrice;

    private String tagline;

    private String features;

    private String colors;

    private Boolean isAvailable = true;

    public CarVariantRequest() {}

    public Long getCarModelId() { return carModelId; }
    public void setCarModelId(Long carModelId) { this.carModelId = carModelId; }

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
}
