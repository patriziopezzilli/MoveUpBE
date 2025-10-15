package com.moveup.repository;

import com.moveup.model.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends MongoRepository<Wallet, String> {
    
    // Find wallet by user ID
    Optional<Wallet> findByUserId(String userId);
    
    // Check if wallet exists for user
    boolean existsByUserId(String userId);
    
    // Find by Stripe Connected Account ID
    Optional<Wallet> findByStripeConnectedAccountId(String stripeConnectedAccountId);
    
    // Find by pass serial number
    Optional<Wallet> findByPassSerialNumber(String passSerialNumber);
    
    // Find wallets with bank account setup
    java.util.List<Wallet> findByBankAccountSetupTrue();
    
    // Find wallets with pass added to Apple Wallet
    java.util.List<Wallet> findByPassAddedToWalletTrue();
}
