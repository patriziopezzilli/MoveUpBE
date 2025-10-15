package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    @NotNull
    @Indexed
    private String recipientId;
    
    @NotNull
    @Size(min = 1, max = 200)
    private String title;
    
    @Size(max = 1000)
    private String message;
    
    @NotNull
    private NotificationType type;
    
    @NotNull
    private NotificationPriority priority = NotificationPriority.MEDIUM;
    
    private String relatedEntityId;
    private String relatedEntityType;
    
    private NotificationAction action = new NotificationAction();
    private NotificationDelivery delivery = new NotificationDelivery();
    
    @Indexed
    private boolean isRead = false;
    
    private LocalDateTime readAt;
    private LocalDateTime scheduledFor;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public Notification() {}
    
    public Notification(String recipientId, String title, String message, NotificationType type) {
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    
    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }
    
    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    
    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }
    
    public NotificationAction getAction() { return action; }
    public void setAction(NotificationAction action) { this.action = action; }
    
    public NotificationDelivery getDelivery() { return delivery; }
    public void setDelivery(NotificationDelivery delivery) { this.delivery = delivery; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public LocalDateTime getScheduledFor() { return scheduledFor; }
    public void setScheduledFor(LocalDateTime scheduledFor) { this.scheduledFor = scheduledFor; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    public boolean isScheduled() {
        return scheduledFor != null && LocalDateTime.now().isBefore(scheduledFor);
    }
    
    public boolean shouldBeSent() {
        return !isScheduled() && !delivery.isSent();
    }
    
    public void markAsSent() {
        delivery.setSent(true);
        delivery.setSentAt(LocalDateTime.now());
    }
    
    public void markAsFailed(String error) {
        delivery.setFailed(true);
        delivery.setFailureReason(error);
        delivery.setFailedAt(LocalDateTime.now());
    }
}

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
    SECURITY("Sicurezza");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

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

// Embedded classes
class NotificationAction {
    private String actionType;
    private String actionUrl;
    private String actionText = "Visualizza";
    private Map<String, String> actionData;
    
    // Constructors
    public NotificationAction() {}
    
    // Getters and Setters
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
    
    public String getActionText() { return actionText; }
    public void setActionText(String actionText) { this.actionText = actionText; }
    
    public Map<String, String> getActionData() { return actionData; }
    public void setActionData(Map<String, String> actionData) { this.actionData = actionData; }
}

class NotificationDelivery {
    private boolean pushSent = false;
    private boolean emailSent = false;
    private boolean smsSent = false;
    private boolean inAppSent = false;
    
    private LocalDateTime sentAt;
    private boolean isSent = false;
    private boolean isFailed = false;
    private String failureReason;
    private LocalDateTime failedAt;
    private int retryCount = 0;
    
    // Constructors
    public NotificationDelivery() {}
    
    // Getters and Setters
    public boolean isPushSent() { return pushSent; }
    public void setPushSent(boolean pushSent) { this.pushSent = pushSent; }
    
    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }
    
    public boolean isSmsSent() { return smsSent; }
    public void setSmsSent(boolean smsSent) { this.smsSent = smsSent; }
    
    public boolean isInAppSent() { return inAppSent; }
    public void setInAppSent(boolean inAppSent) { this.inAppSent = inAppSent; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public boolean isSent() { return isSent; }
    public void setSent(boolean sent) { isSent = sent; }
    
    public boolean isFailed() { return isFailed; }
    public void setFailed(boolean failed) { isFailed = failed; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public LocalDateTime getFailedAt() { return failedAt; }
    public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    
    // Business methods
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    public boolean canRetry() {
        return retryCount < 3; // Max 3 retry attempts
    }
}