package com.moveup.service;

import com.moveup.model.Review;
import com.moveup.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    // Create new review
    public Review createReview(Review review) {
        // Check if user has already reviewed this booking
        if (reviewRepository.existsByBookingIdAndUserId(review.getBookingId(), review.getUserId())) {
            throw new RuntimeException("Hai già recensito questa lezione");
        }
        
        Review savedReview = reviewRepository.save(review);
        
        // Send notification to instructor
        notificationService.sendNewReviewNotification(
            review.getInstructorId(), 
            savedReview.getId(), 
            review.getRating()
        );
        
        return savedReview;
    }
    
    // Get review by ID
    public Optional<Review> getReviewById(String reviewId) {
        return reviewRepository.findById(reviewId);
    }
    
    // Get reviews for instructor
    public List<Review> getReviewsForInstructor(String instructorId, int limit) {
        return reviewRepository.findRecentReviewsByInstructor(instructorId, PageRequest.of(0, limit));
    }
    
    // Get reviews by user
    public List<Review> getReviewsByUser(String userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    // Add response to review
    public void addResponseToReview(String reviewId, String responseText, String responderId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Recensione non trovata"));
        
        if (!review.canAddResponse()) {
            throw new RuntimeException("Questa recensione ha già una risposta");
        }
        
        review.addResponse(responseText, responderId);
        reviewRepository.save(review);
    }
    
    // Calculate average rating for instructor
    public double calculateAverageRatingForInstructor(String instructorId) {
        List<Review> reviews = reviewRepository.findRatingsForInstructor(instructorId);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream()
                .mapToInt(Review::getRating)
                .sum();
        
        return sum / reviews.size();
    }
    
    // Count reviews for instructor
    public long countReviewsForInstructor(String instructorId) {
        return reviewRepository.countByInstructorId(instructorId);
    }
    
    // Get top reviews for instructor
    public List<Review> getTopReviewsForInstructor(String instructorId, int limit) {
        return reviewRepository.findTopReviewsForInstructor(instructorId, PageRequest.of(0, limit));
    }
    
    // Get positive reviews
    public List<Review> getPositiveReviews() {
        return reviewRepository.findPositiveReviews();
    }
    
    // Get negative reviews
    public List<Review> getNegativeReviews() {
        return reviewRepository.findNegativeReviews();
    }
    
    // Search reviews by content
    public List<Review> searchReviewsByContent(String query) {
        return reviewRepository.searchByCommentContent(query);
    }
    
    // Get reviews with responses
    public List<Review> getReviewsWithResponses() {
        return reviewRepository.findReviewsWithResponses();
    }
    
    // Get reviews without responses
    public List<Review> getReviewsWithoutResponses() {
        return reviewRepository.findReviewsWithoutResponses();
    }
    
    // Get review statistics for instructor
    public ReviewStatistics getReviewStatisticsForInstructor(String instructorId) {
        List<Review> allReviews = reviewRepository.findByInstructorId(instructorId);
        
        ReviewStatistics stats = new ReviewStatistics();
        stats.setTotalReviews(allReviews.size());
        
        if (!allReviews.isEmpty()) {
            // Calculate average rating
            double avgRating = allReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            stats.setAverageRating(avgRating);
            
            // Count by rating
            stats.setFiveStars(countReviewsByRating(allReviews, 5));
            stats.setFourStars(countReviewsByRating(allReviews, 4));
            stats.setThreeStars(countReviewsByRating(allReviews, 3));
            stats.setTwoStars(countReviewsByRating(allReviews, 2));
            stats.setOneStar(countReviewsByRating(allReviews, 1));
            
            // Count recommendations
            long recommendations = allReviews.stream()
                    .filter(review -> review.getDetails() != null && review.getDetails().isWouldRecommend())
                    .count();
            stats.setRecommendations(recommendations);
        }
        
        return stats;
    }
    
    // Helper method to count reviews by rating
    private long countReviewsByRating(List<Review> reviews, int rating) {
        return reviews.stream()
                .filter(review -> review.getRating() == rating)
                .count();
    }
    
    // Get reviews in date range
    public List<Review> getReviewsInDateRange(String instructorId, LocalDateTime start, LocalDateTime end) {
        return reviewRepository.findByInstructorIdAndCreatedAtBetween(instructorId, start, end);
    }
    
    // Get reviews with high detailed ratings
    public List<Review> getHighQualityReviews(String instructorId, int minDetailRating) {
        return reviewRepository.findHighQualityReviewsByInstructor(instructorId, minDetailRating);
    }
    
    // Count recommendations for instructor
    public long countRecommendationsForInstructor(String instructorId) {
        return reviewRepository.countRecommendationsByInstructor(instructorId);
    }
    
    // Helper class for review statistics
    public static class ReviewStatistics {
        private int totalReviews;
        private double averageRating;
        private long fiveStars;
        private long fourStars;
        private long threeStars;
        private long twoStars;
        private long oneStar;
        private long recommendations;
        
        // Getters and setters
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
        
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        
        public long getFiveStars() { return fiveStars; }
        public void setFiveStars(long fiveStars) { this.fiveStars = fiveStars; }
        
        public long getFourStars() { return fourStars; }
        public void setFourStars(long fourStars) { this.fourStars = fourStars; }
        
        public long getThreeStars() { return threeStars; }
        public void setThreeStars(long threeStars) { this.threeStars = threeStars; }
        
        public long getTwoStars() { return twoStars; }
        public void setTwoStars(long twoStars) { this.twoStars = twoStars; }
        
        public long getOneStar() { return oneStar; }
        public void setOneStar(long oneStar) { this.oneStar = oneStar; }
        
        public long getRecommendations() { return recommendations; }
        public void setRecommendations(long recommendations) { this.recommendations = recommendations; }
    }
}