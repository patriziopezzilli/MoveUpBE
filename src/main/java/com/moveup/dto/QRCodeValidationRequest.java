package com.moveup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for QR Code validation and check-in
 * Used when a customer scans an instructor's QR code
 */
public class QRCodeValidationRequest {
    
    @NotNull
    private QRData qrData;
    
    @NotBlank
    private String userId;
    
    private LocationData location;
    
    // Constructors
    public QRCodeValidationRequest() {}
    
    public QRCodeValidationRequest(QRData qrData, String userId, LocationData location) {
        this.qrData = qrData;
        this.userId = userId;
        this.location = location;
    }
    
    // Getters and Setters
    public QRData getQrData() { return qrData; }
    public void setQrData(QRData qrData) { this.qrData = qrData; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public LocationData getLocation() { return location; }
    public void setLocation(LocationData location) { this.location = location; }
    
    // Inner classes
    public static class QRData {
        @NotBlank
        private String type;
        
        @NotBlank
        private String instructorId;
        
        @NotNull
        private Long timestamp;
        
        public QRData() {}
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getInstructorId() { return instructorId; }
        public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
        
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }
    
    public static class LocationData {
        private double latitude;
        private double longitude;
        
        public LocationData() {}
        
        public LocationData(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
}
