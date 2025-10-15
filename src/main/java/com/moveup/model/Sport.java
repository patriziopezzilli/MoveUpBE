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
}

enum SportCategory {
    RACQUET("Racchette"),
    TEAM("Sport di squadra"), 
    WATER("Sport acquatici"),
    FITNESS("Fitness"),
    WELLNESS("Benessere"),
    RUNNING("Corsa"),
    COMBAT("Arti marziali"),
    WINTER("Sport invernali"),
    OTHER("Altri");
    
    private final String displayName;
    
    SportCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}