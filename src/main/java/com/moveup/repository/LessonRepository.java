package com.moveup.repository;

import com.moveup.model.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {
    
    // Find by instructor
    List<Lesson> findByInstructorId(String instructorId);
    
    // Find by sport
    List<Lesson> findBySportId(String sportId);
    
    // Find active lessons
    @Query("{'isActive': true}")
    List<Lesson> findByIsActiveTrue();
    
    // Find lessons by type
    List<Lesson> findByType(String type);
    
    // Find lessons by level
    List<Lesson> findByLevel(String level);
    
    // Find lessons by price range
    @Query("{'price': {$gte: ?0, $lte: ?1}}")
    List<Lesson> findByPriceRange(double minPrice, double maxPrice);
    
    // Find lessons by duration
    List<Lesson> findByDuration(int duration);
    
    // Find lessons with equipment provided
    @Query("{'equipment': {$exists: true, $not: {$size: 0}}}")
    List<Lesson> findWithEquipmentProvided();
    
    // Find lessons by location (city)
    List<Lesson> findByLocationCity(String city);
    
    // Find lessons created between dates
    List<Lesson> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Search lessons by title or description
    @Query("{ $or: [ " +
           "{ 'title': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    List<Lesson> searchByTitleOrDescription(String query);
    
    // Find lessons by instructor and sport
    List<Lesson> findByInstructorIdAndSportId(String instructorId, String sportId);
    
    // Find lessons by instructor and active status
    List<Lesson> findByInstructorIdAndIsActiveTrue(String instructorId);
    
    // Find lessons by sport and level
    List<Lesson> findBySportIdAndLevel(String sportId, String level);
    
    // Find lessons by sport and price range
    @Query("{'sportId': ?0, 'price': {$gte: ?1, $lte: ?2}}")
    List<Lesson> findBySportIdAndPriceRange(String sportId, double minPrice, double maxPrice);
    
    // Find popular lessons (with high booking count)
    @Query(value = "{'isActive': true}", sort = "{ 'bookingCount': -1 }")
    List<Lesson> findPopularLessons(org.springframework.data.domain.Pageable pageable);
    
    // Find lessons by multiple criteria
    @Query("{ $and: [ " +
           "{ 'sportId': { $in: ?0 } }, " +
           "{ 'level': { $in: ?1 } }, " +
           "{ 'price': { $gte: ?2, $lte: ?3 } }, " +
           "{ 'isActive': true } " +
           "] }")
    List<Lesson> findByMultipleCriteria(List<String> sportIds, List<String> levels, double minPrice, double maxPrice);
    
    // Find lessons by location radius (simplified - returns empty for now)
    @Query("{'_id': {$exists: false}}") // Always returns empty
    List<Lesson> findByLocationWithinRadius(double latitude, double longitude, double radiusInMeters);
    
    // Count lessons by instructor
    long countByInstructorId(String instructorId);
    
    // Count active lessons by instructor
    long countByInstructorIdAndIsActiveTrue(String instructorId);
    
    // Count lessons by sport
    long countBySportId(String sportId);
    
    // Find recent lessons
    @Query(value = "{'isActive': true}", sort = "{ 'createdAt': -1 }")
    List<Lesson> findRecentLessons(org.springframework.data.domain.Pageable pageable);
    
    // Find lessons with special offers (simplified - returns empty for now)
    @Query("{'_id': {$exists: false}}") // Always returns empty
    List<Lesson> findWithSpecialOffers();
}