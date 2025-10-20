package com.moveup.service;

import com.moveup.model.Transaction;
import com.moveup.model.Wallet;
import com.moveup.repository.TransactionRepository;
import com.moveup.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WalletService {
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Value("${app.platform.fee-percentage:0.0}")
    private double platformFeePercentage;
    
    /**
     * Get or create wallet for user
     */
    public Wallet getOrCreateWallet(String userId) {
        return walletRepository.findByUserId(userId)
            .orElseGet(() -> {
                Wallet wallet = new Wallet(userId);
                return walletRepository.save(wallet);
            });
    }
    
    /**
     * Get wallet by user ID
     */
    public Wallet getWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet non trovato per l'utente"));
    }
    
    /**
     * Setup bank account (IBAN)
     */
    public Wallet setupBankAccount(String userId, String iban, String accountHolderName, String country) {
        Wallet wallet = getOrCreateWallet(userId);
        
        // Validate IBAN format (basic validation)
        if (!isValidIban(iban)) {
            throw new RuntimeException("IBAN non valido");
        }
        
        // Mask IBAN for display (e.g., IT60 •••• •••• •••• 3456)
        String maskedIban = maskIban(iban);
        
        wallet.setMaskedIban(maskedIban);
        wallet.setAccountHolderName(accountHolderName);
        wallet.setCountry(country);
        wallet.setBankAccountSetup(true);
        
        return walletRepository.save(wallet);
    }
    
    /**
     * Credit wallet (add money)
     */
    public Transaction creditWallet(
            String userId, 
            Double amount, 
            String description,
            String bookingId,
            String customerId,
            String customerName,
            Double grossAmount,
            Double platformFee
    ) {
        Wallet wallet = getOrCreateWallet(userId);
        
        // Credit wallet
        wallet.credit(amount);
        wallet.incrementLessonCount();
        walletRepository.save(wallet);
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType(Transaction.TransactionType.LESSON_PAYMENT);
        transaction.setAmount(amount);
        transaction.setNetAmount(amount);
        transaction.setGrossAmount(grossAmount);
        transaction.setPlatformFee(platformFee);
        transaction.setDescription(description);
        transaction.setBookingId(bookingId);
        transaction.setCustomerId(customerId);
        transaction.setCustomerName(customerName);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.complete();
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Debit wallet (withdraw money)
     */
    public Transaction debitWallet(String userId, Double amount, String description) {
        Wallet wallet = getWalletByUserId(userId);
        
        // Check sufficient balance
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Saldo insufficiente");
        }
        
        // Debit wallet
        wallet.debit(amount);
        walletRepository.save(wallet);
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType(Transaction.TransactionType.PAYOUT);
        transaction.setAmount(amount);
        transaction.setNetAmount(amount);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.complete();
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Get wallet balance
     */
    public Double getBalance(String userId) {
        Wallet wallet = getWalletByUserId(userId);
        return wallet.getBalance();
    }
    
    /**
     * Get transaction history
     */
    public List<Transaction> getTransactionHistory(String userId) {
        Wallet wallet = getWalletByUserId(userId);
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }
    
    /**
     * Get transaction history with pagination
     */
    public Page<Transaction> getTransactionHistory(String userId, int page, int size) {
        Wallet wallet = getWalletByUserId(userId);
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable);
    }
    
    /**
     * Get transactions within date range
     */
    public List<Transaction> getTransactionsInRange(
            String userId, 
            LocalDateTime startDate, 
            LocalDateTime endDate
    ) {
        Wallet wallet = getWalletByUserId(userId);
        return transactionRepository.findByWalletIdAndCreatedAtBetween(
            wallet.getId(), 
            startDate, 
            endDate
        );
    }
    
    /**
     * Calculate total earnings
     */
    public Double calculateTotalEarnings(String userId) {
        Wallet wallet = getWalletByUserId(userId);
        List<Transaction> completedPayments = transactionRepository
            .findCompletedLessonPaymentsByWalletId(wallet.getId());
        
        return completedPayments.stream()
            .mapToDouble(Transaction::getNetAmount)
            .sum();
    }
    
    /**
     * Generate pass update token
     */
    public String generatePassUpdateToken(String userId) {
        Wallet wallet = getWalletByUserId(userId);
        String token = UUID.randomUUID().toString();
        wallet.setPassUpdateToken(token);
        walletRepository.save(wallet);
        return token;
    }
    
    /**
     * Mark pass as added to Apple Wallet
     */
    public void markPassAdded(String userId) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setPassAddedToWallet(true);
        walletRepository.save(wallet);
    }
    
    // Helper methods
    
    private boolean isValidIban(String iban) {
        // Remove spaces
        String cleanIban = iban.replaceAll("\\s", "");
        
        // Basic validation: IT IBAN is 27 characters
        if (cleanIban.startsWith("IT") && cleanIban.length() == 27) {
            return true;
        }
        
        // Add more country-specific validation as needed
        return cleanIban.length() >= 15 && cleanIban.length() <= 34;
    }
    
    private String maskIban(String iban) {
        // Remove spaces
        String cleanIban = iban.replaceAll("\\s", "");
        
        // Keep first 4 and last 4 characters, mask the rest
        if (cleanIban.length() <= 8) {
            return cleanIban;
        }
        
        String first = cleanIban.substring(0, 4);
        String last = cleanIban.substring(cleanIban.length() - 4);
        int middleLength = cleanIban.length() - 8;
        
        // Create masked middle part in groups of 4
        StringBuilder masked = new StringBuilder(first);
        for (int i = 0; i < middleLength; i++) {
            if (i % 4 == 0) {
                masked.append(" ");
            }
            masked.append("•");
        }
        masked.append(" ").append(last);
        
        return masked.toString();
    }
    
    /**
     * Calculate platform fee based on gross amount
     */
    public Double calculatePlatformFee(Double grossAmount) {
        return grossAmount * platformFeePercentage;
    }
    
    /**
     * Calculate net amount (after fee)
     */
    public Double calculateNetAmount(Double grossAmount) {
        Double fee = calculatePlatformFee(grossAmount);
        return grossAmount - fee;
    }
}
