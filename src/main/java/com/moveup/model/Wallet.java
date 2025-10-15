package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "wallets")
public class Wallet {
    
    @Id
    private String id;
    private String userId;
    private Double balance = 0.0;
    private String currency = "EUR";
    
    // Bank account setup
    private Boolean bankAccountSetup = false;
    private String maskedIban;  // e.g., "IT60 •••• •••• •••• 3456"
    private String accountHolderName;
    private String country = "IT";
    
    // Stripe Connect
    private String stripeConnectedAccountId;
    
    // Stats
    private Double totalEarnings = 0.0;
    private Integer totalLessons = 0;
    private Double totalWithdrawn = 0.0;
    
    // iOS Wallet Pass
    private String passSerialNumber;
    private String passUpdateToken;
    private Boolean passAddedToWallet = false;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Wallet() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Wallet(String userId) {
        this();
        this.userId = userId;
        this.passSerialNumber = "trainer-" + userId;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Double getBalance() {
        return balance;
    }
    
    public void setBalance(Double balance) {
        this.balance = balance;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Boolean getBankAccountSetup() {
        return bankAccountSetup;
    }
    
    public void setBankAccountSetup(Boolean bankAccountSetup) {
        this.bankAccountSetup = bankAccountSetup;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getMaskedIban() {
        return maskedIban;
    }
    
    public void setMaskedIban(String maskedIban) {
        this.maskedIban = maskedIban;
    }
    
    public String getAccountHolderName() {
        return accountHolderName;
    }
    
    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getStripeConnectedAccountId() {
        return stripeConnectedAccountId;
    }
    
    public void setStripeConnectedAccountId(String stripeConnectedAccountId) {
        this.stripeConnectedAccountId = stripeConnectedAccountId;
    }
    
    public Double getTotalEarnings() {
        return totalEarnings;
    }
    
    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }
    
    public Integer getTotalLessons() {
        return totalLessons;
    }
    
    public void setTotalLessons(Integer totalLessons) {
        this.totalLessons = totalLessons;
    }
    
    public Double getTotalWithdrawn() {
        return totalWithdrawn;
    }
    
    public void setTotalWithdrawn(Double totalWithdrawn) {
        this.totalWithdrawn = totalWithdrawn;
    }
    
    public String getPassSerialNumber() {
        return passSerialNumber;
    }
    
    public void setPassSerialNumber(String passSerialNumber) {
        this.passSerialNumber = passSerialNumber;
    }
    
    public String getPassUpdateToken() {
        return passUpdateToken;
    }
    
    public void setPassUpdateToken(String passUpdateToken) {
        this.passUpdateToken = passUpdateToken;
    }
    
    public Boolean getPassAddedToWallet() {
        return passAddedToWallet;
    }
    
    public void setPassAddedToWallet(Boolean passAddedToWallet) {
        this.passAddedToWallet = passAddedToWallet;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public void credit(Double amount) {
        this.balance += amount;
        this.totalEarnings += amount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void debit(Double amount) {
        if (this.balance < amount) {
            throw new RuntimeException("Saldo insufficiente");
        }
        this.balance -= amount;
        this.totalWithdrawn += amount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementLessonCount() {
        this.totalLessons++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getAverageLessonPrice() {
        return totalLessons > 0 ? totalEarnings / totalLessons : 0.0;
    }
}
