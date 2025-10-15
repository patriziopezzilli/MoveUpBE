package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "reviews")
public class Review {
    
    @Id
    private String id;
    
    @NotNull
    @Indexed
    private String bookingId;
    
    @NotNull
    @Indexed
    private String instructorId;
    
    @NotNull
    @Indexed
    private String userId;
    
    @NotNull
    @Min(1)
    @Max(5)
    private int rating;
    
    @Size(max = 1000)
    private String comment;
    
    @NotNull
    private ReviewType type = ReviewType.USER_TO_INSTRUCTOR;
    
    private ReviewDetails details = new ReviewDetails();
    private ReviewResponse response;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public Review() {}
    
    public Review(String bookingId, String instructorId, String userId, int rating, String comment) {
        this.bookingId = bookingId;
        this.instructorId = instructorId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public ReviewType getType() { return type; }
    public void setType(ReviewType type) { this.type = type; }
    
    public ReviewDetails getDetails() { return details; }
    public void setDetails(ReviewDetails details) { this.details = details; }
    
    public ReviewResponse getResponse() { return response; }
    public void setResponse(ReviewResponse response) { this.response = response; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public void addResponse(String responseText, String responderId) {
        this.response = new ReviewResponse(responseText, responderId, LocalDateTime.now());
    }
    
    public boolean canAddResponse() {
        return response == null;
    }
    
    public boolean isPositive() {
        return rating >= 4;
    }
    
    public boolean isNegative() {
        return rating <= 2;
    }
}

enum ReviewType {
    USER_TO_INSTRUCTOR("Utente → Istruttore"),
    INSTRUCTOR_TO_USER("Istruttore → Utente");
    
    private final String displayName;
    
    ReviewType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

// Embedded classes
static class ReviewDetails {
    private List<String> highlights;
    private int punctualityRating;
    private int professionalismRating;
    private int communicationRating;
    private int preparationRating;
    private boolean wouldRecommend = true;
    
    // Constructors
    public ReviewDetails() {}
    
    // Getters and Setters
    public List<String> getHighlights() { return highlights; }
    public void setHighlights(List<String> highlights) { this.highlights = highlights; }
    
    public int getPunctualityRating() { return punctualityRating; }
    public void setPunctualityRating(int punctualityRating) { this.punctualityRating = punctualityRating; }
    
    public int getProfessionalismRating() { return professionalismRating; }
    public void setProfessionalismRating(int professionalismRating) { this.professionalismRating = professionalismRating; }
    
    public int getCommunicationRating() { return communicationRating; }
    public void setCommunicationRating(int communicationRating) { this.communicationRating = communicationRating; }
    
    public int getPreparationRating() { return preparationRating; }
    public void setPreparationRating(int preparationRating) { this.preparationRating = preparationRating; }
    
    public boolean isWouldRecommend() { return wouldRecommend; }
    public void setWouldRecommend(boolean wouldRecommend) { this.wouldRecommend = wouldRecommend; }
    
    // Helper method
    public double getAverageDetailRating() {
        int count = 0;
        int sum = 0;
        
        if (punctualityRating > 0) { count++; sum += punctualityRating; }
        if (professionalismRating > 0) { count++; sum += professionalismRating; }
        if (communicationRating > 0) { count++; sum += communicationRating; }
        if (preparationRating > 0) { count++; sum += preparationRating; }
        
        return count > 0 ? (double) sum / count : 0.0;
    }
}

class ReviewResponse {
    private String text;
    private String responderId;
    private LocalDateTime respondedAt;
    
    // Constructors
    public ReviewResponse() {}
    
    public ReviewResponse(String text, String responderId, LocalDateTime respondedAt) {
        this.text = text;
        this.responderId = responderId;
        this.respondedAt = respondedAt;
    }
    
    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public String getResponderId() { return responderId; }
    public void setResponderId(String responderId) { this.responderId = responderId; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}