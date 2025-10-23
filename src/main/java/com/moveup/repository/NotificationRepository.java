package com.moveup.repository;

import com.moveup.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    // Find by recipient
    List<Notification> findByRecipientId(String recipientId);
    
    // Find unread notifications by recipient
    @Query("{'recipientId': ?0, 'isRead': false}")
    List<Notification> findByRecipientIdAndIsReadFalse(String recipientId);
    
    // Find read notifications by recipient
    @Query("{'recipientId': ?0, 'isRead': true}")
    List<Notification> findByRecipientIdAndIsReadTrue(String recipientId);
    
    // Find notifications by type
    List<Notification> findByType(String type);
    
    // Find notifications by priority
    List<Notification> findByPriority(String priority);
    
    // Find notifications by recipient and type
    List<Notification> findByRecipientIdAndType(String recipientId, String type);
    
    // Find recent notifications for recipient
    @Query(value = "{'recipientId': ?0}", sort = "{ 'createdAt': -1 }")
    List<Notification> findRecentNotificationsByRecipient(String recipientId, org.springframework.data.domain.Pageable pageable);
    
    // Find notifications created between dates
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find scheduled notifications
    @Query("{'scheduledFor': {$exists: true, $gte: ?0}}")
    List<Notification> findScheduledNotifications(LocalDateTime currentTime);
    
    // Find notifications ready to be sent
    @Query("{ $and: [" +
           "{ $or: [" +
           "{'scheduledFor': {$exists: false}}, " +
           "{'scheduledFor': {$lte: ?0}} " +
           "] }, " +
           "{'delivery.isSent': false}, " +
           "{'delivery.isFailed': false} " +
           "] }")
    List<Notification> findNotificationsReadyToSend(LocalDateTime currentTime);
    
    // Find failed notifications that can be retried
    @Query("{ $and: [" +
           "{'delivery.isFailed': true}, " +
           "{'delivery.retryCount': {$lt: 3}} " +
           "] }")
    List<Notification> findFailedNotificationsForRetry();
    
    // Find notifications by related entity
    List<Notification> findByRelatedEntityIdAndRelatedEntityType(String entityId, String entityType);
    
    // Find urgent notifications
    @Query("{'priority': 'URGENT'}")
    List<Notification> findUrgentNotifications();
    
    // Find high priority notifications
    @Query("{'priority': 'HIGH'}")
    List<Notification> findHighPriorityNotifications();
    
    // Count unread notifications by recipient
    long countByRecipientIdAndIsReadFalse(String recipientId);
    
    // Count notifications by recipient
    long countByRecipientId(String recipientId);
    
    // Count notifications by type
    long countByType(String type);
    
    // Find booking-related notifications
    @Query("{'type': {$in: ['BOOKING_CONFIRMATION', 'BOOKING_REMINDER', 'BOOKING_CANCELLED']}}")
    List<Notification> findBookingRelatedNotifications();
    
    // Find payment-related notifications  
    @Query("{'type': {$in: ['PAYMENT_SUCCESS', 'PAYMENT_FAILED']}}")
    List<Notification> findPaymentRelatedNotifications();
    
    // Find gamification-related notifications
    @Query("{'type': {$in: ['BADGE_EARNED', 'REWARD_AVAILABLE']}}")
    List<Notification> findGamificationRelatedNotifications();
    
    // Find notifications sent via push
    @Query("{'delivery.pushSent': true}")
    List<Notification> findPushSentNotifications();
    
    // Find notifications sent via email
    @Query("{'delivery.emailSent': true}")
    List<Notification> findEmailSentNotifications();
    
    // Find notifications sent via SMS
    @Query("{'delivery.smsSent': true}")
    List<Notification> findSmsSentNotifications();
    
    // Find notifications with actions
    @Query("{'action.actionType': {$exists: true, $ne: null}}")
    List<Notification> findNotificationsWithActions();
    
    // Delete old read notifications
    @Query("{ $and: [" +
           "{'isRead': true}, " +
           "{'readAt': {$lt: ?0}} " +
           "] }")
    List<Notification> findOldReadNotifications(LocalDateTime cutoffDate);
    
    // Find notifications by recipient in date range
    List<Notification> findByRecipientIdAndCreatedAtBetween(String recipientId, LocalDateTime start, LocalDateTime end);
    
    // Get user notifications ordered by creation date (newest first)
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
    
    // Count unread notifications for user
    long countByRecipientIdAndIsRead(String recipientId, boolean isRead);
}