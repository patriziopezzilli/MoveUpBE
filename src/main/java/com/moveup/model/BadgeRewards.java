package com.moveup.model;

public class BadgeRewards {
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
