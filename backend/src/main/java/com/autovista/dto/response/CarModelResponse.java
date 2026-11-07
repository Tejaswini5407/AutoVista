package com.autovista.dto.response;

import java.util.List;

public class CarModelResponse {

    private Long id;
    private String brand;
    private String modelName;
    private String bodyType;
    private Integer year;
    private String description;
    private String heroImageUrl;
    private Boolean isActive;
    private List<CarVariantResponse> variants;

    public CarModelResponse() {}

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public List<CarVariantResponse> getVariants() { return variants; }
    public void setVariants(List<CarVariantResponse> variants) { this.variants = variants; }
}
