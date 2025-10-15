package com.moveup.repository;

import com.moveup.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    
    // Find by booking
    Optional<Review> findByBookingId(String bookingId);
    
    // Find by instructor
    List<Review> findByInstructorId(String instructorId);
    
    // Find by user
    List<Review> findByUserId(String userId);
    
    // Find by instructor and rating
    List<Review> findByInstructorIdAndRating(String instructorId, int rating);
    
    // Find by instructor and rating greater than or equal
    List<Review> findByInstructorIdAndRatingGreaterThanEqual(String instructorId, int rating);
    
    // Find by type
    List<Review> findByType(String type);
    
    // Find reviews with responses
    @Query("{'response': {$exists: true, $ne: null}}")
    List<Review> findReviewsWithResponses();
    
    // Find reviews without responses
    @Query("{'response': {$exists: false}}")
    List<Review> findReviewsWithoutResponses();
    
    // Find reviews created between dates
    List<Review> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find recent reviews for instructor
    @Query(value = "{'instructorId': ?0}", sort = "{ 'createdAt': -1 }")
    List<Review> findRecentReviewsByInstructor(String instructorId, org.springframework.data.domain.Pageable pageable);
    
    // Find positive reviews (rating >= 4)
    @Query("{'rating': {$gte: 4}}")
    List<Review> findPositiveReviews();
    
    // Find negative reviews (rating <= 2)
    @Query("{'rating': {$lte: 2}}")
    List<Review> findNegativeReviews();
    
    // Find reviews with comments
    @Query("{'comment': {$exists: true, $ne: null, $ne: ''}}")
    List<Review> findReviewsWithComments();
    
    // Search reviews by comment content
    @Query("{'comment': {$regex: ?0, $options: 'i'}}")
    List<Review> searchByCommentContent(String query);
    
    // Count reviews by instructor
    long countByInstructorId(String instructorId);
    
    // Count reviews by user
    long countByUserId(String userId);
    
    // Count reviews by rating
    long countByRating(int rating);
    
    // Count reviews by instructor and rating
    long countByInstructorIdAndRating(String instructorId, int rating);
    
    // Calculate average rating for instructor
    @Query(value = "{'instructorId': ?0}", fields = "{'rating': 1}")
    List<Review> findRatingsForInstructor(String instructorId);
    
    // Find top-rated reviews for instructor
    @Query(value = "{'instructorId': ?0, 'rating': {$gte: 4}}", sort = "{ 'rating': -1, 'createdAt': -1 }")
    List<Review> findTopReviewsForInstructor(String instructorId, org.springframework.data.domain.Pageable pageable);
    
    // Find reviews by instructor with high detailed ratings
    @Query("{'instructorId': ?0, 'details.punctualityRating': {$gte: ?1}, 'details.professionalismRating': {$gte: ?1}}")
    List<Review> findHighQualityReviewsByInstructor(String instructorId, int minDetailRating);
    
    // Find recommended reviews (would recommend = true)
    @Query("{'instructorId': ?0, 'details.wouldRecommend': true}")
    List<Review> findRecommendedReviewsByInstructor(String instructorId);
    
    // Count positive recommendations for instructor
    @Query(value = "{'instructorId': ?0, 'details.wouldRecommend': true}", count = true)
    long countRecommendationsByInstructor(String instructorId);
    
    // Find reviews by instructor in date range
    List<Review> findByInstructorIdAndCreatedAtBetween(String instructorId, LocalDateTime start, LocalDateTime end);
    
    // Check if user has already reviewed a booking
    boolean existsByBookingIdAndUserId(String bookingId, String userId);
}