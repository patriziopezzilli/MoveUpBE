package com.moveup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import jakarta.annotation.PostConstruct;

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
 * - Upload immagini Base64 (salvate su MongoDB)
 * - Cache in-memory con refresh automatico
 * - Geocoding con OpenStreetMap
 * - Monitoring Prometheus
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableCaching
@EnableAsync
@EnableMethodSecurity(prePostEnabled = true)
public class MoveUpApplication {

    @Autowired
    private Environment env;

    @PostConstruct
    public void logMongoConfig() {
        System.out.println("=== MONGO CONFIG DEBUG ===");
        System.out.println("MONGODB_URI env var: " + env.getProperty("MONGODB_URI"));
        System.out.println("spring.data.mongodb.uri: " + env.getProperty("spring.data.mongodb.uri"));
        System.out.println("==========================");
    }

    public static void main(String[] args) {
        SpringApplication.run(MoveUpApplication.class, args);
    }
}