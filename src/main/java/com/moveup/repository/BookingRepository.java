package com.moveup.repository;

import com.moveup.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    
    // Find by user
    List<Booking> findByUserId(String userId);
    
    // Find by instructor
    List<Booking> findByInstructorId(String instructorId);
    
    // Find by lesson
    List<Booking> findByLessonId(String lessonId);
    
    // Find by status
    List<Booking> findByStatus(String status);
    
    // Find by payment status
    List<Booking> findByPaymentStatus(String paymentStatus);
    
    // Find by user and status
    List<Booking> findByUserIdAndStatus(String userId, String status);
    
    // Find by instructor and status
    List<Booking> findByInstructorIdAndStatus(String instructorId, String status);
    
    // Find by scheduled date range
    List<Booking> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);
    
    // Find upcoming bookings for user
    @Query("{'userId': ?0, 'scheduledDate': {$gte: ?1}, 'status': {$in: ['PENDING', 'CONFIRMED']}}")
    List<Booking> findUpcomingBookingsByUserId(String userId, LocalDateTime fromDate);
    
    // Find upcoming bookings for instructor
    @Query("{'instructorId': ?0, 'scheduledDate': {$gte: ?1}, 'status': {$in: ['PENDING', 'CONFIRMED']}}")
    List<Booking> findUpcomingBookingsByInstructorId(String instructorId, LocalDateTime fromDate);
    
    // Find past bookings for user
    @Query("{'userId': ?0, 'scheduledDate': {$lt: ?1}}")
    List<Booking> findPastBookingsByUserId(String userId, LocalDateTime toDate);
    
    // Find past bookings for instructor
    @Query("{'instructorId': ?0, 'scheduledDate': {$lt: ?1}}")
    List<Booking> findPastBookingsByInstructorId(String instructorId, LocalDateTime toDate);
    
    // Find bookings by created date range
    List<Booking> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find conflicting bookings (same instructor, same date and time)
    @Query("{'instructorId': ?0, 'scheduledDate': ?1, 'scheduledTime': ?2, 'status': {$in: ['PENDING', 'CONFIRMED']}}")
    List<Booking> findConflictingBookings(String instructorId, LocalDateTime scheduledDate, LocalTime scheduledTime);
    
    // Find pending bookings
    @Query("{'status': 'PENDING'}")
    List<Booking> findPendingBookings();
    
    // Find confirmed bookings for today
    @Query("{'status': 'CONFIRMED', 'scheduledDate': {$gte: ?0, $lt: ?1}}")
    List<Booking> findTodayConfirmedBookings(LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    // Find bookings requiring payment
    @Query("{'paymentStatus': 'PENDING', 'status': 'CONFIRMED'}")
    List<Booking> findBookingsRequiringPayment();
    
    // Find cancelled bookings
    @Query("{'status': 'CANCELLED'}")
    List<Booking> findCancelledBookings();
    
    // Find completed bookings
    @Query("{'status': 'COMPLETED'}")
    List<Booking> findCompletedBookings();
    
    // Find by Stripe Payment Intent ID
    @Query("{'payment.stripePaymentIntentId': ?0}")
    Optional<Booking> findByStripePaymentIntentId(String paymentIntentId);
    
    // Count bookings by user
    long countByUserId(String userId);
    
    // Count bookings by instructor
    long countByInstructorId(String instructorId);
    
    // Count bookings by lesson
    long countByLessonId(String lessonId);
    
    // Count bookings by status
    long countByStatus(String status);
    
    // Count completed bookings by instructor
    @Query(value = "{'instructorId': ?0, 'status': 'COMPLETED'}", count = true)
    long countCompletedBookingsByInstructor(String instructorId);
    
    // Count completed bookings by user
    @Query(value = "{'userId': ?0, 'status': 'COMPLETED'}", count = true)
    long countCompletedBookingsByUser(String userId);
    
    // Find revenue by instructor in date range
    @Query("{'instructorId': ?0, 'status': 'COMPLETED', 'scheduledDate': {$gte: ?1, $lte: ?2}}")
    List<Booking> findRevenueBookings(String instructorId, LocalDateTime start, LocalDateTime end);
    
    // Find most recent booking by user
    @Query(value = "{'userId': ?0}", sort = "{ 'createdAt': -1 }")
    Optional<Booking> findMostRecentBookingByUser(String userId);
    
        // Find bookings with no-show status
    @Query("{'status': 'NO_SHOW'}")
    List<Booking> findNoShowBookings();
    
    // Find active booking for QR code validation
    @Query("{'userId': ?0, 'instructorId': ?1, 'scheduledDate': {$gte: ?2, $lt: ?3}, 'status': {$in: ?4}}")
    Optional<Booking> findFirstByUserIdAndInstructorIdAndScheduledDateBetweenAndStatusIn(
        String userId, String instructorId, LocalDateTime start, LocalDateTime end, List<String> statuses);
    
    // Find booking by Live Activity ID
    @Query("{'liveActivity.activityId': ?0}")
    Optional<Booking> findByLiveActivityActivityId(String activityId);
    
    // Find booking by Wallet Pass Serial Number
    @Query("{'walletPass.serialNumber': ?0}")
    Optional<Booking> findByWalletPassSerialNumber(String serialNumber);
    
    // Count bookings by user and status
    long countByUserIdAndStatus(String userId, Booking.BookingStatus status);
    
    // Find bookings by user and status ordered by created date desc
    List<Booking> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, Booking.BookingStatus status);
    
    // Find bookings by instructor created after date
    List<Booking> findByInstructorIdAndCreatedAtAfter(String instructorId, LocalDateTime date);
    
    // Find bookings by instructor created between dates
    List<Booking> findByInstructorIdAndCreatedAtBetween(String instructorId, LocalDateTime start, LocalDateTime end);
}
