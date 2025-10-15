package com.moveup.service;

import com.moveup.model.Booking;
import com.moveup.model.Transaction;
import com.moveup.model.User;
import com.moveup.model.Wallet;
import com.moveup.repository.BookingRepository;
import com.moveup.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.Customer;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    @Value("${stripe.currency:eur}")
    private String currency;
    
    @Autowired
    private StripeService stripeService;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }
    
    // Create payment intent
    public String createPaymentIntent(double amount, String currency, String bookingId) throws StripeException {
        // Convert amount to cents
        long amountInCents = convertToCents(amount);
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .putMetadata("booking_id", bookingId)
                .setDescription("MoveUp Lesson Booking")
                .build();
        
        PaymentIntent intent = PaymentIntent.create(params);
        
        logger.info("Created PaymentIntent: {} for booking: {} amount: {}", 
                   intent.getId(), bookingId, amount);
        
        return intent.getId();
    }
    
    // Process payment with payment method
    public String processPayment(double amount, String paymentMethodId, String bookingId) throws StripeException {
        // Convert amount to cents
        long amountInCents = convertToCents(amount);
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(this.currency)
                .setPaymentMethod(paymentMethodId)
                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                .setConfirm(true)
                .putMetadata("booking_id", bookingId)
                .setDescription("MoveUp Lesson Booking - ID: " + bookingId)
                .build();
        
        PaymentIntent intent = PaymentIntent.create(params);
        
        logger.info("Processed payment: {} for booking: {} amount: {} status: {}", 
                   intent.getId(), bookingId, amount, intent.getStatus());
        
        return intent.getId();
    }
    
    // Confirm payment intent
    public PaymentIntent confirmPayment(String paymentIntentId) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        
        if ("requires_confirmation".equals(intent.getStatus())) {
            intent = intent.confirm();
        }
        
        logger.info("Confirmed payment intent: {} status: {}", paymentIntentId, intent.getStatus());
        
        return intent;
    }
    
    // Capture payment (for authorized payments)
    public PaymentIntent capturePayment(String paymentIntentId, double amount) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        
        Map<String, Object> params = new HashMap<>();
        if (amount > 0) {
            params.put("amount_to_capture", convertToCents(amount));
        }
        
        intent = intent.capture(params);
        
        logger.info("Captured payment intent: {} amount: {}", paymentIntentId, amount);
        
        return intent;
    }
    
    // Create refund
    public String createRefund(String paymentIntentId, double amount, String reason) throws StripeException {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount(convertToCents(amount))
                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                .putMetadata("refund_reason", reason)
                .build();
        
        Refund refund = Refund.create(params);
        
        logger.info("Created refund: {} for payment: {} amount: {}", 
                   refund.getId(), paymentIntentId, amount);
        
        return refund.getId();
    }
    
    // Create full refund
    public String createFullRefund(String paymentIntentId, String reason) throws StripeException {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                .putMetadata("refund_reason", reason)
                .build();
        
        Refund refund = Refund.create(params);
        
        logger.info("Created full refund: {} for payment: {}", refund.getId(), paymentIntentId);
        
        return refund.getId();
    }
    
    // Create customer
    public String createCustomer(String email, String name, String phoneNumber) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(name)
                .setPhone(phoneNumber)
                .build();
        
        Customer customer = Customer.create(params);
        
        logger.info("Created Stripe customer: {} for email: {}", customer.getId(), email);
        
        return customer.getId();
    }
    
    // Get payment intent
    public PaymentIntent getPaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
    
    // Calculate platform fee (MoveUp commission)
    public double calculatePlatformFee(double amount) {
        // 5% platform fee
        double feePercentage = 0.05;
        return BigDecimal.valueOf(amount * feePercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    // Calculate instructor payout
    public double calculateInstructorPayout(double totalAmount) {
        double platformFee = calculatePlatformFee(totalAmount);
        return BigDecimal.valueOf(totalAmount - platformFee)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    // Calculate payment processing fee (Stripe fee)
    public double calculateProcessingFee(double amount) {
        // Stripe fee: 2.9% + €0.30 per transaction
        double stripePercentage = 0.029;
        double stripeFixedFee = 0.30;
        
        return BigDecimal.valueOf((amount * stripePercentage) + stripeFixedFee)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    // Convert euros to cents for Stripe
    private long convertToCents(double amount) {
        return BigDecimal.valueOf(amount)
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }
    
    // Convert cents to euros from Stripe
    private double convertToEuros(long amountInCents) {
        return BigDecimal.valueOf(amountInCents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    // Validate payment amount
    public boolean isValidPaymentAmount(double amount) {
        // Minimum €0.50, maximum €10,000
        return amount >= 0.50 && amount <= 10000.00;
    }
    
    // Handle webhook events
    public void handleWebhookEvent(String eventType, String paymentIntentId, String status) {
        switch (eventType) {
            case "payment_intent.succeeded":
                logger.info("Payment succeeded for intent: {}", paymentIntentId);
                // Update booking payment status
                break;
            case "payment_intent.payment_failed":
                logger.warn("Payment failed for intent: {}", paymentIntentId);
                // Handle payment failure
                break;
            case "payment_intent.requires_action":
                logger.info("Payment requires action for intent: {}", paymentIntentId);
                // Handle 3D Secure or other authentication
                break;
            default:
                logger.info("Unhandled webhook event: {} for intent: {}", eventType, paymentIntentId);
        }
    }
    
    // Payment summary for booking
    public PaymentSummary calculatePaymentSummary(double lessonPrice, boolean isPremiumUser) {
        PaymentSummary summary = new PaymentSummary();
        
        // Base lesson price
        summary.setLessonPrice(lessonPrice);
        
        // Platform fee
        double platformFee = calculatePlatformFee(lessonPrice);
        summary.setPlatformFee(platformFee);
        
        // Processing fee
        double processingFee = calculateProcessingFee(lessonPrice);
        summary.setProcessingFee(processingFee);
        
        // Discount for premium users
        double discount = 0.0;
        if (isPremiumUser) {
            discount = lessonPrice * 0.10; // 10% discount for premium users
        }
        summary.setDiscount(discount);
        
        // Total amount
        double totalAmount = lessonPrice - discount;
        summary.setTotalAmount(totalAmount);
        
        // Instructor payout
        double instructorPayout = calculateInstructorPayout(lessonPrice) - discount;
        summary.setInstructorPayout(instructorPayout);
        
        return summary;
    }
    
    /**
     * NEW: Create PaymentIntent and HOLD payment (authorize without capture)
     * Payment is authorized but NOT captured until lesson is validated
     */
    public PaymentIntent createAndHoldPayment(
            Booking booking,
            String paymentMethodId
    ) throws StripeException {
        
        // Calculate amounts
        Double grossAmount = booking.getPrice();
        Double platformFee = walletService.calculatePlatformFee(grossAmount);
        Double netAmount = walletService.calculateNetAmount(grossAmount);
        
        // Convert to cents
        long amountInCents = Math.round(grossAmount * 100);
        
        // Create PaymentIntent with manual capture
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency("eur")
            .setPaymentMethod(paymentMethodId)
            .setConfirm(true)
            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL) // HOLD payment
            .setDescription("Lezione " + booking.getSport() + " - " + booking.getId())
            .putMetadata("bookingId", booking.getId())
            .putMetadata("trainerId", booking.getInstructorId())
            .putMetadata("customerId", booking.getUserId())
            .putMetadata("grossAmount", grossAmount.toString())
            .putMetadata("platformFee", platformFee.toString())
            .putMetadata("netAmount", netAmount.toString())
            .build();
        
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        
        // Update booking with payment intent ID
        booking.setPaymentIntentId(paymentIntent.getId());
        booking.setPaymentStatus("AUTHORIZED");
        bookingRepository.save(booking);
        
        logger.info("Payment HELD for booking {}: amount={}, status={}", 
            booking.getId(), grossAmount, paymentIntent.getStatus());
        
        return paymentIntent;
    }
    
    /**
     * NEW: Capture payment after lesson validation
     * This actually charges the customer's card
     */
    public PaymentIntent capturePayment(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        
        PaymentIntentCaptureParams params = PaymentIntentCaptureParams.builder()
            .build();
        
        PaymentIntent captured = paymentIntent.capture(params);
        
        logger.info("Payment CAPTURED: {} status={}", paymentIntentId, captured.getStatus());
        
        return captured;
    }
    
    /**
     * NEW: Complete payment flow: Capture + Transfer to Trainer
     */
    public Map<String, Object> processLessonPayment(Booking booking) throws StripeException {
        Map<String, Object> result = new HashMap<>();
        
        // 1. Capture payment (charge customer)
        PaymentIntent capturedPayment = capturePayment(booking.getPaymentIntentId());
        
        // 2. Get trainer and wallet
        User trainer = userRepository.findById(booking.getInstructorId())
            .orElseThrow(() -> new RuntimeException("Trainer not found"));
        
        Wallet trainerWallet = walletService.getWalletByUserId(booking.getInstructorId());
        
        // 3. Calculate amounts
        Double grossAmount = booking.getPrice();
        Double platformFee = walletService.calculatePlatformFee(grossAmount);
        Double netAmount = walletService.calculateNetAmount(grossAmount);
        
        // 4. Transfer to trainer's Stripe account (will auto-payout to IBAN)
        if (trainerWallet.getStripeConnectedAccountId() != null) {
            com.stripe.model.Transfer transfer = stripeService.transferToTrainer(
                trainerWallet.getStripeConnectedAccountId(),
                netAmount,
                "Pagamento lezione " + booking.getSport(),
                booking.getId()
            );
            
            booking.setStripeTransferId(transfer.getId());
            result.put("transferId", transfer.getId());
            
            logger.info("Transferred {} to trainer {} (Stripe account: {})", 
                netAmount, trainer.getId(), trainerWallet.getStripeConnectedAccountId());
        }
        
        // 5. Credit trainer's wallet
        User customer = userRepository.findById(booking.getUserId())
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Transaction transaction = walletService.creditWallet(
            booking.getInstructorId(),
            netAmount,
            "Lezione " + booking.getSport() + " completata",
            booking.getId(),
            customer.getId(),
            customer.getFirstName() + " " + customer.getLastName(),
            grossAmount,
            platformFee
        );
        
        // 6. Update booking status
        booking.setPaymentStatus("COMPLETED");
        booking.setValidatedAt(java.time.LocalDateTime.now());
        bookingRepository.save(booking);
        
        // 7. Build result
        result.put("success", true);
        result.put("paymentIntentId", capturedPayment.getId());
        result.put("transactionId", transaction.getId());
        result.put("grossAmount", grossAmount);
        result.put("platformFee", platformFee);
        result.put("netAmount", netAmount);
        result.put("trainerEarning", netAmount);
        
        logger.info("Lesson payment completed for booking {}: gross={}, fee={}, net={}", 
            booking.getId(), grossAmount, platformFee, netAmount);
        
        return result;
    }
    
    /**
     * NEW: Cancel payment (if lesson is cancelled before validation)
     */
    public PaymentIntent cancelPayment(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntent cancelled = paymentIntent.cancel();
        
        logger.info("Payment CANCELLED: {}", paymentIntentId);
        
        return cancelled;
    }
    
    // Helper class for payment summary
    public static class PaymentSummary {
        private double lessonPrice;
        private double platformFee;
        private double processingFee;
        private double discount;
        private double totalAmount;
        private double instructorPayout;
        
        // Getters and setters
        public double getLessonPrice() { return lessonPrice; }
        public void setLessonPrice(double lessonPrice) { this.lessonPrice = lessonPrice; }
        
        public double getPlatformFee() { return platformFee; }
        public void setPlatformFee(double platformFee) { this.platformFee = platformFee; }
        
        public double getProcessingFee() { return processingFee; }
        public void setProcessingFee(double processingFee) { this.processingFee = processingFee; }
        
        public double getDiscount() { return discount; }
        public void setDiscount(double discount) { this.discount = discount; }
        
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        
        public double getInstructorPayout() { return instructorPayout; }
        public void setInstructorPayout(double instructorPayout) { this.instructorPayout = instructorPayout; }
    }
}