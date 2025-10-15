package com.moveup.repository;

import com.moveup.model.Reward;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RewardRepository extends MongoRepository<Reward, String> {
    
    // Find by type
    List<Reward> findByType(String type);
    
    // Find active rewards
    List<Reward> findByIsActiveTrue();
    
    // Find featured rewards
    List<Reward> findByIsFeaturedTrue();
    
    // Find available rewards (active and has quantity)
    @Query("{ $and: [" +
           "{'isActive': true}, " +
           "{ $or: [" +
           "{'remainingQuantity': {$gt: 0}}, " +
           "{'remainingQuantity': -1} " +
           "] } " +
           "] }")
    List<Reward> findAvailableRewards();
    
    // Find rewards by points cost range
    @Query("{'pointsCost': {$gte: ?0, $lte: ?1}}")
    List<Reward> findByPointsCostRange(int minPoints, int maxPoints);
    
    // Find rewards user can afford
    @Query("{ $and: [" +
           "{'isActive': true}, " +
           "{'pointsCost': {$lte: ?0}}, " +
           "{ $or: [" +
           "{'remainingQuantity': {$gt: 0}}, " +
           "{'remainingQuantity': -1} " +
           "] } " +
           "] }")
    List<Reward> findAffordableRewards(int userPoints);
    
    // Find discount rewards
    @Query("{'type': 'DISCOUNT', 'isActive': true}")
    List<Reward> findDiscountRewards();
    
    // Find free lesson rewards
    @Query("{'type': 'FREE_LESSON', 'isActive': true}")
    List<Reward> findFreeLessonRewards();
    
    // Find cash rewards
    @Query("{'type': 'CASH_REWARD', 'isActive': true}")
    List<Reward> findCashRewards();
    
    // Find premium feature rewards
    @Query("{'type': 'PREMIUM_FEATURE', 'isActive': true}")
    List<Reward> findPremiumFeatureRewards();
    
    // Find merchandise rewards
    @Query("{'type': 'MERCHANDISE', 'isActive': true}")
    List<Reward> findMerchandiseRewards();
    
    // Find experience rewards
    @Query("{'type': 'EXPERIENCE', 'isActive': true}")
    List<Reward> findExperienceRewards();
    
    // Find rewards ordered by points cost (ascending)
    @Query(value = "{'isActive': true}", sort = "{'pointsCost': 1}")
    List<Reward> findRewardsOrderedByPointsCost();
    
    // Find rewards ordered by points cost (descending)
    @Query(value = "{'isActive': true}", sort = "{'pointsCost': -1}")
    List<Reward> findRewardsOrderedByPointsCostDesc();
    
    // Find limited quantity rewards
    @Query("{'quantity': {$gt: 0, $ne: -1}}")
    List<Reward> findLimitedQuantityRewards();
    
    // Find unlimited rewards
    @Query("{'quantity': -1}")
    List<Reward> findUnlimitedRewards();
    
    // Find rewards expiring soon
    @Query("{'terms.expirationDate': {$exists: true, $gte: ?0, $lte: ?1}}")
    List<Reward> findRewardsExpiringSoon(LocalDateTime now, LocalDateTime soonDate);
    
    // Find non-expiring rewards
    @Query("{'terms.expirationDate': {$exists: false}}")
    List<Reward> findNonExpiringRewards();
    
    // Count rewards by type
    long countByType(String type);
    
    // Count active rewards
    long countByIsActiveTrue();
    
    // Count available rewards
    @Query(value = "{ $and: [" +
           "{'isActive': true}, " +
           "{ $or: [" +
           "{'remainingQuantity': {$gt: 0}}, " +
           "{'remainingQuantity': -1} " +
           "] } " +
           "] }", count = true)
    long countAvailableRewards();
    
    // Find popular rewards (recently created, featured, or low cost)
    @Query("{ $and: [" +
           "{'isActive': true}, " +
           "{ $or: [" +
           "{'isFeatured': true}, " +
           "{'pointsCost': {$lte: ?0}} " +
           "] } " +
           "] }")
    List<Reward> findPopularRewards(int maxPoints);
    
    // Find rewards with specific terms conditions
    @Query("{'terms.specialConditions': {$exists: true, $ne: null, $ne: ''}}")
    List<Reward> findRewardsWithSpecialConditions();
}