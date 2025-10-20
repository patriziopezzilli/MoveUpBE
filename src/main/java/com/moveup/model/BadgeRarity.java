package com.moveup.model;

public enum BadgeRarity {
    COMMON("Comune", 10, "#9E9E9E"),
    UNCOMMON("Non Comune", 25, "#4CAF50"),
    RARE("Raro", 50, "#2196F3"),
    EPIC("Epico", 100, "#9C27B0"),
    LEGENDARY("Leggendario", 250, "#FF9800");

    private final String displayName;
    private final int pointsValue;
    private final String colorHex;

    BadgeRarity(String displayName, int pointsValue, String colorHex) {
        this.displayName = displayName;
        this.pointsValue = pointsValue;
        this.colorHex = colorHex;
    }

    public String getDisplayName() { return displayName; }
    public int getPointsValue() { return pointsValue; }
    public String getColorHex() { return colorHex; }
}