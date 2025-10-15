package com.moveup.model;

public enum NotificationPriority {
    LOW("Bassa"),
    MEDIUM("Media"),
    HIGH("Alta"),
    URGENT("Urgente");
    
    private final String displayName;
    
    NotificationPriority(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
