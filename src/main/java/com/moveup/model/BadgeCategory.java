package com.moveup.model;

public enum BadgeCategory {
    LESSONS("Lezioni"),
    REVIEWS("Recensioni"),
    SOCIAL("Sociale"),
    CONSISTENCY("Costanza"),
    ACHIEVEMENT("Risultati"),
    SPECIAL("Speciale");

    private final String displayName;

    BadgeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}