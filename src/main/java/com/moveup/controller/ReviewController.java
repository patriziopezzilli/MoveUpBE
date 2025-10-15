package com.moveup.controller;

import com.moveup.model.Review;
import com.moveup.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(@Valid @RequestBody Review review) {
        try {
            Review createdReview = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Recensione creata con successo", "review", createdReview));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable String reviewId) {
        return reviewService.getReviewById(reviewId)
                .map(review -> ResponseEntity.ok(review))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Review>> getReviewsForInstructor(@PathVariable String instructorId, @RequestParam(defaultValue = "20") int limit) {
        List<Review> reviews = reviewService.getReviewsForInstructor(instructorId, limit);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable String userId) {
        List<Review> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }
    
    @PostMapping("/{reviewId}/response")
    public ResponseEntity<Map<String, String>> addResponse(@PathVariable String reviewId, @RequestBody Map<String, String> request) {
        try {
            String responseText = request.get("response");
            String responderId = request.get("responderId");
            reviewService.addResponseToReview(reviewId, responseText, responderId);
            return ResponseEntity.ok(Map.of("message", "Risposta aggiunta con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/instructor/{instructorId}/stats")
    public ResponseEntity<ReviewService.ReviewStatistics> getInstructorReviewStats(@PathVariable String instructorId) {
        ReviewService.ReviewStatistics stats = reviewService.getReviewStatisticsForInstructor(instructorId);
        return ResponseEntity.ok(stats);
    }
}