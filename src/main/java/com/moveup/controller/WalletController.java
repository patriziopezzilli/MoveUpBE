package com.moveup.controller;

import com.moveup.model.Transaction;
import com.moveup.model.Wallet;
import com.moveup.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * GET /api/wallet
     * Get wallet info for authenticated user
     */
    @GetMapping
    public ResponseEntity<Wallet> getWallet(@RequestParam String userId) {
        Wallet wallet = walletService.getOrCreateWallet(userId);
        return ResponseEntity.ok(wallet);
    }
    
    /**
     * POST /api/wallet/setup
     * Setup bank account (IBAN)
     */
    @PostMapping("/setup")
    public ResponseEntity<?> setupBankAccount(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String iban = request.get("iban");
            String accountHolderName = request.get("accountHolderName");
            String country = request.getOrDefault("country", "IT");
            
            Wallet wallet = walletService.setupBankAccount(userId, iban, accountHolderName, country);
            
            return ResponseEntity.ok(Map.of(
                "message", "Conto bancario collegato con successo",
                "wallet", wallet
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/wallet/balance
     * Get current balance
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@RequestParam String userId) {
        try {
            Double balance = walletService.getBalance(userId);
            Wallet wallet = walletService.getWalletByUserId(userId);
            
            return ResponseEntity.ok(Map.of(
                "balance", balance,
                "currency", wallet.getCurrency(),
                "formatted", String.format("€%.2f", balance)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/wallet/transactions
     * Get transaction history
     */
    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<Transaction> transactions = walletService.getTransactionHistory(userId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("transactions", transactions.getContent());
            response.put("totalCount", transactions.getTotalElements());
            response.put("page", page);
            response.put("pageSize", size);
            response.put("hasMore", transactions.hasNext());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/wallet/stats
     * Get wallet statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@RequestParam String userId) {
        try {
            Wallet wallet = walletService.getWalletByUserId(userId);
            Double totalEarnings = walletService.calculateTotalEarnings(userId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("balance", wallet.getBalance());
            stats.put("totalEarnings", totalEarnings);
            stats.put("totalLessons", wallet.getTotalLessons());
            stats.put("averageLessonPrice", wallet.getAverageLessonPrice());
            stats.put("totalWithdrawn", wallet.getTotalWithdrawn());
            stats.put("bankAccountSetup", wallet.getBankAccountSetup());
            
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * POST /api/wallet/calculate-fee
     * Calculate platform fee for an amount
     */
    @PostMapping("/calculate-fee")
    public ResponseEntity<Map<String, Object>> calculateFee(@RequestBody Map<String, Double> request) {
        Double grossAmount = request.get("grossAmount");
        
        Double platformFee = walletService.calculatePlatformFee(grossAmount);
        Double netAmount = walletService.calculateNetAmount(grossAmount);
        Double feePercentage = grossAmount > 0 ? (platformFee / grossAmount) * 100 : 0;
        
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("grossAmount", grossAmount);
        breakdown.put("platformFee", platformFee);
        breakdown.put("netAmount", netAmount);
        breakdown.put("feePercentage", feePercentage);
        breakdown.put("formattedGross", String.format("€%.2f", grossAmount));
        breakdown.put("formattedFee", String.format("€%.2f", platformFee));
        breakdown.put("formattedNet", String.format("€%.2f", netAmount));
        
        return ResponseEntity.ok(breakdown);
    }
}
