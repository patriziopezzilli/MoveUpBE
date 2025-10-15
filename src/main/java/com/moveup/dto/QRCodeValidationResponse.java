package com.moveup.dto;

/**
 * Response DTO for QR Code validation
 */
public class QRCodeValidationResponse {
    
    private boolean success;
    private String message;
    private String bookingId;
    private CheckInDetails checkInDetails;
    private PaymentDetails paymentDetails;
    
    // Constructors
    public QRCodeValidationResponse() {}
    
    public QRCodeValidationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    
    public CheckInDetails getCheckInDetails() { return checkInDetails; }
    public void setCheckInDetails(CheckInDetails checkInDetails) { this.checkInDetails = checkInDetails; }
    
    public PaymentDetails getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(PaymentDetails paymentDetails) { this.paymentDetails = paymentDetails; }
    
    // Inner classes
    public static class CheckInDetails {
        private String lessonTitle;
        private String instructorName;
        private String checkedInAt;
        private double distance;
        
        public CheckInDetails() {}
        
        public String getLessonTitle() { return lessonTitle; }
        public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }
        
        public String getInstructorName() { return instructorName; }
        public void setInstructorName(String instructorName) { this.instructorName = instructorName; }
        
        public String getCheckedInAt() { return checkedInAt; }
        public void setCheckedInAt(String checkedInAt) { this.checkedInAt = checkedInAt; }
        
        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }
    }
    
    public static class PaymentDetails {
        private boolean paymentProcessed;
        private double amount;
        private String currency;
        
        public PaymentDetails() {}
        
        public boolean isPaymentProcessed() { return paymentProcessed; }
        public void setPaymentProcessed(boolean paymentProcessed) { this.paymentProcessed = paymentProcessed; }
        
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}
