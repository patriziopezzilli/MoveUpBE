package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transactions")
public class Transaction {
    
    @Id
    private String id;
    private String walletId;
    
    public enum TransactionType {
        LESSON_PAYMENT,
        PAYOUT,
        REFUND,
        ADJUSTMENT,
        BONUS
    }
    
    private TransactionType type;
    private Double amount;
    private String currency = "EUR";
    
    // Description
    private String description;
    private String notes;
    
    // References
    private String bookingId;
    private String customerId;
    private String customerName;
    
    // Fee breakdown
    private Double grossAmount;    // Total lesson price (e.g., €50)
    private Double platformFee;    // Platform fee (e.g., €5)
    private Double netAmount;      // Amount to trainer (e.g., €45)
    
    // Stripe references
    private String stripeTransferId;
    private String stripePaymentIntentId;
    private String stripePayoutId;
    
    public enum TransactionStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELLED
    }
    
    private TransactionStatus status = TransactionStatus.PENDING;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime failedAt;
    
    // Constructors
    public Transaction() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Transaction(String walletId, TransactionType type, Double netAmount, String description) {
        this();
        this.walletId = walletId;
        this.type = type;
        this.amount = netAmount;
        this.netAmount = netAmount;
        this.description = description;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getWalletId() {
        return walletId;
    }
    
    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public Double getGrossAmount() {
        return grossAmount;
    }
    
    public void setGrossAmount(Double grossAmount) {
        this.grossAmount = grossAmount;
    }
    
    public Double getPlatformFee() {
        return platformFee;
    }
    
    public void setPlatformFee(Double platformFee) {
        this.platformFee = platformFee;
    }
    
    public Double getNetAmount() {
        return netAmount;
    }
    
    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }
    
    public String getStripeTransferId() {
        return stripeTransferId;
    }
    
    public void setStripeTransferId(String stripeTransferId) {
        this.stripeTransferId = stripeTransferId;
    }
    
    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }
    
    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }
    
    public String getStripePayoutId() {
        return stripePayoutId;
    }
    
    public void setStripePayoutId(String stripePayoutId) {
        this.stripePayoutId = stripePayoutId;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
        if (status == TransactionStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        } else if (status == TransactionStatus.FAILED) {
            this.failedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getFailedAt() {
        return failedAt;
    }
    
    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }
    
    // Helper methods
    public Double getFeePercentage() {
        if (grossAmount != null && grossAmount > 0 && platformFee != null) {
            return (platformFee / grossAmount) * 100;
        }
        return 0.0;
    }
    
    public void complete() {
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    public void fail(String reason) {
        this.status = TransactionStatus.FAILED;
        this.failedAt = LocalDateTime.now();
        this.notes = reason;
    }
}
