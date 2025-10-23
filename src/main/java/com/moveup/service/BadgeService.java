package com.moveup.service;

import com.moveup.model.Badge;
import com.moveup.model.User;
import com.moveup.repository.BadgeRepository;
import com.moveup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BadgeService {
    
    @Autowired
    private BadgeRepository badgeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    // Create new badge
    public Badge createBadge(Badge badge) {
        // Check if badge title already exists
        if (badgeRepository.findByTitle(badge.getTitle()).isPresent()) {
            throw new RuntimeException("Un badge con questo titolo esiste già");
        }
        
        return badgeRepository.save(badge);
    }
    
    // Get badge by ID
    public Optional<Badge> getBadgeById(String badgeId) {
        return badgeRepository.findById(badgeId);
    }
    
    // Get badge by title
    public Optional<Badge> getBadgeByTitle(String title) {
        return badgeRepository.findByTitle(title);
    }
    
    // Update badge
    public Badge updateBadge(String badgeId, Badge updatedBadge) {
        Badge existingBadge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge non trovato"));
        
        // Update fields
        if (updatedBadge.getTitle() != null && !updatedBadge.getTitle().equals(existingBadge.getTitle())) {
            // Check if new title already exists
            if (badgeRepository.findByTitle(updatedBadge.getTitle()).isPresent()) {
                throw new RuntimeException("Un badge con questo titolo esiste già");
            }
            existingBadge.setTitle(updatedBadge.getTitle());
        }
        
        if (updatedBadge.getDescription() != null) {
            existingBadge.setDescription(updatedBadge.getDescription());
        }
        if (updatedBadge.getType() != null) {
            existingBadge.setType(updatedBadge.getType());
        }
        if (updatedBadge.getCategory() != null) {
            existingBadge.setCategory(updatedBadge.getCategory());
        }
        if (updatedBadge.getRarity() != null) {
            existingBadge.setRarity(updatedBadge.getRarity());
        }
        if (updatedBadge.getRequirements() != null) {
            existingBadge.setRequirements(updatedBadge.getRequirements());
        }
        if (updatedBadge.getRewards() != null) {
            existingBadge.setRewards(updatedBadge.getRewards());
        }
        
        return badgeRepository.save(existingBadge);
    }
    
    // Delete badge (soft delete - mark as inactive)
    public void deleteBadge(String badgeId) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge non trovato"));
        
        badge.setActive(false);
        badgeRepository.save(badge);
    }
    
    // Get all active badges
    public List<Badge> getAllActiveBadges() {
        return badgeRepository.findByIsActiveTrue();
    }
    
    // Get badges by type
    public List<Badge> getBadgesByType(String type) {
        return badgeRepository.findByType(type);
    }
    
    // Get badges by category
    public List<Badge> getBadgesByCategory(String category) {
        return badgeRepository.findByCategory(category);
    }
    
    // Get badges by rarity
    public List<Badge> getBadgesByRarity(String rarity) {
        return badgeRepository.findByRarity(rarity);
    }
    
    // Get achievement badges
    public List<Badge> getAchievementBadges() {
        return badgeRepository.findAchievementBadges();
    }
    
    // Get milestone badges
    public List<Badge> getMilestoneBadges() {
        return badgeRepository.findMilestoneBadges();
    }
    
    // Get special badges
    public List<Badge> getSpecialBadges() {
        return badgeRepository.findSpecialBadges();
    }
    
    // Get seasonal badges
    public List<Badge> getSeasonalBadges() {
        return badgeRepository.findSeasonalBadges();
    }
    
    // Get badges for new users
    public List<Badge> getNewUserBadges() {
        return badgeRepository.findNewUserBadges();
    }
    
    // Check and award badges to user
    public List<Badge> checkAndAwardBadges(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        // Get user stats
        Map<String, Object> userStats = getUserStatsForBadges(user);
        
        // Get all active badges
        List<Badge> allBadges = badgeRepository.findByIsActiveTrue();
        
        List<Badge> awardedBadges = new ArrayList<>();
        
        for (Badge badge : allBadges) {
            // Check if user already has this badge
            if (!user.hasBadge(badge.getId()) && badge.isEligibleForUser(userStats)) {
                awardBadgeToUser(userId, badge.getId());
                awardedBadges.add(badge);
            }
        }
        
        return awardedBadges;
    }
    
    // Award specific badge to user
    public void awardBadgeToUser(String userId, String badgeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge non trovato"));
        
        // Check if user already has this badge
        if (user.hasBadge(badgeId)) {
            return; // User already has this badge
        }
        
        // Award the badge
        user.addBadge(badgeId);
        
        // Add points and experience
        int points = badge.getPointsValue();
        if (badge.getRewards().getPoints() > 0) {
            points = badge.getRewards().getPoints();
        }
        
        user.addPoints(points);
        
        if (badge.getRewards().getExperiencePoints() > 0) {
            user.addExperience(badge.getRewards().getExperiencePoints());
        }
        
        userRepository.save(user);
        
        // Send notification
        notificationService.sendBadgeEarnedNotification(userId, badgeId, badge.getTitle());
    }
    
    // Get badges eligible for user
    public List<Badge> getEligibleBadgesForUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Map<String, Object> userStats = getUserStatsForBadges(user);
        
        return badgeRepository.findByIsActiveTrue()
                .stream()
                .filter(badge -> !user.hasBadge(badge.getId()))
                .filter(badge -> badge.isEligibleForUser(userStats))
                .toList();
    }
    
    // Get user's badges
    public List<Badge> getUserBadges(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        return user.getGameStatus().getBadges()
                .stream()
                .map(badgeId -> badgeRepository.findById(badgeId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
    
    // Count badges by category
    public long countBadgesByCategory(String category) {
        return badgeRepository.countByCategory(category);
    }
    
    // Count badges by type
    public long countBadgesByType(String type) {
        return badgeRepository.countByType(type);
    }
    
    // Count active badges
    public long countActiveBadges() {
        return badgeRepository.countByIsActiveTrue();
    }
    
    // Get badge statistics
    public BadgeStatistics getBadgeStatistics(String badgeId) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge non trovato"));
        
        BadgeStatistics stats = new BadgeStatistics();
        stats.setBadgeId(badgeId);
        stats.setTitle(badge.getTitle());
        stats.setType(badge.getType());
        stats.setCategory(badge.getCategory().name());
        stats.setRarity(badge.getRarity().name());
        stats.setPointsValue(badge.getPointsValue());
        
        // Count users who have this badge
        long usersWithBadge = userRepository.findByIsActiveTrue()
                .stream()
                .filter(user -> user.hasBadge(badgeId))
                .count();
        
        stats.setUsersWithBadge(usersWithBadge);
        
        return stats;
    }
    
    // Helper method to get user stats for badge eligibility
    private Map<String, Object> getUserStatsForBadges(User user) {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic user stats
        stats.put("totalLessons", user.getGameStatus().getTotalLessons());
        stats.put("totalReviews", user.getGameStatus().getTotalReviews());
        stats.put("averageRating", user.getGameStatus().getAverageRating());
        stats.put("totalPoints", user.getGameStatus().getTotalPoints());
        stats.put("level", user.getGameStatus().getLevel());
        stats.put("experiencePoints", user.getGameStatus().getExperiencePoints());
        
        // You could add more complex stats here like:
        // - Consecutive days of activity
        // - Primary sport category
        // - Etc.
        
        return stats;
    }
    
    // Get all badges
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }
    
    // Helper class for badge statistics
    public static class BadgeStatistics {
        private String badgeId;
        private String title;
        private String type;
        private String category;
        private String rarity;
        private int pointsValue;
        private long usersWithBadge;
        
        // Getters and setters
        public String getBadgeId() { return badgeId; }
        public void setBadgeId(String badgeId) { this.badgeId = badgeId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getRarity() { return rarity; }
        public void setRarity(String rarity) { this.rarity = rarity; }
        
        public int getPointsValue() { return pointsValue; }
        public void setPointsValue(int pointsValue) { this.pointsValue = pointsValue; }
        
        public long getUsersWithBadge() { return usersWithBadge; }
        public void setUsersWithBadge(long usersWithBadge) { this.usersWithBadge = usersWithBadge; }
    }
}