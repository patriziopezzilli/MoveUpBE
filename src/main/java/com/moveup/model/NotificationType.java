package com.moveup.model;

public enum NotificationType {
    BOOKING_CONFIRMATION("Conferma Prenotazione"),
    BOOKING_REMINDER("Promemoria Prenotazione"),
    BOOKING_CANCELLED("Prenotazione Cancellata"),
    LESSON_COMPLETED("Lezione Completata"),
    NEW_REVIEW("Nuova Recensione"),
    PAYMENT_SUCCESS("Pagamento Riuscito"),
    PAYMENT_FAILED("Pagamento Fallito"),
    BADGE_EARNED("Badge Ottenuto"),
    REWARD_AVAILABLE("Ricompensa Disponibile"),
    INSTRUCTOR_MESSAGE("Messaggio Istruttore"),
    SYSTEM_UPDATE("Aggiornamento Sistema"),
    MARKETING("Marketing"),
    SECURITY("Sicurezza"),
    WELCOME("Benvenuto");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
