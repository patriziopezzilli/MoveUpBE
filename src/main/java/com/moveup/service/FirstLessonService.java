package com.moveup.service;

import com.moveup.model.User;
import com.moveup.model.Booking;
import com.moveup.repository.UserRepository;
import com.moveup.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class FirstLessonService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private WalletService walletService;
    
    // Configuration
    private static final int MAX_FREE_LESSONS = 500; // Limite campagna
    private static final double MAX_FREE_LESSON_AMOUNT = 10.0; // Max €10 gratis
    private static int freeLessonsGiven = 0; // Counter campagna
    
    /**
     * Check se l'utente ha diritto alla prima lezione gratis
     */
    public FirstLessonEligibility checkEligibility(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        // Check se ha già usato la prima lezione gratis
        if (user.getHasUsedFirstLesson()) {
            return new FirstLessonEligibility(
                false,
                "Hai già usato la tua prima lezione gratis",
                0.0
            );
        }
        
        // Check se ha già prenotato lezioni
        long bookingsCount = bookingRepository.countByUserId(userId);
        if (bookingsCount > 0) {
            return new FirstLessonEligibility(
                false,
                "Hai già effettuato delle prenotazioni",
                0.0
            );
        }
        
        // Check limite campagna
        if (freeLessonsGiven >= MAX_FREE_LESSONS) {
            return new FirstLessonEligibility(
                false,
                "Promozione terminata - limite raggiunto",
                0.0
            );
        }
        
        // Eligible!
        return new FirstLessonEligibility(
            true,
            "🎉 La tua prima lezione è GRATIS! (Max €" + MAX_FREE_LESSON_AMOUNT + ")",
            MAX_FREE_LESSON_AMOUNT
        );
    }
    
    /**
     * Applica sconto prima lezione gratis al booking
     */
    public FirstLessonDiscount applyFirstLessonDiscount(String userId, Booking booking) {
        FirstLessonEligibility eligibility = checkEligibility(userId);
        
        if (!eligibility.isEligible()) {
            throw new RuntimeException(eligibility.getMessage());
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        double lessonPrice = booking.getPrice();
        double discountAmount = Math.min(lessonPrice, MAX_FREE_LESSON_AMOUNT);
        double finalPrice = lessonPrice - discountAmount;
        
        // Se la lezione costa meno di €10, è completamente gratis
        boolean isCompletelyFree = lessonPrice <= MAX_FREE_LESSON_AMOUNT;
        
        // Marca utente come "ha usato prima lezione"
        user.setHasUsedFirstLesson(true);
        userRepository.save(user);
        
        // Incrementa counter campagna
        freeLessonsGiven++;
        
        return new FirstLessonDiscount(
            true,
            discountAmount,
            finalPrice,
            isCompletelyFree,
            "🎁 MoveUp ti regala " + (isCompletelyFree ? "la lezione completa" : "€" + discountAmount)
        );
    }
    
    /**
     * Processa pagamento con sconto prima lezione
     * MoveUp copre la differenza pagando il trainer
     */
    public FirstLessonPaymentResult processFirstLessonPayment(
            String userId, 
            Booking booking,
            String paymentMethodId
    ) throws com.stripe.exception.StripeException {
        
        FirstLessonDiscount discount = applyFirstLessonDiscount(userId, booking);
        
        // Se completamente gratis, nessun addebito al cliente
        if (discount.isCompletelyFree()) {
            // MoveUp paga direttamente il trainer
            double trainerAmount = walletService.calculateNetAmount(booking.getPrice());
            
            // Credit wallet trainer (MoveUp copre)
            walletService.creditWallet(
                booking.getInstructorId(),
                trainerAmount,
                "Prima lezione gratis - coperta da MoveUp",
                booking.getId(),
                userId,
                "MoveUp Platform",
                booking.getPrice(),
                walletService.calculatePlatformFee(booking.getPrice())
            );
            
            // Update booking
            booking.setPaymentStatus(Booking.PaymentStatus.CAPTURED);
            booking.setNotes("Prima lezione gratis - MoveUp copre il costo");
            
            return new FirstLessonPaymentResult(
                true,
                0.0,
                booking.getPrice(),
                booking.getPrice(),
                "🎉 Lezione completamente GRATIS! MoveUp ha coperto il costo.",
                null
            );
        }
        
        // Se parzialmente gratis, addebita solo la differenza
        // Crea PaymentIntent per la differenza
        com.stripe.model.PaymentIntent paymentIntent = paymentService.createAndHoldPayment(
            booking,
            paymentMethodId
        );
        
        // MoveUp copre la parte scontata
        double moveUpCovers = discount.getDiscountAmount();
        double trainerAmount = walletService.calculateNetAmount(booking.getPrice());
        
        // Credit wallet trainer (cliente paga parte + MoveUp copre resto)
        walletService.creditWallet(
            booking.getInstructorId(),
            trainerAmount,
            "Lezione con sconto prima volta - MoveUp copre €" + moveUpCovers,
            booking.getId(),
            userId,
            "Cliente + MoveUp",
            booking.getPrice(),
            walletService.calculatePlatformFee(booking.getPrice())
        );
        
        return new FirstLessonPaymentResult(
            true,
            discount.getFinalPrice(),
            discount.getDiscountAmount(),
            booking.getPrice(),
            "Paghi solo €" + discount.getFinalPrice() + " - MoveUp copre il resto!",
            paymentIntent.getId()
        );
    }
    
    /**
     * Stats campagna first lesson
     */
    public FirstLessonStats getCampaignStats() {
        double totalCostCovered = freeLessonsGiven * MAX_FREE_LESSON_AMOUNT;
        int remaining = MAX_FREE_LESSONS - freeLessonsGiven;
        double percentageUsed = (double) freeLessonsGiven / MAX_FREE_LESSONS * 100;
        
        return new FirstLessonStats(
            freeLessonsGiven,
            remaining,
            MAX_FREE_LESSONS,
            totalCostCovered,
            percentageUsed
        );
    }
    
    // Inner classes
    public static class FirstLessonEligibility {
        private boolean eligible;
        private String message;
        private double maxDiscount;
        
        public FirstLessonEligibility(boolean eligible, String message, double maxDiscount) {
            this.eligible = eligible;
            this.message = message;
            this.maxDiscount = maxDiscount;
        }
        
        public boolean isEligible() { return eligible; }
        public String getMessage() { return message; }
        public double getMaxDiscount() { return maxDiscount; }
    }
    
    public static class FirstLessonDiscount {
        private boolean applied;
        private double discountAmount;
        private double finalPrice;
        private boolean completelyFree;
        private String message;
        
        public FirstLessonDiscount(boolean applied, double discountAmount, double finalPrice, 
                                  boolean completelyFree, String message) {
            this.applied = applied;
            this.discountAmount = discountAmount;
            this.finalPrice = finalPrice;
            this.completelyFree = completelyFree;
            this.message = message;
        }
        
        public boolean isApplied() { return applied; }
        public double getDiscountAmount() { return discountAmount; }
        public double getFinalPrice() { return finalPrice; }
        public boolean isCompletelyFree() { return completelyFree; }
        public String getMessage() { return message; }
    }
    
    public static class FirstLessonPaymentResult {
        private boolean success;
        private double amountCharged;
        private double moveUpCovers;
        private double originalPrice;
        private String message;
        private String paymentIntentId;
        
        public FirstLessonPaymentResult(boolean success, double amountCharged, double moveUpCovers,
                                        double originalPrice, String message, String paymentIntentId) {
            this.success = success;
            this.amountCharged = amountCharged;
            this.moveUpCovers = moveUpCovers;
            this.originalPrice = originalPrice;
            this.message = message;
            this.paymentIntentId = paymentIntentId;
        }
        
        public boolean isSuccess() { return success; }
        public double getAmountCharged() { return amountCharged; }
        public double getMoveUpCovers() { return moveUpCovers; }
        public double getOriginalPrice() { return originalPrice; }
        public String getMessage() { return message; }
        public String getPaymentIntentId() { return paymentIntentId; }
    }
    
    public static class FirstLessonStats {
        private int lessonsGiven;
        private int lessonsRemaining;
        private int totalCampaignLimit;
        private double totalCostCovered;
        private double percentageUsed;
        
        public FirstLessonStats(int lessonsGiven, int lessonsRemaining, int totalCampaignLimit,
                               double totalCostCovered, double percentageUsed) {
            this.lessonsGiven = lessonsGiven;
            this.lessonsRemaining = lessonsRemaining;
            this.totalCampaignLimit = totalCampaignLimit;
            this.totalCostCovered = totalCostCovered;
            this.percentageUsed = percentageUsed;
        }
        
        public int getLessonsGiven() { return lessonsGiven; }
        public int getLessonsRemaining() { return lessonsRemaining; }
        public int getTotalCampaignLimit() { return totalCampaignLimit; }
        public double getTotalCostCovered() { return totalCostCovered; }
        public double getPercentageUsed() { return percentageUsed; }
    }
}
