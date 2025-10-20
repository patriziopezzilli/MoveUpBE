package com.moveup.service;

import com.moveup.dto.LiveActivityStartRequest;
import com.moveup.dto.LiveActivityUpdateRequest;
import com.moveup.model.Booking;
import com.moveup.repository.BookingRepository;
import com.eatthepath.pushy.apns.*;
import com.eatthepath.pushy.apns.util.*;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing Live Activity push notifications via APNs
 * Sends real-time updates to iOS Live Activities (Dynamic Island + Lock Screen)
 */
@Service
public class LiveActivityService {
    
    private static final Logger logger = LoggerFactory.getLogger(LiveActivityService.class);
    
    @Value("${apple.apns.key.path:}")
    private String apnsKeyPath;
    
    @Value("${apple.apns.key.id:}")
    private String apnsKeyId;
    
    @Value("${apple.apns.team.id:}")
    private String teamId;
    
    @Value("${apple.apns.topic:com.moveup.app}")
    private String apnsTopic;
    
    @Value("${apple.apns.production:false}")
    private boolean isProduction;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    private ApnsClient apnsClient;
    
    /**
     * Initialize APNs client (called after bean construction)
     */
    public void initializeApnsClient() {
        if (apnsKeyPath == null || apnsKeyPath.isEmpty()) {
            logger.warn("APNs key path not configured. Live Activity push notifications will not work.");
            return;
        }
        
        try {
            File keyFile = new File(apnsKeyPath);
            
            ApnsClient client = new ApnsClientBuilder()
                .setApnsServer(isProduction ? 
                    ApnsClientBuilder.PRODUCTION_APNS_HOST : 
                    ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                .setSigningKey(ApnsSigningKey.loadFromPkcs8File(
                    keyFile, 
                    teamId, 
                    apnsKeyId))
                .build();
            
            this.apnsClient = client;
            logger.info("APNs client initialized successfully. Environment: {}", 
                isProduction ? "Production" : "Development");
                
        } catch (Exception e) {
            logger.error("Failed to initialize APNs client", e);
        }
    }
    
    /**
     * Start a new Live Activity
     */
    public String startActivity(LiveActivityStartRequest request) {
        try {
            Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Generate unique activity ID
            String activityId = "LA-" + UUID.randomUUID().toString();
            
            // Create LiveActivityInfo object
            booking.setLiveActivity(createLiveActivityInfo(
                activityId, 
                request.getPushToken(), 
                "active"
            ));
            
            bookingRepository.save(booking);
            
            logger.info("Live Activity started: {} for booking: {}", activityId, booking.getId());
            
            // Send initial push notification
            sendActivityUpdate(request.getPushToken(), booking, "upcoming", null);
            
            return activityId;
            
        } catch (Exception e) {
            logger.error("Failed to start Live Activity", e);
            throw new RuntimeException("Failed to start Live Activity: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing Live Activity
     */
    public void updateActivity(String activityId, String status, LiveActivityUpdateRequest.InstructorLocation location) {
        try {
            Booking booking = bookingRepository.findByLiveActivityActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("Live Activity not found"));
            
            if (booking.getLiveActivity() == null) {
                throw new RuntimeException("Live Activity not initialized");
            }
            
            String pushToken = booking.getLiveActivity().getPushToken();
            
            // Update status
            booking.getLiveActivity().setStatus(status);
            booking.getLiveActivity().setLastUpdate(LocalDateTime.now());
            bookingRepository.save(booking);
            
            // Send push notification
            sendActivityUpdate(pushToken, booking, status, location);
            
            logger.info("Live Activity updated: {} - Status: {}", activityId, status);
            
        } catch (Exception e) {
            logger.error("Failed to update Live Activity: {}", activityId, e);
            throw new RuntimeException("Failed to update Live Activity: " + e.getMessage());
        }
    }
    
    /**
     * End a Live Activity
     */
    public void endActivity(String activityId, String dismissalPolicy) {
        try {
            Booking booking = bookingRepository.findByLiveActivityActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("Live Activity not found"));
            
            if (booking.getLiveActivity() == null) {
                return; // Already ended
            }
            
            String pushToken = booking.getLiveActivity().getPushToken();
            
            // Send final update
            sendActivityEnd(pushToken, booking, dismissalPolicy);
            
            // Update status to ended
            booking.getLiveActivity().setStatus("ended");
            booking.getLiveActivity().setLastUpdate(LocalDateTime.now());
            bookingRepository.save(booking);
            
            logger.info("Live Activity ended: {} - Dismissal: {}", activityId, dismissalPolicy);
            
        } catch (Exception e) {
            logger.error("Failed to end Live Activity: {}", activityId, e);
        }
    }
    
    /**
     * Send activity update push notification
     */
    private void sendActivityUpdate(String pushToken, Booking booking, String status, 
                                    LiveActivityUpdateRequest.InstructorLocation location) {
        if (apnsClient == null) {
            logger.warn("APNs client not initialized. Skipping push notification.");
            return;
        }
        
        try {
            // Build APNs payload for Live Activity
            String payload = buildActivityPayload(booking, status, location, false);
            
            SimpleApnsPushNotification pushNotification = 
                new SimpleApnsPushNotification(
                    pushToken,
                    apnsTopic,
                    payload,
                    null,
                    DeliveryPriority.IMMEDIATE,
                    PushType.ALERT
                );
            
            CompletableFuture<PushNotificationResponse<SimpleApnsPushNotification>> sendFuture = 
                apnsClient.sendNotification(pushNotification);
            
            sendFuture.whenComplete((response, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to send Live Activity push", throwable);
                } else if (response.isAccepted()) {
                    logger.info("Live Activity push sent successfully");
                } else {
                    logger.error("Live Activity push rejected: {}", response.getRejectionReason());
                }
            });
            
        } catch (Exception e) {
            logger.error("Error sending Live Activity push", e);
        }
    }
    
    /**
     * Send activity end push notification
     */
    private void sendActivityEnd(String pushToken, Booking booking, String dismissalPolicy) {
        if (apnsClient == null) {
            logger.warn("APNs client not initialized. Skipping push notification.");
            return;
        }
        
        try {
            String payload = buildActivityPayload(booking, "completed", null, true);
            
            SimpleApnsPushNotification pushNotification = 
                new SimpleApnsPushNotification(
                    pushToken,
                    apnsTopic,
                    payload,
                    null,
                    DeliveryPriority.IMMEDIATE,
                    PushType.ALERT
                );
            
            apnsClient.sendNotification(pushNotification);
            logger.info("Live Activity end push sent");
            
        } catch (Exception e) {
            logger.error("Error sending Live Activity end push", e);
        }
    }
    
    /**
     * Build APNs JSON payload for Live Activity
     */
    private String buildActivityPayload(Booking booking, String status, 
                                       LiveActivityUpdateRequest.InstructorLocation location,
                                       boolean isEnd) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"aps\": {");
        json.append("\"timestamp\": ").append(System.currentTimeMillis() / 1000).append(",");
        json.append("\"event\": \"").append(isEnd ? "end" : "update").append("\",");
        
