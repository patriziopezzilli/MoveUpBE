package com.moveup.controller;

import com.moveup.dto.*;
import com.moveup.service.QRCodeService;
import com.moveup.service.LiveActivityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Apple Wallet Integration Features
 * Handles QR Code validation, Live Activity push notifications, and Wallet Pass generation
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Apple Wallet Integration", description = "QR Code, Live Activity, and Apple Wallet Pass APIs")
public class AppleWalletController {
    
    private static final Logger logger = LoggerFactory.getLogger(AppleWalletController.class);
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private LiveActivityService liveActivityService;
    
    /**
     * POST /api/v1/qr/validate/checkin
     * Validate QR code and process check-in
     */
    @PostMapping("/qr/validate/checkin")
    @Operation(summary = "Validate QR code and check-in", 
               description = "Validates instructor QR code, processes check-in, captures payment, and updates Live Activity")
    public ResponseEntity<QRCodeValidationResponse> validateCheckIn(
            @Valid @RequestBody QRCodeValidationRequest request) {
        
        logger.info("QR validation request received for user: {} with instructor: {}", 
            request.getUserId(), request.getQrData().getInstructorId());
        
        try {
            QRCodeValidationResponse response = qrCodeService.validateAndCheckIn(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (Exception e) {
            logger.error("QR validation failed", e);
            QRCodeValidationResponse errorResponse = new QRCodeValidationResponse(
                false, 
                "Si è verificato un errore durante la validazione. Riprova."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * POST /api/v1/live-activity/start
     * Start a new Live Activity for a booking
     */
    @PostMapping("/live-activity/start")
    @Operation(summary = "Start Live Activity", 
               description = "Registers a new Live Activity with push token for real-time updates")
    public ResponseEntity<Map<String, Object>> startLiveActivity(
            @Valid @RequestBody LiveActivityStartRequest request) {
        
        logger.info("Live Activity start request for booking: {}", request.getBookingId());
        
        try {
            String activityId = liveActivityService.startActivity(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("activityId", activityId);
            response.put("message", "Live Activity avviata con successo");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to start Live Activity", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Errore nell'avvio della Live Activity: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * POST /api/v1/live-activity/{activityId}/update
     * Update an existing Live Activity
     */
    @PostMapping("/live-activity/{activityId}/update")
    @Operation(summary = "Update Live Activity", 
               description = "Sends APNs push notification to update Live Activity status and content")
    public ResponseEntity<Map<String, Object>> updateLiveActivity(
            @PathVariable String activityId,
            @Valid @RequestBody LiveActivityUpdateRequest request) {
        
        logger.info("Live Activity update request for: {} - Status: {}", activityId, request.getStatus());
        
        try {
            liveActivityService.updateActivity(
                activityId, 
                request.getStatus(), 
                request.getInstructorLocation()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Live Activity aggiornata");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to update Live Activity: {}", activityId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Errore nell'aggiornamento: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * POST /api/v1/live-activity/{activityId}/end
     * End a Live Activity
     */
    @PostMapping("/live-activity/{activityId}/end")
    @Operation(summary = "End Live Activity", 
               description = "Sends final APNs push to end Live Activity with dismissal policy")
    public ResponseEntity<Map<String, Object>> endLiveActivity(
            @PathVariable String activityId,
            @RequestParam(defaultValue = "default") String dismissalPolicy) {
        
        logger.info("Live Activity end request for: {} - Dismissal: {}", activityId, dismissalPolicy);
        
        try {
            liveActivityService.endActivity(activityId, dismissalPolicy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Live Activity terminata");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to end Live Activity: {}", activityId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Errore nella chiusura: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /api/v1/qr/health
     * Health check endpoint for QR service
     */
    @GetMapping("/qr/health")
    @Operation(summary = "QR Service Health Check")
    public ResponseEntity<Map<String, String>> qrHealthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "QR Code Validation");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/live-activity/health
     * Health check endpoint for Live Activity service
     */
    @GetMapping("/live-activity/health")
    @Operation(summary = "Live Activity Service Health Check")
    public ResponseEntity<Map<String, String>> liveActivityHealthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "Live Activity Push");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}
