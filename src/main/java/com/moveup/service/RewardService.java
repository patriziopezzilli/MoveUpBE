package com.moveup.service;

import com.moveup.model.Reward;
import com.moveup.model.User;
import com.moveup.repository.RewardRepository;
import com.moveup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RewardService {
    
    @Autowired
    private RewardRepository rewardRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    // Create new reward
    public Reward createReward(Reward reward) {
        return rewardRepository.save(reward);
    }
    
    // Get reward by ID
    public Optional<Reward> getRewardById(String rewardId) {
        return rewardRepository.findById(rewardId);
    }
    
    // Update reward
    public Reward updateReward(String rewardId, Reward updatedReward) {
        Reward existingReward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Ricompensa non trovata"));
        
        // Update fields
        if (updatedReward.getTitle() != null) {
            existingReward.setTitle(updatedReward.getTitle());
        }
        if (updatedReward.getDescription() != null) {
            existingReward.setDescription(updatedReward.getDescription());
        }
        if (updatedReward.getType() != null) {
            existingReward.setType(updatedReward.getType());
        }
        if (updatedReward.getPointsCost() > 0) {
            existingReward.setPointsCost(updatedReward.getPointsCost());
        }
        if (updatedReward.getQuantity() != 0) {
            existingReward.setQuantity(updatedReward.getQuantity());
            existingReward.setRemainingQuantity(updatedReward.getQuantity());
        }
        if (updatedReward.getValue() != null) {
            existingReward.setValue(updatedReward.getValue());
        }
        if (updatedReward.getTerms() != null) {
            existingReward.setTerms(updatedReward.getTerms());
        }
        
        return rewardRepository.save(existingReward);
    }
    
    // Delete reward (soft delete - mark as inactive)
    public void deleteReward(String rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Ricompensa non trovata"));
        
        reward.setActive(false);
        rewardRepository.save(reward);
    }
    
    // Get all active rewards
    public List<Reward> getAllActiveRewards() {
        return rewardRepository.findByIsActiveTrue();
    }
    
    // Get available rewards
    public List<Reward> getAvailableRewards() {
        return rewardRepository.findAvailableRewards();
    }
    
    // Get featured rewards
    public List<Reward> getFeaturedRewards() {
        return rewardRepository.findByIsFeaturedTrue();
    }
    
    // Get rewards by type
    public List<Reward> getRewardsByType(String type) {
        return rewardRepository.findByType(type);
    }
    
    // Get rewards user can afford
    public List<Reward> getAffordableRewards(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        int userPoints = user.getGameStatus().getTotalPoints();
        return rewardRepository.findAffordableRewards(userPoints);
    }
    
    // Get rewards by points cost range
    public List<Reward> getRewardsByPointsRange(int minPoints, int maxPoints) {
        return rewardRepository.findByPointsCostRange(minPoints, maxPoints);
    }
    
    // Get discount rewards
    public List<Reward> getDiscountRewards() {
        return rewardRepository.findDiscountRewards();
    }
    
    // Get free lesson rewards
    public List<Reward> getFreeLessonRewards() {
        return rewardRepository.findFreeLessonRewards();
    }
    
    // Get cash rewards
    public List<Reward> getCashRewards() {
        return rewardRepository.findCashRewards();
    }
    
    // Get premium feature rewards
    public List<Reward> getPremiumFeatureRewards() {
        return rewardRepository.findPremiumFeatureRewards();
    }
    
    // Get merchandise rewards
    public List<Reward> getMerchandiseRewards() {
        return rewardRepository.findMerchandiseRewards();
    }
    
    // Get experience rewards
    public List<Reward> getExperienceRewards() {
        return rewardRepository.findExperienceRewards();
    }
    
    // Get rewards ordered by points cost
    public List<Reward> getRewardsOrderedByPoints(boolean ascending) {
        if (ascending) {
            return rewardRepository.findRewardsOrderedByPointsCost();
        } else {
            return rewardRepository.findRewardsOrderedByPointsCostDesc();
        }
    }
    
    // Redeem reward
    public void redeemReward(String userId, String rewardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Ricompensa non trovata"));
        
        // Check if reward can be redeemed
        if (!reward.canBeRedeemed(user.getGameStatus().getTotalPoints())) {
            throw new RuntimeException("Punti insufficienti o ricompensa non disponibile");
        }
        
        // Check expiration
        if (reward.getTerms().isExpired()) {
            throw new RuntimeException("Questa ricompensa è scaduta");
        }
        
        // Deduct points from user
        user.deductPoints(reward.getPointsCost());
        
        // Add redeemed reward to user
        user.addRedeemedReward(rewardId);
        
        // Update reward quantity
        reward.redeem();
        
        // Save changes
        userRepository.save(user);
        rewardRepository.save(reward);
        
        // Send notification
        notificationService.createNotification(
            userId,
            "Ricompensa Riscattata!",
            "Hai riscattato con successo: " + reward.getTitle(),
            "REWARD_AVAILABLE"
        );
        
        // Process specific reward type
        processRewardRedemption(user, reward);
    }
    
    // Check if user can redeem reward
    public boolean canUserRedeemReward(String userId, String rewardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Ricompensa non trovata"));
        
        return reward.canBeRedeemed(user.getGameStatus().getTotalPoints()) && 
               !reward.getTerms().isExpired();
    }
    
    // Get user's redeemed rewards
    public List<Reward> getUserRedeemedRewards(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        return user.getGameStatus().getRedeemedRewards()
                .stream()
                .map(rewardId -> rewardRepository.findById(rewardId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
    
    // Notify users about new rewards
    public void notifyUsersAboutAffordableRewards() {
        List<User> users = userRepository.findByIsActiveTrue();
        
        for (User user : users) {
            List<Reward> affordableRewards = getAffordableRewards(user.getId());
            
            for (Reward reward : affordableRewards) {
                // Check if user hasn't been notified about this reward recently
                if (!user.hasRedeemedReward(reward.getId())) {
                    notificationService.sendRewardAvailableNotification(
                        user.getId(),
                        reward.getId(),
                        reward.getTitle()
                    );
                }
            }
        }
    }
    
    // Get reward statistics
    public RewardStatistics getRewardStatistics(String rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Ricompensa non trovata"));
        
        RewardStatistics stats = new RewardStatistics();
        stats.setRewardId(rewardId);
        stats.setTitle(reward.getTitle());
        stats.setType(reward.getType().name());
        stats.setPointsCost(reward.getPointsCost());
        stats.setTotalQuantity(reward.getQuantity());
        stats.setRemainingQuantity(reward.getRemainingQuantity());
        
        // Calculate redemption count
        int redemptionCount = reward.getQuantity() == -1 ? 0 : 
                             reward.getQuantity() - reward.getRemainingQuantity();
        stats.setRedemptionCount(redemptionCount);
        
        return stats;
    }
    
    // Get rewards expiring soon
    public List<Reward> getRewardsExpiringSoon(int daysAhead) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soonDate = now.plusDays(daysAhead);
        return rewardRepository.findRewardsExpiringSoon(now, soonDate);
    }
    
    // Get popular rewards
    public List<Reward> getPopularRewards(int maxPoints) {
        return rewardRepository.findPopularRewards(maxPoints);
    }
    
    // Count available rewards
    public long countAvailableRewards() {
        return rewardRepository.countAvailableRewards();
    }
    
    // Count rewards by type
    public long countRewardsByType(String type) {
        return rewardRepository.countByType(type);
    }
    
    // Helper method to process specific reward redemption
    private void processRewardRedemption(User user, Reward reward) {
        // Process reward based on type
        try {
            switch (reward.getType()) {
                case FREE_LESSON:
                    user.addFreeLessonCredit();
                    break;
                case DISCOUNT:
                    // Could add discount credit to user account
                    user.addFreeLessonCredit(); // Placeholder - use discount for now
                    break;
                case CASH_REWARD:
                    // Could add cash credit to wallet
                    user.addFreeLessonCredit(); // Placeholder - use lesson credit for now
                    break;
                case PREMIUM_FEATURE:
                    // Could unlock premium features
                    user.addFreeLessonCredit(); // Placeholder - use lesson credit for now
                    break;
                case MERCHANDISE:
                    // Could add merchandise credit
                    user.addFreeLessonCredit(); // Placeholder - use lesson credit for now
                    break;
                case EXPERIENCE:
                    // Could add experience points
                    if (user.getGameStatus() != null) {
                        user.getGameStatus().setTotalPoints(user.getGameStatus().getTotalPoints() + 100);
                    }
                    break;
                case SPECIAL_ACCESS:
                    // Could grant special access
                    user.addFreeLessonCredit(); // Placeholder - use lesson credit for now
                    break;
                default:
                    user.addFreeLessonCredit(); // Fallback
                    break;
            }
        } catch (Exception e) {
            // Log error but don't fail the redemption
            System.err.println("Error processing reward redemption: " + e.getMessage());
        }
        
        userRepository.save(user);
    }
    
    // Helper method to generate discount coupon
    private String generateDiscountCoupon(String userId, String rewardId) {
        return "MOVE" + userId.substring(0, 4).toUpperCase() + 
               rewardId.substring(0, 4).toUpperCase() + 
               System.currentTimeMillis() % 10000;
    }
    
    // Helper class for reward statistics
    public static class RewardStatistics {
        private String rewardId;
        private String title;
        private String type;
        private int pointsCost;
        private int totalQuantity;
        private int remainingQuantity;
        private int redemptionCount;
        
        // Getters and setters
        public String getRewardId() { return rewardId; }
        public void setRewardId(String rewardId) { this.rewardId = rewardId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public int getPointsCost() { return pointsCost; }
        public void setPointsCost(int pointsCost) { this.pointsCost = pointsCost; }
        
        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
        
        public int getRemainingQuantity() { return remainingQuantity; }
        public void setRemainingQuantity(int remainingQuantity) { this.remainingQuantity = remainingQuantity; }
        
        public int getRedemptionCount() { return redemptionCount; }
        public void setRedemptionCount(int redemptionCount) { this.redemptionCount = redemptionCount; }
    }
}