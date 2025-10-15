package com.moveup.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for starting a Live Activity
 */
public class LiveActivityStartRequest {
    
    @NotBlank
    private String bookingId;
    
    @NotBlank
    private String pushToken;
    
    private String userId;
    
    // Constructors
    public LiveActivityStartRequest() {}
    
    public LiveActivityStartRequest(String bookingId, String pushToken, String userId) {
        this.bookingId = bookingId;
        this.pushToken = pushToken;
        this.userId = userId;
    }
    
    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    
    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
