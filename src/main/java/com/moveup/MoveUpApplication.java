package com.moveup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * MoveUp Backend Application
 * 
 * Piattaforma per connettere istruttori sportivi con utenti per lezioni individuali.
 * 
 * Features:
 * - Autenticazione JWT
 * - Gestione utenti e istruttori  
 * - Sistema prenotazioni con pagamenti Stripe
 * - Recensioni bilaterali
 * - Gamification (punti e badge)
 * - Push notifications
 * - File upload S3
 * - Caching Redis
 * - Monitoring Prometheus
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableCaching
@EnableAsync
@EnableMethodSecurity(prePostEnabled = true)
public class MoveUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoveUpApplication.class, args);
    }
}