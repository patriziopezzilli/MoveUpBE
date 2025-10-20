package com.moveup.repository;

import com.moveup.model.Instructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends MongoRepository<Instructor, String> {
    
    // Find by email
    Optional<Instructor> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find by username (searches by email field)
    @Query("{ 'email' : ?0 }")
    Optional<Instructor> findByUsername(String username);

    // Check if username exists (checks by email field)
    @Query(value = "{ 'email' : ?0 }", exists = true)
    boolean existsByUsername(String username);
    
    // Find by phone number
    Optional<Instructor> findByPhoneNumber(String phoneNumber);
    
    // Find active instructors
    List<Instructor> findByIsActiveTrue();
    
    // Find verified instructors
    List<Instructor> findByIsVerifiedTrue();
    
    // Find available instructors
    List<Instructor> findByIsAvailableTrue();
    
    // Find instructors by sport
    @Query("{'sports': {$elemMatch: {'sportId': ?0}}}")
    List<Instructor> findBySportId(String sportId);
    
    // Find instructors by city
    List<Instructor> findByLocationCity(String city);
    
    // Find instructors by rating (greater than or equal)
    @Query("{'rating': {$gte: ?0}}")
    List<Instructor> findByAverageRatingGreaterThanEqual(double rating);
    
    // Find instructors with certification
    @Query("{'certifications': {$exists: true, $not: {$size: 0}}}")
    List<Instructor> findWithCertifications();
    
    // Find instructors by experience years (greater than or equal)
    @Query("{'yearsOfExperience': {$gte: ?0}}")
    List<Instructor> findByExperienceGreaterThanEqual(int years);
    
    // Find instructors by hourly rate range
    @Query("{'hourlyRate': {$gte: ?0, $lte: ?1}}")
    List<Instructor> findByHourlyRateRange(double minRate, double maxRate);
    
    // Search instructors by name (case insensitive)
    @Query("{ $or: [ " +
           "{ 'firstName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'lastName': { $regex: ?0, $options: 'i' } } " +
           "] }")
    List<Instructor> searchByName(String nameQuery);
    
    // Find instructors created between dates
    List<Instructor> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Count total active instructors
    long countByIsActiveTrue();
    
    // Count verified instructors
    long countByIsVerifiedTrue();
    
    // Count available instructors
    long countByIsAvailableTrue();
    
    // Find top-rated instructors
    @Query(value = "{'isActive': true, 'isVerified': true}", sort = "{ 'rating': -1 }")
    List<Instructor> findTopRatedInstructors(org.springframework.data.domain.Pageable pageable);
    
    // Find instructors with upcoming availability
    @Query("{'availability': {$elemMatch: {'isAvailable': true, 'date': {$gte: ?0}}}}")
    List<Instructor> findWithUpcomingAvailability(LocalDateTime fromDate);
    
    // Find instructors by location radius (requires geospatial indexing)
    @Query("{'location': {$near: {$geometry: {type: 'Point', coordinates: [?1, ?0]}, $maxDistance: ?2}}}")
    List<Instructor> findByLocationWithinRadius(double latitude, double longitude, double radiusInMeters);
    
    // Find instructors by multiple sports
    @Query("{'specializations': {$in: ?0}}")
    List<Instructor> findBySportIds(List<String> sportIds);
}