package com.moveup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "MoveUp Backend API is running",
            "timestamp", LocalDateTime.now(),
            "version", "1.0.0"
        ));
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
            "application", "MoveUp Backend",
            "description", "Piattaforma per connettere istruttori sportivi con studenti",
            "version", "1.0.0",
            "environment", "development",
            "features", Map.of(
                "authentication", "JWT",
                "database", "MongoDB",
                "payments", "Stripe",
                "notifications", "Firebase",
                "email", "SendGrid",
                "storage", "AWS S3"
            )
        ));
    }
}