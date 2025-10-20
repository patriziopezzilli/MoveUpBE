package com.moveup.model;

import java.util.ArrayList;
import java.util.List;

public class GameStatus {
    private int totalPoints = 0;
    private int level = 1;
    private List<String> badges = new ArrayList<>();
    private int totalLessons = 0;
    private int totalReviews = 0;
    private double averageRating = 0.0;
    private int experiencePoints = 0;
    private List<String> redeemedRewards = new ArrayList<>();
    
    // Constructors
    public GameStatus() {}
    
    // Getters and Setters
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public List<String> getBadges() { return badges; }
    public void setBadges(List<String> badges) { this.badges = badges; }
    
    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }
    
    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
    
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    
    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }
    
    public List<String> getRedeemedRewards() { return redeemedRewards; }
    public void setRedeemedRewards(List<String> redeemedRewards) { this.redeemedRewards = redeemedRewards; }
    
    // Business methods for rewards
    public void addRedeemedReward(String rewardId) {
        if (redeemedRewards == null) {
            redeemedRewards = new ArrayList<>();
        }
        redeemedRewards.add(rewardId);
    }
    
    public boolean hasRedeemedReward(String rewardId) {
        return redeemedRewards != null && redeemedRewards.contains(rewardId);
    }
}
