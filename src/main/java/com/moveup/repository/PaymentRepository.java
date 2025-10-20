package com.moveup.repository;

import com.moveup.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    
    // Find payments by user ID
    List<Payment> findByUserId(String userId);
    
    // Find payments by booking ID
    List<Payment> findByBookingId(String bookingId);
    
    // Find payments by instructor ID
    List<Payment> findByInstructorId(String instructorId);
    
    // Find payments by status
    List<Payment> findByStatus(String status);
    
    // Find payments by Stripe payment intent ID
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    // Find payments by Stripe charge ID
    Optional<Payment> findByStripeChargeId(String stripeChargeId);
    
    // Count payments by user and status
    long countByUserIdAndStatus(String userId, String status);
    
    // Find payments within date range
    @Query("{ 'createdAt' : { $gte: ?0, $lte: ?1 } }")
    List<Payment> findByCreatedAtBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}