package com.moveup.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for updating a Live Activity
 */
public class LiveActivityUpdateRequest {
    
    @NotBlank
    private String status; // upcoming, starting, inProgress, completed
    
    private InstructorLocation instructorLocation;
    
    // Constructors
    public LiveActivityUpdateRequest() {}
    
    public LiveActivityUpdateRequest(String status, InstructorLocation instructorLocation) {
        this.status = status;
        this.instructorLocation = instructorLocation;
    }
    
    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public InstructorLocation getInstructorLocation() { return instructorLocation; }
    public void setInstructorLocation(InstructorLocation instructorLocation) { this.instructorLocation = instructorLocation; }
    
    // Inner class
    public static class InstructorLocation {
        private double latitude;
        private double longitude;
        private double distanceFromLesson;
        
        public InstructorLocation() {}
        
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        
        public double getDistanceFromLesson() { return distanceFromLesson; }
        public void setDistanceFromLesson(double distanceFromLesson) { this.distanceFromLesson = distanceFromLesson; }
    }
}
