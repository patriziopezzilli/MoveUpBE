package com.moveup.repository;

import com.moveup.model.Badge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends MongoRepository<Badge, String> {
    
    // Find by title
    Optional<Badge> findByTitle(String title);
    
    // Find by type
    List<Badge> findByType(String type);
    
    // Find by category
    List<Badge> findByCategory(String category);
    
    // Find by rarity
    List<Badge> findByRarity(String rarity);
    
    // Find active badges
    List<Badge> findByIsActiveTrue();
    
    // Find by type and category
    List<Badge> findByTypeAndCategory(String type, String category);
    
    // Find badges by minimum requirements
    @Query("{'requirements.minLessons': {$lte: ?0}}")
    List<Badge> findByMinLessonsRequirement(int userLessons);
    
    // Find badges by minimum rating requirement
    @Query("{'requirements.minRating': {$lte: ?0}}")
    List<Badge> findByMinRatingRequirement(double userRating);
    
    // Find badges by sport category requirement
    @Query("{ $or: [{'requirements.sportCategory': null}, {'requirements.sportCategory': ''}, {'requirements.sportCategory': ?0}] }")
    List<Badge> findByCompatibleSportCategory(String userSportCategory);
    
    // Find achievement badges
    @Query("{'type': 'ACHIEVEMENT', 'isActive': true}")
    List<Badge> findAchievementBadges();
    
    // Find milestone badges
    @Query("{'type': 'MILESTONE', 'isActive': true}")
    List<Badge> findMilestoneBadges();
    
    // Find special badges
    @Query("{'type': 'SPECIAL', 'isActive': true}")
    List<Badge> findSpecialBadges();
    
    // Find seasonal badges
    @Query("{'type': 'SEASONAL', 'isActive': true}")
    List<Badge> findSeasonalBadges();
    
    // Find badges by rarity level (ordered by points value)
    @Query(value = "{'rarity': ?0, 'isActive': true}", sort = "{'rarity': 1}")
    List<Badge> findByRarityOrdered(String rarity);
    
    // Find all rarities (distinct)
    @Query(value = "{}", fields = "{'rarity': 1}")
    List<Badge> findAllRarities();
    
    // Find all categories (distinct)
    @Query(value = "{}", fields = "{'category': 1}")
    List<Badge> findAllCategories();
    
    // Count badges by category
    long countByCategory(String category);
    
    // Count badges by type
    long countByType(String type);
    
    // Count active badges
    long countByIsActiveTrue();
    
    // Find badges with no requirements (achievable by anyone)
    @Query("{ $and: [" +
           "{'requirements.minLessons': {$exists: false}}, " +
           "{'requirements.minReviews': {$exists: false}}, " +
           "{'requirements.minRating': {$exists: false}}, " +
           "{'requirements.minConsecutiveDays': {$exists: false}}, " +
           "{'requirements.sportCategory': {$exists: false}} " +
           "] }")
    List<Badge> findBadgesWithNoRequirements();
    
    // Find badges suitable for new users (low requirements)
    @Query("{ $and: [" +
           "{'isActive': true}, " +
           "{ $or: [" +
           "{'requirements.minLessons': {$lte: 5}}, " +
           "{'requirements.minLessons': {$exists: false}} " +
           "] } " +
           "] }")
    List<Badge> findNewUserBadges();
}