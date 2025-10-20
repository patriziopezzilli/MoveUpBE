package com.moveup.repository;

import com.moveup.model.Sport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SportRepository extends MongoRepository<Sport, String> {
    
    // Find by name
    Optional<Sport> findByName(String name);
    
    // Check if name exists
    boolean existsByName(String name);
    
    // Find by category
    List<Sport> findByCategory(String category);
    
    // Find active sports
    @Query("{'isActive': true}")
    List<Sport> findByIsActiveTrue();
    
    // Find popular sports
    @Query(value = "{'isActive': true}", sort = "{ 'popularity': -1 }")
    List<Sport> findPopularSports(org.springframework.data.domain.Pageable pageable);
    
    // Search sports by name (case insensitive)
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Sport> searchByName(String nameQuery);
    
    // Find sports with equipment
    @Query("{'equipment': {$exists: true, $not: {$size: 0}}}")
    List<Sport> findWithEquipment();
    
    // Find sports by difficulty level
    List<Sport> findByDifficultyLevel(String difficultyLevel);
    
    // Find indoor sports
    @Query("{'characteristics.isIndoor': true}")
    List<Sport> findIndoorSports();
    
    // Find outdoor sports
    @Query("{'characteristics.isOutdoor': true}")
    List<Sport> findOutdoorSports();
    
    // Find team sports
    @Query("{'characteristics.isTeamSport': true}")
    List<Sport> findTeamSports();
    
    // Find individual sports
    @Query("{'characteristics.isIndividual': true}")
    List<Sport> findIndividualSports();
    
    // Find sports with seasonal availability
    @Query("{'characteristics.seasonalAvailability': {$exists: true, $not: {$size: 0}}}")
    List<Sport> findWithSeasonalAvailability();
    
    // Find sports by multiple categories
    List<Sport> findByCategoryIn(List<String> categories);
    
    // Count sports by category
    long countByCategory(String category);
    
    // Count active sports
    long countByIsActiveTrue();
    
    // Find all categories (distinct)
    @Query(value = "{}", fields = "{ 'category': 1 }")
    List<Sport> findAllCategories();
    
    // Find recommended sports (high popularity and active)
    @Query("{ 'isActive': true, 'popularity': { $gte: ?0 } }")
    List<Sport> findRecommendedSports(int minPopularity);
}