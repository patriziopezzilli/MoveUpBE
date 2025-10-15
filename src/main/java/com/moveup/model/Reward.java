package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Document(collection = "rewards")
public class Reward {
    
    @Id
    private String id;
    
    @NotNull
    @Size(min = 1, max = 100)
    private String title;
    
    @Size(max = 500)
    private String description;
    
    @NotNull
    private RewardType type;
    
    @NotNull
    @Min(1)
    private int pointsCost;
    
    @Min(0)
    private int quantity = -1; // -1 = unlimited
    
    @Min(0)
    private int remainingQuantity = -1; // -1 = unlimited
    
    private String imageUrl;
    private RewardValue value = new RewardValue();
    private RewardTerms terms = new RewardTerms();
    
    @Indexed
    private boolean isActive = true;
    
    @Indexed
    private boolean isFeatured = false;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public Reward() {}
    
    public Reward(String title, String description, RewardType type, int pointsCost) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.pointsCost = pointsCost;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public RewardType getType() { return type; }
    public void setType(RewardType type) { this.type = type; }
    
    public int getPointsCost() { return pointsCost; }
    public void setPointsCost(int pointsCost) { this.pointsCost = pointsCost; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(int remainingQuantity) { this.remainingQuantity = remainingQuantity; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public RewardValue getValue() { return value; }
    public void setValue(RewardValue value) { this.value = value; }
    
    public RewardTerms getTerms() { return terms; }
    public void setTerms(RewardTerms terms) { this.terms = terms; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public boolean isAvailable() {
        return isActive && (remainingQuantity > 0 || remainingQuantity == -1);
    }
    
    public boolean canBeRedeemed(int userPoints) {
        return isAvailable() && userPoints >= pointsCost;
    }
    
    public void redeem() {
        if (remainingQuantity > 0) {
            remainingQuantity--;
        }
    }
    
    public double getDiscountPercentage() {
        return type == RewardType.DISCOUNT ? value.getDiscountPercentage() : 0;
    }
    
    public double getCashValue() {
        return type == RewardType.CASH_REWARD ? value.getCashAmount() : 0;
    }
}

enum RewardType {
    DISCOUNT("Sconto"),
    FREE_LESSON("Lezione Gratuita"),
    CASH_REWARD("Ricompensa in Denaro"),
    PREMIUM_FEATURE("Funzione Premium"),
    MERCHANDISE("Merchandising"),
    EXPERIENCE("Esperienza"),
    SPECIAL_ACCESS("Accesso Speciale");
    
    private final String displayName;
    
    RewardType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

// Embedded classes
class RewardValue {
    private double discountPercentage;
    private double cashAmount;
    private String couponCode;
    private String featureAccess;
    private String experienceDetails;
    
    // Constructors
    public RewardValue() {}
    
    // Getters and Setters
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    
    public double getCashAmount() { return cashAmount; }
    public void setCashAmount(double cashAmount) { this.cashAmount = cashAmount; }
    
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    
    public String getFeatureAccess() { return featureAccess; }
    public void setFeatureAccess(String featureAccess) { this.featureAccess = featureAccess; }
    
    public String getExperienceDetails() { return experienceDetails; }
    public void setExperienceDetails(String experienceDetails) { this.experienceDetails = experienceDetails; }
}

class RewardTerms {
    private LocalDateTime expirationDate;
    private int maxUsesPerUser = 1;
    private String applicableToSports;
    private double minOrderAmount;
    private String specialConditions;
    
    // Constructors
    public RewardTerms() {}
    
    // Getters and Setters
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    
    public int getMaxUsesPerUser() { return maxUsesPerUser; }
    public void setMaxUsesPerUser(int maxUsesPerUser) { this.maxUsesPerUser = maxUsesPerUser; }
    
    public String getApplicableToSports() { return applicableToSports; }
    public void setApplicableToSports(String applicableToSports) { this.applicableToSports = applicableToSports; }
    
    public double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(double minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    
    public String getSpecialConditions() { return specialConditions; }
    public void setSpecialConditions(String specialConditions) { this.specialConditions = specialConditions; }
    
    // Business methods
    public boolean isExpired() {
        return expirationDate != null && LocalDateTime.now().isAfter(expirationDate);
    }
    
    public boolean isValidForAmount(double amount) {
        return amount >= minOrderAmount;
    }
}