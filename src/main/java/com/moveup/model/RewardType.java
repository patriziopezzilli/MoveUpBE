package com.moveup.model;

public enum RewardType {
    DISCOUNT("Sconto"),
    FREE_LESSON("Lezione Gratuita"),
    CASH_REWARD("Ricompensa in Denaro"),
    PREMIUM_FEATURE("Funzione Premium"),
    MERCHANDISE("Merchandising"),
    EXPERIENCE("Esperienza"),
    SPECIAL_ACCESS("Accesso Speciale");

    private final String displayName;

    RewardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}