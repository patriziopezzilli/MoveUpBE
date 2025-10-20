package com.moveup.model;

import java.time.LocalDateTime;

public class RewardTerms {
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