        if (isEnd) {
            json.append("\"dismissal-date\": ").append(System.currentTimeMillis() / 1000 + 14400); // 4 hours
        } else {
            json.append("\"content-state\": {");
            json.append("\"lessonStartTime\": \"").append(booking.getScheduledDate().toString()).append("\",");
            json.append("\"currentTime\": \"").append(LocalDateTime.now().toString()).append("\",");
            json.append("\"status\": \"").append(status).append("\"");
            
            if (location != null) {
                json.append(",\"instructorLocation\": {");
                json.append("\"latitude\": ").append(location.getLatitude()).append(",");
                json.append("\"longitude\": ").append(location.getLongitude()).append(",");
                json.append("\"distanceFromLesson\": ").append(location.getDistanceFromLesson());
                json.append("}");
            }
            
            json.append("}");
        }
        
        json.append("}");
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Create LiveActivityInfo embedded object
     */
    private Booking.LiveActivityInfo createLiveActivityInfo(String activityId, String pushToken, String status) {
        Booking.LiveActivityInfo liveActivity = new Booking.LiveActivityInfo();
        liveActivity.setActivityId(activityId);
        liveActivity.setPushToken(pushToken);
        liveActivity.setStatus(status);
        liveActivity.setStartedAt(LocalDateTime.now());
        liveActivity.setLastUpdate(LocalDateTime.now());
        return liveActivity;
    }
}
