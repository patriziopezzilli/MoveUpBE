package com.moveup.service;

import com.moveup.model.Notification;
import com.moveup.model.NotificationPriority;
import com.moveup.model.User;
import com.moveup.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    // Create notification
    public Notification createNotification(String recipientId, String title, String message, String type) {
        Notification notification = new Notification(recipientId, title, message, 
            type);
        return notificationRepository.save(notification);
    }
    
    // Get notifications for user
    public List<Notification> getNotificationsForUser(String userId, int limit) {
        return notificationRepository.findRecentNotificationsByRecipient(userId, PageRequest.of(0, limit));
    }
    
    // Get unread notifications for user
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId);
    }
    
    // Mark notification as read
    public void markAsRead(String notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }
    
    // Mark all notifications as read for user
    public void markAllAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndIsReadFalse(userId);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        notificationRepository.saveAll(unreadNotifications);
    }
    
    // Count unread notifications
    public long countUnreadNotifications(String userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }
    
    // Send booking confirmation notification
    public void sendBookingConfirmationNotification(String userId, String bookingId) {
        Notification notification = new Notification();
        notification.setRecipientId(userId);
        notification.setTitle("Prenotazione Confermata");
        notification.setMessage("La tua prenotazione è stata confermata con successo!");
        notification.setType("BOOKING_CONFIRMATION");
        notification.setRelatedEntityId(bookingId);
        notification.setRelatedEntityType("BOOKING");
        
        notificationRepository.save(notification);
    }
    
    // Send booking reminder notification
    public void sendBookingReminderNotification(String userId, String bookingId, LocalDateTime lessonTime) {
        Notification notification = new Notification();
        notification.setRecipientId(userId);
        notification.setTitle("Promemoria Lezione");
        notification.setMessage("Non dimenticare la tua lezione oggi alle " + lessonTime.toLocalTime());
        notification.setType("BOOKING_REMINDER");
        notification.setRelatedEntityId(bookingId);
        notification.setRelatedEntityType("BOOKING");
        notification.setPriority(NotificationPriority.HIGH);
        
        notificationRepository.save(notification);
    }
    
    // Send booking cancellation notification
    public void sendBookingCancellationNotification(String userId, String bookingId) {
        Notification notification = new Notification();
        notification.setRecipientId(userId);
        notification.setTitle("Prenotazione Cancellata");
        notification.setMessage("La tua prenotazione è stata cancellata.");
        notification.setType("BOOKING_CANCELLED");
        notification.setRelatedEntityId(bookingId);
        notification.setRelatedEntityType("BOOKING");
        
        notificationRepository.save(notification);
    }
    
    // Send welcome notification to new users
    public void sendWelcomeNotification(User user) {
        Notification notification = new Notification();
        notification.setRecipientId(user.getId());
        notification.setTitle("Benvenuto in MoveUp!");
        notification.setMessage("Grazie per esserti registrato. Iniziamo il tuo viaggio sportivo!");
        notification.setType("WELCOME");
        notification.setPriority(NotificationPriority.HIGH);
        
        notificationRepository.save(notification);
    }
    
    // Send new review notification
    public void sendNewReviewNotification(String instructorId, String reviewId, int rating) {
        Notification notification = new Notification();
        notification.setRecipientId(instructorId);
        notification.setTitle("Nuova Recensione");
        notification.setMessage("Hai ricevuto una nuova recensione per la tua lezione.");
        notification.setType("NEW_REVIEW");
        notification.setRelatedEntityId(reviewId);
        notification.setRelatedEntityType("REVIEW");
        
        notificationRepository.save(notification);
    }
    
    // Send payment success notification
    public void sendPaymentSuccessNotification(String userId, String bookingId, double amount) {
        Notification notification = new Notification();
        notification.setRecipientId(userId);
        notification.setTitle("Pagamento Riuscito");
        notification.setMessage("Il pagamento per la tua prenotazione è stato elaborato con successo.");
        notification.setType("PAYMENT_SUCCESS");
        notification.setRelatedEntityId(bookingId);
        notification.setRelatedEntityType("BOOKING");
        
        notificationRepository.save(notification);
    }
    
    // Send payment failed notification
    public void sendPaymentFailedNotification(String userId, String bookingId) {
        Notification notification = new Notification();
        notification.setRecipientId(userId);
        notification.setTitle("Pagamento Fallito");
        notification.setMessage("Il pagamento per la tua prenotazione non è riuscito. Riprova o contatta il supporto.");
        notification.setType("PAYMENT_FAILED");
        notification.setRelatedEntityId(bookingId);
        notification.setRelatedEntityType("BOOKING");
        notification.setPriority(NotificationPriority.HIGH);
        
        notificationRepository.save(notification);
    }
    
    // Send badge earned notification
    public void sendBadgeEarnedNotification(String userId, String badgeId, String badgeTitle) {
        Notification notification = new Notification();
        notification.setRecipientId(userId);
        notification.setTitle("Badge Consegnato!");
        notification.setMessage("Hai guadagnato un nuovo badge: " + badgeTitle);
        notification.setType("BADGE_EARNED");
        notification.setRelatedEntityId(badgeId);
        notification.setRelatedEntityType("BADGE");
        
        notificationRepository.save(notification);
    }
    
    // Send reward available notification
    public void sendRewardAvailableNotification(String userId, String rewardId, String rewardTitle) {
        Notification notification = new Notification();
        notification.setRecipientId(userId);
        notification.setTitle("Ricompensa Disponibile");
        notification.setMessage("Hai una nuova ricompensa disponibile da riscattare!");
        notification.setType("REWARD_AVAILABLE");
        notification.setRelatedEntityId(rewardId);
        notification.setRelatedEntityType("REWARD");
        
        notificationRepository.save(notification);
    }
    
    // Send points earned notification
    public void sendPointsEarnedNotification(User user, int points, String reason) {
        Notification notification = new Notification();
        notification.setRecipientId(user.getId());
        notification.setTitle("Punti Guadagnati!");
        notification.setMessage(String.format("Hai guadagnato %d punti per: %s", points, reason));
        notification.setType("SYSTEM_UPDATE");
        
        notificationRepository.save(notification);
    }
    
    // Schedule notification
    public Notification scheduleNotification(String recipientId, String title, String message, 
                                           String type, LocalDateTime scheduledFor) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setScheduledFor(scheduledFor);
        
        return notificationRepository.save(notification);
    }
    
    // Get notifications ready to send
    public List<Notification> getNotificationsReadyToSend() {
        return notificationRepository.findNotificationsReadyToSend(LocalDateTime.now());
    }
    
    // Mark notification as sent
    public void markAsSent(String notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsSent();
            notificationRepository.save(notification);
        }
    }
    
    // Mark notification as failed
    public void markAsFailed(String notificationId, String error) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsFailed(error);
            notificationRepository.save(notification);
        }
    }
    
    // Get failed notifications for retry
    public List<Notification> getFailedNotificationsForRetry() {
        return notificationRepository.findFailedNotificationsForRetry();
    }
    
    // Delete old read notifications
    public void deleteOldReadNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<Notification> oldNotifications = notificationRepository.findOldReadNotifications(cutoffDate);
        notificationRepository.deleteAll(oldNotifications);
    }
    
    // Get notifications by type
    public List<Notification> getNotificationsByType(String userId, String type) {
        return notificationRepository.findByRecipientIdAndType(userId, type);
    }
    
    // Get notification statistics for user
    public NotificationStats getNotificationStats(String userId) {
        long totalNotifications = notificationRepository.countByRecipientId(userId);
        long unreadNotifications = notificationRepository.countByRecipientIdAndIsReadFalse(userId);
        
        NotificationStats stats = new NotificationStats();
        stats.setTotalNotifications(totalNotifications);
        stats.setUnreadNotifications(unreadNotifications);
        stats.setReadNotifications(totalNotifications - unreadNotifications);
        
        return stats;
    }
    
    /**
     * Notify instructor that customer has checked in via QR code
     */
    public void notifyInstructorCheckIn(String instructorId, String customerId, String bookingId) {
        Notification notification = new Notification();
        notification.setRecipientId(instructorId);
        notification.setTitle("Check-in Effettuato");
        notification.setMessage("Il cliente ha effettuato il check-in tramite QR code");
        notification.setType("BOOKING_CONFIRMATION");
        notification.setRelatedEntityId(bookingId);
        notification.setRelatedEntityType("BOOKING");
        notification.setPriority(NotificationPriority.HIGH);
        
        notificationRepository.save(notification);
    }
    
    // Get user notifications
    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }
    
    // Get unread notification count for user
    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientIdAndIsRead(userId, false);
    }
    
    // Delete notification
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    // Helper class for notification statistics
    public static class NotificationStats {
        private long totalNotifications;
        private long unreadNotifications;
        private long readNotifications;
        
        // Getters and setters
        public long getTotalNotifications() { return totalNotifications; }
        public void setTotalNotifications(long totalNotifications) { this.totalNotifications = totalNotifications; }
        
        public long getUnreadNotifications() { return unreadNotifications; }
        public void setUnreadNotifications(long unreadNotifications) { this.unreadNotifications = unreadNotifications; }
        
        public long getReadNotifications() { return readNotifications; }
        public void setReadNotifications(long readNotifications) { this.readNotifications = readNotifications; }
    }
}