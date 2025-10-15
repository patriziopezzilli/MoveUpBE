package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "sports")
public class Sport {
    
    @Id
    private String id;
    
    @NotBlank
    @Indexed(unique = true)
    private String name;
    
    @NotNull
    private SportCategory category;
    
    @NotBlank
    private String iconName;
    
    private String iconUrl;
    private String difficultyLevel;  // "EASY", "MEDIUM", "HARD"
    private List<String> characteristics = new ArrayList<>();
    private List<String> equipment = new ArrayList<>();
    private int popularity = 0;
    private boolean isActive = true;
    private boolean isPopular = false;
    private String description;
    private List<String> equipmentNeeded = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // Constructors
    public Sport() {}
    
    public Sport(String name, SportCategory category, String iconName) {
        this.name = name;
        this.category = category;
        this.iconName = iconName;
    }
    
    public Sport(String name, SportCategory category, String iconName, String description) {
        this.name = name;
        this.category = category;
        this.iconName = iconName;
        this.description = description;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public SportCategory getCategory() { return category; }
    public void setCategory(SportCategory category) { this.category = category; }
    
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    
    public List<String> getCharacteristics() { return characteristics; }
    public void setCharacteristics(List<String> characteristics) { this.characteristics = characteristics; }
    
    public List<String> getEquipment() { return equipment; }
    public void setEquipment(List<String> equipment) { this.equipment = equipment; }
    
    public int getPopularity() { return popularity; }
    public void setPopularity(int popularity) { this.popularity = popularity; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public boolean isPopular() { return isPopular; }
    public void setPopular(boolean popular) { isPopular = popular; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getEquipmentNeeded() { return equipmentNeeded; }
    public void setEquipmentNeeded(List<String> equipmentNeeded) { this.equipmentNeeded = equipmentNeeded; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
        // Business methods
    public void addEquipment(String equipment) {
        if (!equipmentNeeded.contains(equipment)) {
            equipmentNeeded.add(equipment);
        }
    }
    
    public void incrementPopularity() {
        this.popularity++;
    }
}
