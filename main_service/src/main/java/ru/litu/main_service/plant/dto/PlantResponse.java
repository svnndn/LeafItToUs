package ru.litu.main_service.plant.dto;

import lombok.*;
import ru.litu.main_service.plant.model.CareLevel;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlantResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String type;
    private CareLevel careLevel;
    private String wateringFrequency;
    private String lightingNeeds;
    private String fertilizingFrequency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CareLevel getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(CareLevel careLevel) {
        this.careLevel = careLevel;
    }

    public String getWateringFrequency() {
        return wateringFrequency;
    }

    public void setWateringFrequency(String wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public String getLightingNeeds() {
        return lightingNeeds;
    }

    public void setLightingNeeds(String lightingNeeds) {
        this.lightingNeeds = lightingNeeds;
    }

    public String getFertilizingFrequency() {
        return fertilizingFrequency;
    }

    public void setFertilizingFrequency(String fertilizingFrequency) {
        this.fertilizingFrequency = fertilizingFrequency;
    }
}