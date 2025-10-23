package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "lessons")
public class Lesson {
    
    @Id
    private String id;
    
    @NotNull
    @Indexed
    private String instructorId;
    
    @NotNull
    @Indexed
    private String sportId;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String description;
    
    @Min(0)
    private double price;
    
    @Min(1)
    private int duration; // in minutes
    
    @NotNull
    private Location location;
    
    private int maxParticipants = 1; // MVP focuses on individual lessons
    
    @NotNull
    private SkillLevel skillLevel;
    
    private String type;
    
    private List<String> equipment = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private boolean isActive = true;
    private List<String> images = new ArrayList<>();
    private String requirements;
    private String cancellationPolicy;
    
    // Statistics fields
    private int bookingCount = 0;
    private int viewCount = 0;
    private double averageRating = 0.0;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public Lesson() {}
    
    public Lesson(String instructorId, String sportId, String title, String description, 
                  double price, int duration, Location location, SkillLevel skillLevel) {
        this.instructorId = instructorId;
        this.sportId = sportId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.location = location;
        this.skillLevel = skillLevel;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    
    public String getSportId() { return sportId; }
    public void setSportId(String sportId) { this.sportId = sportId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    
    public SkillLevel getSkillLevel() { return skillLevel; }
    public void setSkillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; }
    
    public int getLevel() { return skillLevel != null ? skillLevel.ordinal() + 1 : 0; }
    public void setLevel(int level) { 
        if (level >= 1 && level <= SkillLevel.values().length) {
            this.skillLevel = SkillLevel.values()[level - 1];
        }
    }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<String> getEquipment() { return equipment; }
    public void setEquipment(List<String> equipment) { this.equipment = equipment; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    
    public String getCancellationPolicy() { return cancellationPolicy; }
    public void setCancellationPolicy(String cancellationPolicy) { this.cancellationPolicy = cancellationPolicy; }
    
    public int getBookingCount() { return bookingCount; }
    public void setBookingCount(int bookingCount) { this.bookingCount = bookingCount; }
    
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public void addEquipment(String item) {
        if (!equipment.contains(item)) {
            equipment.add(item);
        }
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addImage(String imageBase64) {
        images.add(imageBase64);
    }
    
    public String getDurationFormatted() {
        if (duration < 60) {
            return duration + "min";
        } else {
            int hours = duration / 60;
            int minutes = duration % 60;
            if (minutes == 0) {
                return hours + "h";
            } else {
                return hours + "h " + minutes + "min";
            }
        }
    }
    
    // Business methods for statistics
    public void incrementBookingCount() {
        this.bookingCount++;
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
}