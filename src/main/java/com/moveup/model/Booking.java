package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "bookings")
public class Booking {
    
    @Id
    private String id;
    
    @NotNull
    @Indexed
    private String lessonId;
    
    @NotNull
    @Indexed
    private String instructorId;
    
    @NotNull
    @Indexed
    private String userId;
    
    @NotNull
    private LocalDateTime scheduledDate;
    
    @NotNull
    private LocalTime scheduledTime;
    
    @NotNull
    private BookingStatus status = BookingStatus.PENDING;
    
    @NotNull
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Min(0)
    private double totalAmount;
    
    private String notes;
    private PaymentInfo payment = new PaymentInfo();
    private CancellationInfo cancellation;
    
    // Wallet & Payment Fields (for hold/capture flow)
    private String paymentIntentId;  // Stripe PaymentIntent ID (for capture)
    private String stripeTransferId;  // Stripe Transfer ID (to trainer)
    private LocalDateTime validatedAt;  // When QR code was scanned & payment captured
    private String sport;  // Sport type for description
    private Double price;  // Lesson price
    
    // Apple Wallet Integration Fields
    private WalletPassInfo walletPass;
    
    // Live Activity Integration Fields
    private LiveActivityInfo liveActivity;
    
    // QR Code Check-in Fields
    private CheckInInfo checkin;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public Booking() {}
    
    public Booking(String lessonId, String instructorId, String userId, 
                   LocalDateTime scheduledDate, LocalTime scheduledTime, double totalAmount) {
        this.lessonId = lessonId;
        this.instructorId = instructorId;
        this.userId = userId;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.totalAmount = totalAmount;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getLessonId() { return lessonId; }
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }
    
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    
    public LocalTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalTime scheduledTime) { this.scheduledTime = scheduledTime; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public PaymentInfo getPayment() { return payment; }
    public void setPayment(PaymentInfo payment) { this.payment = payment; }
    
    public CancellationInfo getCancellation() { return cancellation; }
    public void setCancellation(CancellationInfo cancellation) { this.cancellation = cancellation; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // New Getters and Setters for wallet fields
    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
    
    public String getStripeTransferId() { return stripeTransferId; }
    public void setStripeTransferId(String stripeTransferId) { this.stripeTransferId = stripeTransferId; }
    
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    
    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public WalletPassInfo getWalletPass() { return walletPass; }
    public void setWalletPass(WalletPassInfo walletPass) { this.walletPass = walletPass; }
    
    public LiveActivityInfo getLiveActivity() { return liveActivity; }
    public void setLiveActivity(LiveActivityInfo liveActivity) { this.liveActivity = liveActivity; }
    
    public CheckInInfo getCheckin() { return checkin; }
    public void setCheckin(CheckInInfo checkin) { this.checkin = checkin; }
    
    // Business methods
    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }
    
    public void complete() {
        this.status = BookingStatus.COMPLETED;
        this.paymentStatus = PaymentStatus.CAPTURED;
        if (this.validatedAt == null) {
            this.validatedAt = LocalDateTime.now();
        }
    }
    
    public void cancel(String cancelledBy, String reason) {
        this.status = BookingStatus.CANCELLED;
        this.cancellation = new CancellationInfo(cancelledBy, LocalDateTime.now(), reason);
    }
    
    public boolean canBeCancelled() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
    
    public boolean isValidated() {
        return validatedAt != null && paymentStatus == PaymentStatus.CAPTURED;
    }
    
    public void authorizePayment(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        this.paymentStatus = PaymentStatus.AUTHORIZED;
    }
    
    public void capturePayment(String stripeTransferId) {
        this.stripeTransferId = stripeTransferId;
        this.paymentStatus = PaymentStatus.CAPTURED;
        this.validatedAt = LocalDateTime.now();
    }
}

enum BookingStatus {
    PENDING("In attesa"),
    CONFIRMED("Confermata"),
    COMPLETED("Completata"),
    CANCELLED("Cancellata"),
    NO_SHOW("Assente");
    
    private final String displayName;
    
    BookingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

enum PaymentStatus {
    PENDING("In elaborazione"),
    AUTHORIZED("Autorizzato"),
    CAPTURED("Completato"),
    REFUNDED("Rimborsato"),
    FAILED("Fallito");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

// Embedded classes
class PaymentInfo {
    private String stripePaymentIntentId;
    private String stripeChargeId;
    private String refundId;
    private String currency = "EUR";
    private double processingFee;
    
    // Constructors
    public PaymentInfo() {}
    
    // Getters and Setters
    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String stripePaymentIntentId) { this.stripePaymentIntentId = stripePaymentIntentId; }
    
    public String getStripeChargeId() { return stripeChargeId; }
    public void setStripeChargeId(String stripeChargeId) { this.stripeChargeId = stripeChargeId; }
    
    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public double getProcessingFee() { return processingFee; }
    public void setProcessingFee(double processingFee) { this.processingFee = processingFee; }
}

class CancellationInfo {
    private String cancelledBy;
    private LocalDateTime cancelledAt;
    private String reason;
    private double refundAmount;
    
    // Constructors
    public CancellationInfo() {}
    
    public CancellationInfo(String cancelledBy, LocalDateTime cancelledAt, String reason) {
        this.cancelledBy = cancelledBy;
        this.cancelledAt = cancelledAt;
        this.reason = reason;
    }
    
    // Getters and Setters
    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }
}

// Apple Wallet Pass Info
class WalletPassInfo {
    private String serialNumber;
    private boolean passAdded;
    private java.util.List<String> deviceTokens = new java.util.ArrayList<>();
    private LocalDateTime lastUpdated;
    
    public WalletPassInfo() {}
    
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    
    public boolean isPassAdded() { return passAdded; }
    public void setPassAdded(boolean passAdded) { this.passAdded = passAdded; }
    
    public java.util.List<String> getDeviceTokens() { return deviceTokens; }
    public void setDeviceTokens(java.util.List<String> deviceTokens) { this.deviceTokens = deviceTokens; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}

// Live Activity Info
class LiveActivityInfo {
    private String activityId;
    private String pushToken;
    private LocalDateTime startedAt;
    private String status; // active, updated, ended
    private LocalDateTime lastUpdate;
    
    public LiveActivityInfo() {}
    
    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }
    
    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}

// QR Code Check-in Info
class CheckInInfo {
    private LocalDateTime checkedInAt;
    private boolean scannedQR;
    private GeoLocation location;
    private double distance; // Distance from lesson location in meters
    
    public CheckInInfo() {}
    
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
    
    public boolean isScannedQR() { return scannedQR; }
    public void setScannedQR(boolean scannedQR) { this.scannedQR = scannedQR; }
    
    public GeoLocation getLocation() { return location; }
    public void setLocation(GeoLocation location) { this.location = location; }
    
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
}

// GeoJSON Location
class GeoLocation {
    private String type = "Point";
    private double[] coordinates; // [longitude, latitude]
    
    public GeoLocation() {}
    
    public GeoLocation(double longitude, double latitude) {
        this.coordinates = new double[]{longitude, latitude};
    }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public double[] getCoordinates() { return coordinates; }
    public void setCoordinates(double[] coordinates) { this.coordinates = coordinates; }
}
}
}