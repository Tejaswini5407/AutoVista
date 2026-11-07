package com.autovista.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class CarVariantResponse {

    private Long id;
    private Long carModelId;
    private String brand;
    private String modelName;
    private String variantName;
    private Integer year;
    private String bodyType;
    private String fuelType;
    private String transmission;
    private String engineSpecs;
    private String powerSpecs;
    private String torqueSpecs;
    private Integer seatingCapacity;
    private BigDecimal exShowroomPrice;
    private String tagline;
    private List<String> features;
    private List<String> colors;
    private String heroImageUrl;
    private Boolean isAvailable;

    public CarVariantResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCarModelId() { return carModelId; }
    public void setCarModelId(Long carModelId) { this.carModelId = carModelId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }

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

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }

    public String getHeroImageUrl() { return heroImageUrl; }
    public void setHeroImageUrl(String heroImageUrl) { this.heroImageUrl = heroImageUrl; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}
