package com.moveup.model;

public enum BadgeType {
    ACHIEVEMENT("Traguardo"),
    MILESTONE("Pietra Miliare"),
    SPECIAL("Speciale"),
    SEASONAL("Stagionale");

    private final String displayName;

    BadgeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}