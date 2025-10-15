package com.moveup.service;

import com.moveup.model.Booking;
import com.moveup.model.User;
import com.moveup.model.Instructor;
import com.moveup.repository.BookingRepository;
import com.moveup.repository.UserRepository;
import com.moveup.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private EmailService emailService;
    
    // Create new booking
    public Booking createBooking(Booking booking) {
        // Verify no conflicts
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            booking.getInstructorId(), 
            booking.getScheduledDate(), 
            booking.getScheduledTime()
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Orario già prenotato");
        }
        
        // Verify user exists
        User user = userRepository.findById(booking.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        // Verify instructor exists
        Instructor instructor = instructorRepository.findById(booking.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Send confirmation notifications
        notificationService.sendBookingConfirmationNotification(user.getId(), savedBooking.getId());
        emailService.sendBookingConfirmationEmail(savedBooking, user, instructor);
        
        return savedBooking;
    }
    
    // Get booking by ID
    public Optional<Booking> getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }
    
    // Update booking (used by validation endpoint)
    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }
    
    // Get bookings for user
    public List<Booking> getBookingsForUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    // Get bookings for instructor
    public List<Booking> getBookingsForInstructor(String instructorId) {
        return bookingRepository.findByInstructorId(instructorId);
    }
    
    // Get upcoming bookings for user
    public List<Booking> getUpcomingBookingsForUser(String userId) {
        return bookingRepository.findUpcomingBookingsByUserId(userId, LocalDateTime.now());
    }
    
    // Get upcoming bookings for instructor
    public List<Booking> getUpcomingBookingsForInstructor(String instructorId) {
        return bookingRepository.findUpcomingBookingsByInstructorId(instructorId, LocalDateTime.now());
    }
    
    // Get past bookings for user
    public List<Booking> getPastBookingsForUser(String userId) {
        return bookingRepository.findPastBookingsByUserId(userId, LocalDateTime.now());
    }
    
    // Get past bookings for instructor
    public List<Booking> getPastBookingsForInstructor(String instructorId) {
        return bookingRepository.findPastBookingsByInstructorId(instructorId, LocalDateTime.now());
    }
    
    // Confirm booking
    public void confirmBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
        
        booking.confirm();
        bookingRepository.save(booking);
        
        // Process payment if needed
        if (booking.getPaymentStatus().name().equals("PENDING")) {
            // Payment processing logic would go here
        }
    }
    
    // Cancel booking
    public void cancelBooking(String bookingId, String cancelledBy, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
        
        if (!booking.canBeCancelled()) {
            throw new RuntimeException("Questa prenotazione non può essere cancellata");
        }
        
        booking.cancel(cancelledBy, reason);
        bookingRepository.save(booking);
        
        // Send cancellation notifications
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        Instructor instructor = instructorRepository.findById(booking.getInstructorId()).orElse(null);
        
        if (user != null && instructor != null) {
            notificationService.sendBookingCancellationNotification(user.getId(), bookingId);
            emailService.sendBookingCancellationEmail(booking, user, instructor);
        }
        
        // Process refund if applicable
        processRefundIfApplicable(booking);
    }
    
    // Complete booking
    public void completeBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
        
        booking.complete();
        bookingRepository.save(booking);
        
        // Add points to user for completed lesson
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null) {
            int points = calculatePointsForBooking(booking);
            user.addPoints(points);
            userRepository.save(user);
            
            notificationService.sendPointsEarnedNotification(user, points, "Lezione completata");
        }
    }
    
    // Mark as no-show
    public void markAsNoShow(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
        
        booking.setStatus(Booking.BookingStatus.NO_SHOW);
        bookingRepository.save(booking);
    }
    
    // Get today's confirmed bookings
    public List<Booking> getTodayConfirmedBookings() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return bookingRepository.findTodayConfirmedBookings(startOfDay, endOfDay);
    }
    
    // Get bookings requiring payment
    public List<Booking> getBookingsRequiringPayment() {
        return bookingRepository.findBookingsRequiringPayment();
    }
    
    // Process payment for booking
    public void processPayment(String bookingId, String paymentMethodId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
        
        try {
            // Process payment with Stripe
            String paymentIntentId = paymentService.processPayment(
                booking.getTotalAmount(),
                paymentMethodId,
                booking.getId()
            );
            
            booking.getPayment().setStripePaymentIntentId(paymentIntentId);
            booking.setPaymentStatus(Booking.PaymentStatus.AUTHORIZED);
            bookingRepository.save(booking);
            
            // Send payment success notification
            notificationService.sendPaymentSuccessNotification(
                booking.getUserId(),
                booking.getId(),
                booking.getTotalAmount()
            );
            
        } catch (Exception e) {
            // Handle payment failure
            booking.setPaymentStatus(Booking.PaymentStatus.FAILED);
            bookingRepository.save(booking);
            
            notificationService.sendPaymentFailedNotification(
                booking.getUserId(),
                booking.getId()
            );
            
            throw new RuntimeException("Errore nel processamento del pagamento: " + e.getMessage());
        }
    }
    
    // Get booking statistics for instructor
    public BookingStatistics getBookingStatisticsForInstructor(String instructorId) {
        BookingStatistics stats = new BookingStatistics();
        
        stats.setTotalBookings(bookingRepository.countByInstructorId(instructorId));
        stats.setCompletedBookings(bookingRepository.countCompletedBookingsByInstructor(instructorId));
        
        // Calculate revenue
        List<Booking> revenueBookings = bookingRepository.findRevenueBookings(
            instructorId, 
            LocalDateTime.now().minusMonths(12), 
            LocalDateTime.now()
        );
        
        double totalRevenue = revenueBookings.stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        
        stats.setTotalRevenue(totalRevenue);
        
        return stats;
    }
    
    // Get booking statistics for user
    public BookingStatistics getBookingStatisticsForUser(String userId) {
        BookingStatistics stats = new BookingStatistics();
        
        stats.setTotalBookings(bookingRepository.countByUserId(userId));
        stats.setCompletedBookings(bookingRepository.countCompletedBookingsByUser(userId));
        
        return stats;
    }
    
    // Helper methods
    private void processRefundIfApplicable(Booking booking) {
        if (booking.getPaymentStatus().name().equals("CAPTURED")) {
            // Process refund logic here
            // This would integrate with Stripe refunds API
        }
    }
    
    private int calculatePointsForBooking(Booking booking) {
        // Base points for completing a lesson
        int basePoints = 10;
        
        // Bonus points based on booking amount (1 point per 10 euros)
        int bonusPoints = (int) (booking.getTotalAmount() / 10);
        
        return basePoints + bonusPoints;
    }
    
    // Send booking reminders
    public void sendBookingReminders() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime startOfTomorrow = tomorrow.withHour(0).withMinute(0);
        LocalDateTime endOfTomorrow = tomorrow.withHour(23).withMinute(59);
        
        List<Booking> tomorrowBookings = bookingRepository.findByScheduledDateBetween(startOfTomorrow, endOfTomorrow);
        
        for (Booking booking : tomorrowBookings) {
            if (booking.getStatus().name().equals("CONFIRMED")) {
                notificationService.sendBookingReminderNotification(
                    booking.getUserId(),
                    booking.getId(),
                    booking.getScheduledDate()
                );
            }
        }
    }
    
    // Helper class for booking statistics
    public static class BookingStatistics {
        private long totalBookings;
        private long completedBookings;
        private long cancelledBookings;
        private double totalRevenue;
        private double averageBookingValue;
        
        // Getters and setters
        public long getTotalBookings() { return totalBookings; }
        public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
        
        public long getCompletedBookings() { return completedBookings; }
        public void setCompletedBookings(long completedBookings) { this.completedBookings = completedBookings; }
        
        public long getCancelledBookings() { return cancelledBookings; }
        public void setCancelledBookings(long cancelledBookings) { this.cancelledBookings = cancelledBookings; }
        
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public double getAverageBookingValue() { return averageBookingValue; }
        public void setAverageBookingValue(double averageBookingValue) { this.averageBookingValue = averageBookingValue; }
    }
}