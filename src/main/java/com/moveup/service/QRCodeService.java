package com.moveup.service;

import com.moveup.dto.QRCodeValidationRequest;
import com.moveup.dto.QRCodeValidationResponse;
import com.moveup.exception.QRCodeException;
import com.moveup.model.Booking;
import com.moveup.model.Instructor;
import com.moveup.model.Lesson;
import com.moveup.model.User;
import com.moveup.repository.BookingRepository;
import com.moveup.repository.InstructorRepository;
import com.moveup.repository.LessonRepository;
import com.moveup.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Service for QR Code validation and check-in processing
 * Implements the 6-step validation logic from BACKEND_APPLE_WALLET_INTEGRATION.md
 */
@Service
public class QRCodeService {
    
    private static final Logger logger = LoggerFactory.getLogger(QRCodeService.class);
    
    private static final long QR_MAX_AGE_SECONDS = 300; // 5 minutes
    private static final long CHECK_IN_WINDOW_MINUTES = 15; // ±15 minutes
    private static final double MAX_DISTANCE_METERS = 1000; // 1km warning threshold
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StripeService stripeService;
    
    @Autowired
    private LiveActivityService liveActivityService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Validates QR code and processes check-in with payment
     * 
     * 6-Step Validation Process:
     * 1. Validate QR timestamp (max 5 minutes old)
     * 2. Find active booking (userId + instructorId + today)
     * 3. Check time window (±15 minutes from lesson start)
     * 4. Check location proximity (max 1km, warning only)
     * 5. Prevent duplicate check-in
     * 6. Process check-in + payment + Live Activity update + notify instructor
     */
    @Transactional
    public QRCodeValidationResponse validateAndCheckIn(QRCodeValidationRequest request) {
        try {
            // Step 1: Validate QR timestamp (max 5 minutes old)
            validateQRTimestamp(request.getQrData());
            
            // Step 2: Find active booking
            Booking booking = findActiveBooking(
                request.getUserId(),
                request.getQrData().getInstructorId()
            );
            
            // Step 3: Check time window (±15 minutes)
            validateTimeWindow(booking);
            
            // Step 4: Check location proximity (warning only)
            double distance = 0;
            if (request.getLocation() != null) {
                distance = checkLocationProximity(booking, request.getLocation());
            }
            
            // Step 5: Prevent duplicate check-in
            preventDuplicateCheckIn(booking);
            
            // Step 6: Process check-in
            processCheckIn(booking, request.getLocation(), distance);
            
            // Load additional data for response
            Lesson lesson = lessonRepository.findById(booking.getLessonId()).orElse(null);
            Instructor instructor = instructorRepository.findById(booking.getInstructorId()).orElse(null);
            User instructorUser = instructor != null ? 
                userRepository.findById(instructor.getUserId()).orElse(null) : null;
            
            // Build success response
            return buildSuccessResponse(booking, lesson, instructorUser, distance);
            
        } catch (QRCodeException e) {
            logger.warn("QR code validation failed: {}", e.getMessage());
            return new QRCodeValidationResponse(false, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during QR validation", e);
            return new QRCodeValidationResponse(false, "Si è verificato un errore. Riprova.");
        }
    }
    
    /**
     * Step 1: Validate QR code timestamp
     */
    private void validateQRTimestamp(QRCodeValidationRequest.QRData qrData) {
        long qrTimestamp = qrData.getTimestamp();
        long currentTimestamp = System.currentTimeMillis() / 1000;
        long qrAge = currentTimestamp - qrTimestamp;
        
        if (qrAge > QR_MAX_AGE_SECONDS) {
            throw new QRCodeException("QR_CODE_EXPIRED", 
                "Il codice QR è scaduto. Richiedi all'istruttore di mostrare un nuovo codice.");
        }
        
        // Also check for future timestamps (clock skew)
        if (qrAge < -60) { // Allow 1 minute of clock skew
            throw new QRCodeException("QR_CODE_INVALID", 
                "Il codice QR non è valido. Verifica che l'ora del dispositivo sia corretta.");
        }
        
        logger.info("QR timestamp validated. Age: {} seconds", qrAge);
    }
    
    /**
     * Step 2: Find active booking for today
     */
    private Booking findActiveBooking(String userId, String instructorId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        
        Optional<Booking> bookingOpt = bookingRepository
            .findFirstByUserIdAndInstructorIdAndScheduledDateBetweenAndStatusIn(
                userId,
                instructorId,
                startOfDay,
                endOfDay,
                java.util.Arrays.asList("CONFIRMED", "PENDING")
            );
        
        if (bookingOpt.isEmpty()) {
            throw new QRCodeException("NO_BOOKING_FOUND",
                "Nessuna prenotazione trovata per oggi con questo istruttore.");
        }
        
        logger.info("Found booking: {} for user: {} with instructor: {}", 
            bookingOpt.get().getId(), userId, instructorId);
        
        return bookingOpt.get();
    }
    
    /**
     * Step 3: Validate time window (±15 minutes from lesson start)
     */
    private void validateTimeWindow(Booking booking) {
        LocalDateTime lessonTime = booking.getScheduledDate();
        LocalDateTime now = LocalDateTime.now();
        
        long minutesDiff = Math.abs(ChronoUnit.MINUTES.between(now, lessonTime));
        
        if (minutesDiff > CHECK_IN_WINDOW_MINUTES) {
            String message;
            if (now.isBefore(lessonTime)) {
                message = String.format("È troppo presto per il check-in. La lezione inizia tra %d minuti.", 
                    ChronoUnit.MINUTES.between(now, lessonTime));
            } else {
                message = String.format("È troppo tardi per il check-in. La lezione è iniziata %d minuti fa.", 
                    ChronoUnit.MINUTES.between(lessonTime, now));
            }
            throw new QRCodeException("OUTSIDE_CHECKIN_WINDOW", message);
        }
        
        logger.info("Time window validated. Lesson time: {}, Current time: {}, Diff: {} minutes", 
            lessonTime, now, minutesDiff);
    }
    
    /**
     * Step 4: Check location proximity (warning only, max 1km)
     */
    private double checkLocationProximity(Booking booking, QRCodeValidationRequest.LocationData userLocation) {
        // Load lesson to get location
        Lesson lesson = lessonRepository.findById(booking.getLessonId()).orElse(null);
        if (lesson == null || lesson.getLocation() == null) {
            logger.warn("Cannot validate location: lesson or location not found");
            return 0;
        }
        
        double lessonLat = lesson.getLocation().getLatitude();
        double lessonLon = lesson.getLocation().getLongitude();
        double userLat = userLocation.getLatitude();
        double userLon = userLocation.getLongitude();
        
        // Calculate distance using Haversine formula
        double distance = calculateDistance(lessonLat, lessonLon, userLat, userLon);
        
        if (distance > MAX_DISTANCE_METERS) {
            logger.warn("User is {} meters from lesson location (> {}m)", distance, MAX_DISTANCE_METERS);
            // Warning only, don't block check-in
        } else {
            logger.info("Location validated. Distance: {} meters", distance);
        }
        
        return distance;
    }
    
    /**
     * Step 5: Prevent duplicate check-in
     */
    private void preventDuplicateCheckIn(Booking booking) {
        if (booking.getCheckin() != null && booking.getCheckin().getCheckedInAt() != null) {
            throw new QRCodeException("ALREADY_CHECKED_IN",
                "Check-in già effettuato per questa lezione.");
        }
        
        logger.info("No duplicate check-in found for booking: {}", booking.getId());
    }
    
    /**
     * Step 6: Process check-in, payment, Live Activity update, and notifications
     */
    private void processCheckIn(Booking booking, QRCodeValidationRequest.LocationData location, double distance) {
        LocalDateTime now = LocalDateTime.now();
        
        // Update booking with check-in info
        booking.setCheckin(createCheckInInfo(now, location, distance));
        booking.setStatus("IN_PROGRESS");
        booking.setValidatedAt(now);
        
        // Process payment (capture authorized amount)
        if (booking.getPaymentIntentId() != null) {
            try {
                String transferId = stripeService.capturePaymentAndTransfer(
                    booking.getPaymentIntentId(),
                    booking.getInstructorId(),
                    booking.getTotalAmount()
                );
                booking.setStripeTransferId(transferId);
                booking.setPaymentStatus("CAPTURED");
                logger.info("Payment captured and transferred. Transfer ID: {}", transferId);
            } catch (Exception e) {
                logger.error("Payment capture failed for booking: {}", booking.getId(), e);
                // Don't block check-in if payment fails, log for manual review
            }
        }
        
        // Save booking
        bookingRepository.save(booking);
        logger.info("Check-in processed for booking: {}", booking.getId());
        
        // Update Live Activity status to "inProgress"
        if (booking.getLiveActivity() != null && booking.getLiveActivity().getActivityId() != null) {
            try {
                liveActivityService.updateActivity(
                    booking.getLiveActivity().getActivityId(),
                    "inProgress",
                    null
                );
                logger.info("Live Activity updated to inProgress");
            } catch (Exception e) {
                logger.error("Failed to update Live Activity", e);
            }
        }
        
        // Notify instructor
        try {
            notificationService.notifyInstructorCheckIn(
                booking.getInstructorId(),
                booking.getUserId(),
                booking.getId()
            );
            logger.info("Instructor notification sent");
        } catch (Exception e) {
            logger.error("Failed to send instructor notification", e);
        }
        
        // Update instructor QR scan stats
        try {
            Instructor instructor = instructorRepository.findById(booking.getInstructorId()).orElse(null);
            if (instructor != null && instructor.getQrPass() != null) {
                instructor.getQrPass().recordScan();
                instructorRepository.save(instructor);
            }
        } catch (Exception e) {
            logger.error("Failed to update instructor QR stats", e);
        }
    }
    
    /**
     * Create CheckInInfo object
     */
    private Object createCheckInInfo(LocalDateTime checkedInAt, 
                                    QRCodeValidationRequest.LocationData location, 
                                    double distance) {
        // This would create the CheckInInfo embedded object
        // For now, returning a placeholder - full implementation would use reflection or builder
        return new Object() {
            public LocalDateTime getCheckedInAt() { return checkedInAt; }
            public boolean isScannedQR() { return true; }
            public double getDistance() { return distance; }
        };
    }
    
    /**
     * Build success response
     */
    private QRCodeValidationResponse buildSuccessResponse(
            Booking booking, 
            Lesson lesson, 
            User instructorUser,
            double distance) {
        
        QRCodeValidationResponse response = new QRCodeValidationResponse(
            true,
            "Check-in effettuato con successo!"
        );
        response.setBookingId(booking.getId());
        
        // Check-in details
        QRCodeValidationResponse.CheckInDetails checkInDetails = 
            new QRCodeValidationResponse.CheckInDetails();
        checkInDetails.setLessonTitle(lesson != null ? lesson.getTitle() : "Lezione");
        checkInDetails.setInstructorName(instructorUser != null ? 
            instructorUser.getFirstName() + " " + instructorUser.getLastName() : "Istruttore");
        checkInDetails.setCheckedInAt(booking.getValidatedAt().toString());
        checkInDetails.setDistance(distance);
        response.setCheckInDetails(checkInDetails);
        
        // Payment details
        QRCodeValidationResponse.PaymentDetails paymentDetails = 
            new QRCodeValidationResponse.PaymentDetails();
        paymentDetails.setPaymentProcessed(booking.getPaymentStatus().equals("CAPTURED"));
        paymentDetails.setAmount(booking.getTotalAmount());
        paymentDetails.setCurrency("EUR");
        response.setPaymentDetails(paymentDetails);
        
        return response;
    }
    
    /**
     * Calculate distance between two coordinates using Haversine formula
     * Returns distance in meters
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000; // meters
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
}
