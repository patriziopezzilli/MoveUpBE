package com.moveup.repository;

import com.moveup.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    
    // Find all transactions for a wallet
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(String walletId);
    
    // Find transactions for a wallet with pagination
    Page<Transaction> findByWalletIdOrderByCreatedAtDesc(String walletId, Pageable pageable);
    
    // Find by booking ID
    List<Transaction> findByBookingId(String bookingId);
    
    // Find by transaction type
    List<Transaction> findByWalletIdAndType(String walletId, Transaction.TransactionType type);
    
    // Find by status
    List<Transaction> findByWalletIdAndStatus(String walletId, Transaction.TransactionStatus status);
    
    // Find pending transactions
    List<Transaction> findByStatusOrderByCreatedAtDesc(Transaction.TransactionStatus status);
    
    // Find transactions within date range
    List<Transaction> findByWalletIdAndCreatedAtBetween(
        String walletId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    // Find by Stripe Payment Intent ID
    Transaction findByStripePaymentIntentId(String stripePaymentIntentId);
    
    // Find by Stripe Transfer ID
    Transaction findByStripeTransferId(String stripeTransferId);
    
    // Calculate total earnings for wallet
    @Query("{ 'walletId': ?0, 'type': 'LESSON_PAYMENT', 'status': 'COMPLETED' }")
    List<Transaction> findCompletedLessonPaymentsByWalletId(String walletId);
    
    // Count transactions by type
    long countByWalletIdAndType(String walletId, Transaction.TransactionType type);
}
