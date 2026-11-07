package com.autovista.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CarModelRequest {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model name is required")
    private String modelName;

    @NotBlank(message = "Body type is required")
    private String bodyType;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be valid")
    private Integer year;

    private String description;

    private String heroImageUrl;

    private Boolean isActive = true;

    public CarModelRequest() {}

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHeroImageUrl() { return heroImageUrl; }
    public void setHeroImageUrl(String heroImageUrl) { this.heroImageUrl = heroImageUrl; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
