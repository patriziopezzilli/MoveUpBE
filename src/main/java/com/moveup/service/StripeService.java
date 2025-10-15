package com.moveup.service;

import com.moveup.model.User;
import com.moveup.model.Wallet;
import com.moveup.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.ExternalAccount;
import com.stripe.model.Transfer;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.TransferCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class StripeService {
    
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
}
