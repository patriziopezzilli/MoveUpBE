package com.moveup.service;

import com.moveup.model.User;
import com.moveup.model.Wallet;
import com.moveup.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.ExternalAccount;
import com.stripe.model.Transfer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.TransferCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class StripeService {
    
    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create Stripe Connected Account for trainer
     * This is a CUSTOM account for maximum transparency
     */
    public Account createConnectedAccount(String userId, User user) throws StripeException {
        // Create custom connected account
        AccountCreateParams params = AccountCreateParams.builder()
            .setType(AccountCreateParams.Type.CUSTOM)
            .setCountry("IT") // Italy
            .setEmail(user.getEmail())
            .setCapabilities(
                AccountCreateParams.Capabilities.builder()
                    .setTransfers(
                        AccountCreateParams.Capabilities.Transfers.builder()
                            .setRequested(true)
                            .build()
                    )
                    .build()
            )
            .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
            .setIndividual(
                AccountCreateParams.Individual.builder()
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setEmail(user.getEmail())
                    .build()
            )
            .build();
        
        Account account = Account.create(params);
        
        // Save account ID to wallet
        Wallet wallet = walletService.getOrCreateWallet(userId);
        wallet.setStripeConnectedAccountId(account.getId());
        
        return account;
    }
    
    /**
     * Add external bank account (IBAN) to Connected Account
     */
    public ExternalAccount addBankAccount(
            String stripeAccountId, 
            String iban, 
            String accountHolderName,
            String country
    ) throws StripeException {
        
        // Create bank account token first
        Map<String, Object> bankAccountParams = new HashMap<>();
        bankAccountParams.put("object", "bank_account");
        bankAccountParams.put("country", country);
        bankAccountParams.put("currency", "eur");
        bankAccountParams.put("account_holder_name", accountHolderName);
        bankAccountParams.put("account_number", iban);
        bankAccountParams.put("default_for_currency", true);
        
        // Add external account to connected account
        Account account = Account.retrieve(stripeAccountId);
        return account.getExternalAccounts().create(bankAccountParams);
    }
    
    /**
     * Transfer funds to trainer's connected account
     * This will automatically payout to their IBAN
     */
    public Transfer transferToTrainer(
            String stripeAccountId, 
            Double amount, 
            String description,
            String transferGroup
    ) throws StripeException {
        
        // Convert to cents (Stripe uses smallest currency unit)
        long amountInCents = Math.round(amount * 100);
        
        TransferCreateParams params = TransferCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency("eur")
            .setDestination(stripeAccountId)
            .setDescription(description)
            .setTransferGroup(transferGroup)
            .build();
        
        return Transfer.create(params);
    }
    
    /**
     * Get Connected Account details
     */
    public Account getAccount(String stripeAccountId) throws StripeException {
        return Account.retrieve(stripeAccountId);
    }
    
    /**
     * Check if account has external account setup
     */
    public boolean hasExternalAccount(String stripeAccountId) throws StripeException {
        Account account = Account.retrieve(stripeAccountId);
        return account.getExternalAccounts().getData().size() > 0;
    }
    
    /**
     * Setup complete Stripe account with IBAN
     * Combines account creation + bank account addition
     */
    public Map<String, Object> setupStripeAccount(
            String userId,
            String iban,
            String accountHolderName,
            String country
    ) throws StripeException {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Wallet wallet = walletService.getOrCreateWallet(userId);
        
        Account account;
        
        // Check if account already exists
        if (wallet.getStripeConnectedAccountId() != null) {
            account = Account.retrieve(wallet.getStripeConnectedAccountId());
        } else {
            // Create new account
            account = createConnectedAccount(userId, user);
            wallet.setStripeConnectedAccountId(account.getId());
        }
        
        // Add bank account
        ExternalAccount externalAccount = addBankAccount(
            account.getId(), 
            iban, 
            accountHolderName, 
            country
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("accountId", account.getId());
        result.put("externalAccountId", externalAccount.getId());
        result.put("success", true);
        
        return result;
    }
    
    /**
     * Capture payment and transfer funds to trainer's connected account
     * This method combines payment capture with automatic transfer to trainer
     */
    public Map<String, Object> capturePaymentAndTransfer(
            String paymentIntentId,
            String trainerUserId,
            Double amount,
            String description,
            String transferGroup
    ) throws StripeException {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // First, get the trainer's wallet to find their Stripe connected account
            Wallet trainerWallet = walletService.getOrCreateWallet(trainerUserId);
            
            if (trainerWallet.getStripeConnectedAccountId() == null) {
                throw new RuntimeException("Trainer does not have a Stripe connected account setup");
            }
            
            // Capture the payment intent
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            
            if (!"requires_capture".equals(intent.getStatus())) {
                throw new RuntimeException("Payment intent is not in a capturable state. Current status: " + intent.getStatus());
            }
            
            // Capture the payment
            PaymentIntent capturedIntent = intent.capture();
            result.put("paymentIntent", capturedIntent);
            
            // If capture was successful, transfer funds to trainer
            if ("succeeded".equals(capturedIntent.getStatus())) {
                // Calculate transfer amount (after platform fees)
                // For now, transfer 80% to trainer, keep 20% as platform fee
                double platformFee = amount * 0.20;
                double transferAmount = amount - platformFee;
                
                Transfer transfer = transferToTrainer(
                    trainerWallet.getStripeConnectedAccountId(),
                    transferAmount,
                    description + " - Platform fee: " + String.format("%.2f", platformFee) + " EUR",
                    transferGroup
                );
                
                result.put("transfer", transfer);
                result.put("transferAmount", transferAmount);
                result.put("platformFee", platformFee);
                result.put("success", true);
                
                logger.info("Successfully captured payment {} and transferred {} EUR to trainer {}", 
                           paymentIntentId, transferAmount, trainerUserId);
                
            } else {
                result.put("success", false);
                result.put("error", "Payment capture failed with status: " + capturedIntent.getStatus());
            }
            
        } catch (StripeException e) {
            logger.error("Stripe error during capture and transfer: {}", e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error during capture and transfer: {}", e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
            throw new RuntimeException("Failed to capture payment and transfer funds", e);
        }
        
        return result;
    }
}
