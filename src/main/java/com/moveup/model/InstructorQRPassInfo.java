package com.moveup.model;

import java.time.LocalDateTime;

public class InstructorQRPassInfo {
    private String serialNumber;
    private LocalDateTime generatedAt;
    private int totalScans = 0;
    private LocalDateTime lastScan;
    private boolean passAddedToWallet = false;

    public InstructorQRPassInfo() {}

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public int getTotalScans() { return totalScans; }
    public void setTotalScans(int totalScans) { this.totalScans = totalScans; }

    public LocalDateTime getLastScan() { return lastScan; }
    public void setLastScan(LocalDateTime lastScan) { this.lastScan = lastScan; }

    public boolean isPassAddedToWallet() { return passAddedToWallet; }
    public void setPassAddedToWallet(boolean passAddedToWallet) { this.passAddedToWallet = passAddedToWallet; }

    public void recordScan() {
        this.totalScans++;
        this.lastScan = LocalDateTime.now();
    }
}