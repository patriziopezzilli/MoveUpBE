package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "badges")
public class Badge {
    
    @Id
    private String id;
    
    @NotNull
    @Size(min = 1, max = 100)
    private String title;
    
    @Size(max = 500)
    private String description;
    
    @NotNull
    private BadgeType type;
    
    @NotNull
    private BadgeCategory category;
    
    @NotNull
    private BadgeRarity rarity = BadgeRarity.COMMON;
    
    private String iconUrl;
    private String colorHex = "#1E88E5";
    private BadgeRequirements requirements = new BadgeRequirements();
    private BadgeRewards rewards = new BadgeRewards();
    
    @Indexed
    private boolean isActive = true;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // Constructors
    public Badge() {}
    
    public Badge(String title, String description, BadgeType type, BadgeCategory category) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BadgeType getType() { return type; }
    public void setType(BadgeType type) { this.type = type; }
    
    public BadgeCategory getCategory() { return category; }
    public void setCategory(BadgeCategory category) { this.category = category; }
    
    public BadgeRarity getRarity() { return rarity; }
    public void setRarity(BadgeRarity rarity) { this.rarity = rarity; }
    
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    
    public BadgeRequirements getRequirements() { return requirements; }
    public void setRequirements(BadgeRequirements requirements) { this.requirements = requirements; }
    
    public BadgeRewards getRewards() { return rewards; }
    public void setRewards(BadgeRewards rewards) { this.rewards = rewards; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Business methods
    public int getPointsValue() {
        return rarity.getPointsValue();
    }
    
    public boolean isEligibleForUser(Map<String, Object> userStats) {
        return requirements.checkEligibility(userStats);
    }
}

enum BadgeType {
    ACHIEVEMENT("Traguardo"),
    MILESTONE("Pietra Miliare"),
    SPECIAL("Speciale"),
    SEASONAL("Stagionale");
    
    private final String displayName;
    
    BadgeType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

enum BadgeCategory {
    LESSONS("Lezioni"),
    REVIEWS("Recensioni"),
    SOCIAL("Sociale"),
    CONSISTENCY("Costanza"),
    ACHIEVEMENT("Risultati"),
    SPECIAL("Speciale");
    
    private final String displayName;
    
    BadgeCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

enum BadgeRarity {
    COMMON("Comune", 10, "#9E9E9E"),
    UNCOMMON("Non Comune", 25, "#4CAF50"),
    RARE("Raro", 50, "#2196F3"),
    EPIC("Epico", 100, "#9C27B0"),
    LEGENDARY("Leggendario", 250, "#FF9800");
    
    private final String displayName;
    private final int pointsValue;
    private final String colorHex;
    
    BadgeRarity(String displayName, int pointsValue, String colorHex) {
        this.displayName = displayName;
        this.pointsValue = pointsValue;
        this.colorHex = colorHex;
    }
    
    public String getDisplayName() { return displayName; }
    public int getPointsValue() { return pointsValue; }
    public String getColorHex() { return colorHex; }
}

// Embedded classes
class BadgeRequirements {
    private int minLessons;
    private int minReviews;
    private double minRating;
    private int minConsecutiveDays;
    private String sportCategory;
    private Map<String, Object> customCriteria;
    
    // Constructors
    public BadgeRequirements() {}
    
    // Getters and Setters
    public int getMinLessons() { return minLessons; }
    public void setMinLessons(int minLessons) { this.minLessons = minLessons; }
    
    public int getMinReviews() { return minReviews; }
    public void setMinReviews(int minReviews) { this.minReviews = minReviews; }
    
    public double getMinRating() { return minRating; }
    public void setMinRating(double minRating) { this.minRating = minRating; }
    
    public int getMinConsecutiveDays() { return minConsecutiveDays; }
    public void setMinConsecutiveDays(int minConsecutiveDays) { this.minConsecutiveDays = minConsecutiveDays; }
    
    public String getSportCategory() { return sportCategory; }
    public void setSportCategory(String sportCategory) { this.sportCategory = sportCategory; }
    
    public Map<String, Object> getCustomCriteria() { return customCriteria; }
    public void setCustomCriteria(Map<String, Object> customCriteria) { this.customCriteria = customCriteria; }
    
    // Business method
    public boolean checkEligibility(Map<String, Object> userStats) {
        // Check lessons requirement
        if (minLessons > 0) {
            Integer userLessons = (Integer) userStats.get("totalLessons");
            if (userLessons == null || userLessons < minLessons) {
                return false;
            }
        }
        
        // Check reviews requirement
        if (minReviews > 0) {
            Integer userReviews = (Integer) userStats.get("totalReviews");
            if (userReviews == null || userReviews < minReviews) {
                return false;
            }
        }
        
        // Check rating requirement
        if (minRating > 0) {
            Double userRating = (Double) userStats.get("averageRating");
            if (userRating == null || userRating < minRating) {
                return false;
            }
        }
        
        // Check sport category requirement
        if (sportCategory != null && !sportCategory.isEmpty()) {
            String userSportCategory = (String) userStats.get("primarySportCategory");
            if (!sportCategory.equals(userSportCategory)) {
                return false;
            }
        }
        
        return true;
    }
}

public static class BadgeRewards {
    private int points;
    private int experiencePoints;
    private String unlockableFeature;
    private String specialTitle;
    
    // Constructors
    public BadgeRewards() {}
    
    // Getters and Setters
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }
    
    public String getUnlockableFeature() { return unlockableFeature; }
    public void setUnlockableFeature(String unlockableFeature) { this.unlockableFeature = unlockableFeature; }
    
    public String getSpecialTitle() { return specialTitle; }
    public void setSpecialTitle(String specialTitle) { this.specialTitle = specialTitle; }
}
