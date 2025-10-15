package com.moveup.controller;

import com.moveup.model.Booking;
import com.moveup.service.BookingService;
import com.moveup.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private PaymentService paymentService;
    
    // Create new booking
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@Valid @RequestBody Booking booking) {
        try {
            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", "Prenotazione creata con successo",
                        "booking", createdBooking
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get booking by ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable String bookingId) {
        return bookingService.getBookingById(bookingId)
                .map(booking -> ResponseEntity.ok(booking))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get bookings for user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsForUser(@PathVariable String userId) {
        List<Booking> bookings = bookingService.getBookingsForUser(userId);
        return ResponseEntity.ok(bookings);
    }
    
    // Get bookings for instructor
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Booking>> getBookingsForInstructor(@PathVariable String instructorId) {
        List<Booking> bookings = bookingService.getBookingsForInstructor(instructorId);
        return ResponseEntity.ok(bookings);
    }
    
    // Get upcoming bookings for user
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<Booking>> getUpcomingBookingsForUser(@PathVariable String userId) {
        List<Booking> bookings = bookingService.getUpcomingBookingsForUser(userId);
        return ResponseEntity.ok(bookings);
    }
    
    // Get upcoming bookings for instructor
    @GetMapping("/instructor/{instructorId}/upcoming")
    public ResponseEntity<List<Booking>> getUpcomingBookingsForInstructor(@PathVariable String instructorId) {
        List<Booking> bookings = bookingService.getUpcomingBookingsForInstructor(instructorId);
        return ResponseEntity.ok(bookings);
    }
    
    // Get past bookings for user
    @GetMapping("/user/{userId}/past")
    public ResponseEntity<List<Booking>> getPastBookingsForUser(@PathVariable String userId) {
        List<Booking> bookings = bookingService.getPastBookingsForUser(userId);
        return ResponseEntity.ok(bookings);
    }
    
    // Get past bookings for instructor
    @GetMapping("/instructor/{instructorId}/past")
    public ResponseEntity<List<Booking>> getPastBookingsForInstructor(@PathVariable String instructorId) {
        List<Booking> bookings = bookingService.getPastBookingsForInstructor(instructorId);
        return ResponseEntity.ok(bookings);
    }
    
    // Confirm booking
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<Map<String, String>> confirmBooking(@PathVariable String bookingId) {
        try {
            bookingService.confirmBooking(bookingId);
            return ResponseEntity.ok(Map.of("message", "Prenotazione confermata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Cancel booking
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable String bookingId,
                                                           @RequestBody Map<String, String> request) {
        try {
            String cancelledBy = request.get("cancelledBy");
            String reason = request.get("reason");
            
            bookingService.cancelBooking(bookingId, cancelledBy, reason);
            return ResponseEntity.ok(Map.of("message", "Prenotazione cancellata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Complete booking
    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<Map<String, String>> completeBooking(@PathVariable String bookingId) {
        try {
            bookingService.completeBooking(bookingId);
            return ResponseEntity.ok(Map.of("message", "Prenotazione completata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Mark as no-show
    @PostMapping("/{bookingId}/no-show")
    public ResponseEntity<Map<String, String>> markAsNoShow(@PathVariable String bookingId) {
        try {
            bookingService.markAsNoShow(bookingId);
            return ResponseEntity.ok(Map.of("message", "Prenotazione segnata come mancata presenza"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Process payment for booking
    @PostMapping("/{bookingId}/payment")
    public ResponseEntity<Map<String, String>> processPayment(@PathVariable String bookingId,
                                                            @RequestBody Map<String, String> request) {
        try {
            String paymentMethodId = request.get("paymentMethodId");
            bookingService.processPayment(bookingId, paymentMethodId);
            
            return ResponseEntity.ok(Map.of("message", "Pagamento processato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get booking statistics for instructor
    @GetMapping("/instructor/{instructorId}/stats")
    public ResponseEntity<BookingService.BookingStatistics> getInstructorBookingStats(@PathVariable String instructorId) {
        BookingService.BookingStatistics stats = bookingService.getBookingStatisticsForInstructor(instructorId);
        return ResponseEntity.ok(stats);
    }
    
    // Get booking statistics for user
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<BookingService.BookingStatistics> getUserBookingStats(@PathVariable String userId) {
        BookingService.BookingStatistics stats = bookingService.getBookingStatisticsForUser(userId);
        return ResponseEntity.ok(stats);
    }
    
    // NEW: Validate lesson with QR code (captures payment and transfers to trainer)
    @PostMapping("/{bookingId}/validate")
    public ResponseEntity<Map<String, Object>> validateLesson(
            @PathVariable String bookingId,
            @RequestBody Map<String, String> request) {
        try {
            // Get QR code data
            String qrCodeData = request.get("qrCodeData");
            String scannedBy = request.get("scannedBy"); // Customer ID
            
            // Validate QR format: should contain bookingId and trainerId
            // Format: "MOVEUP:BOOKING:{bookingId}:TRAINER:{trainerId}"
            if (qrCodeData == null || !qrCodeData.startsWith("MOVEUP:BOOKING:")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "QR code non valido"));
            }
            
            // Parse QR data
            String[] parts = qrCodeData.split(":");
            if (parts.length < 5) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Formato QR code non valido"));
            }
            
            String qrBookingId = parts[2];
            String qrTrainerId = parts[4];
            
            // Verify booking ID matches
            if (!qrBookingId.equals(bookingId)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "QR code non corrisponde alla prenotazione"));
            }
            
            // Get booking
            Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
            
            // Verify trainer ID matches
            if (!booking.getInstructorId().equals(qrTrainerId)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "QR code non corrisponde all'istruttore"));
            }
            
            // Verify customer matches
            if (!booking.getUserId().equals(scannedBy)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Non sei autorizzato a validare questa prenotazione"));
            }
            
            // Verify payment is authorized
            if (booking.getPaymentIntentId() == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Pagamento non autorizzato"));
            }
            
            // Verify not already validated
            if (booking.isValidated()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lezione già validata"));
            }
            
            // Process payment: capture + transfer + credit wallet
            Map<String, Object> paymentResult = paymentService.processLessonPayment(booking);
            
            // Update booking status
            booking.complete();
            bookingService.updateBooking(booking);
            
            // Return success with payment details
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lezione validata con successo! Pagamento completato.",
                "booking", booking,
                "payment", paymentResult
            ));
            
        } catch (com.stripe.exception.StripeException e) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(Map.of("error", "Errore nel processare il pagamento: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Errore del server: " + e.getMessage()));
        }
    }
    
    // Admin endpoints
    
    // Get today's confirmed bookings (admin/instructor)
    @GetMapping("/today/confirmed")
    public ResponseEntity<List<Booking>> getTodayConfirmedBookings() {
        List<Booking> bookings = bookingService.getTodayConfirmedBookings();
        return ResponseEntity.ok(bookings);
    }
    
    // Get bookings requiring payment (admin)
    @GetMapping("/requiring-payment")
    public ResponseEntity<List<Booking>> getBookingsRequiringPayment() {
        List<Booking> bookings = bookingService.getBookingsRequiringPayment();
        return ResponseEntity.ok(bookings);
    }
    
    // Send booking reminders (admin/system)
    @PostMapping("/send-reminders")
    public ResponseEntity<Map<String, String>> sendBookingReminders() {
        try {
            bookingService.sendBookingReminders();
            return ResponseEntity.ok(Map.of("message", "Promemoria inviati con successo"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore nell'invio dei promemoria"));
        }
    }
}