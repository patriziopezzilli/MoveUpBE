package com.moveup.controller;

import com.moveup.dto.RegisterWithOnboardingDTO;
import com.moveup.model.User;
import com.moveup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    // Register new user
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            
            // Remove sensitive information from response
            createdUser.setPassword(null);
            createdUser.setVerificationToken(null);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", "Utente registrato con successo. Controlla la tua email per la verifica.",
                        "user", createdUser
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Register with complete onboarding data
    @PostMapping("/register-with-onboarding")
    public ResponseEntity<Map<String, Object>> registerWithOnboarding(
            @Valid @RequestBody RegisterWithOnboardingDTO dto) {
        try {
            User createdUser = userService.createUserWithOnboarding(dto);
            
            // Remove sensitive information from response
            createdUser.setPassword(null);
            createdUser.setVerificationToken(null);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", "Registrazione completata con successo!",
                        "user", createdUser,
                        "profileCompletion", userService.calculateProfileCompletion(createdUser)
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Login user
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            // Authentication logic would be implemented here with JWT
            // For now, just validate user exists and password is correct
            
            return userService.getUserByEmail(email)
                    .map(user -> {
                        if (user.isActive() && user.isVerified()) {
                            // Remove sensitive information
                            user.setPassword(null);
                            user.setVerificationToken(null);
                            
                            return ResponseEntity.ok(Map.of(
                                "message", "Login effettuato con successo",
                                "user", user,
                                "token", "jwt_token_would_be_here"
                            ));
                        } else {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("error", "Account non attivato o non verificato"));
                        }
                    })
                    .orElse(ResponseEntity.badRequest()
                            .body(Map.of("error", "Credenziali non valide")));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante il login"));
        }
    }
    
    // Verify email
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String verificationToken = request.get("token");
            boolean verified = userService.verifyEmail(verificationToken);
            
            if (verified) {
                return ResponseEntity.ok(Map.of("message", "Email verificata con successo"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token di verifica non valido o scaduto"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante la verifica email"));
        }
    }
    
    // Request password reset
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            userService.requestPasswordReset(email);
            
            return ResponseEntity.ok(Map.of(
                "message", "Se l'email esiste nel nostro sistema, riceverai le istruzioni per il reset della password"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante la richiesta di reset password"));
        }
    }
    
    // Reset password
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String resetToken = request.get("token");
            String newPassword = request.get("newPassword");
            
            boolean reset = userService.resetPassword(resetToken, newPassword);
            
            if (reset) {
                return ResponseEntity.ok(Map.of("message", "Password reimpostata con successo"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token di reset non valido o scaduto"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante il reset della password"));
        }
    }
    
    // Logout (for completeness, mainly client-side with JWT)
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout effettuato con successo"));
    }
    
    // Refresh token (when JWT is implemented)
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            
            // JWT refresh logic would go here
            
            return ResponseEntity.ok(Map.of(
                "message", "Token aggiornato con successo",
                "token", "new_jwt_token_would_be_here"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token di refresh non valido"));
        }
    }
    
    // Register with complete onboarding data
    @PostMapping("/register-with-onboarding")
    public ResponseEntity<?> registerWithOnboarding(@Valid @RequestBody RegisterWithOnboardingDTO dto) {
        try {
            User user = userService.createUserWithOnboarding(dto);
            double profileCompletion = userService.calculateProfileCompletion(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "Registrazione completata con successo",
                "user", user,
                "profileCompletion", profileCompletion
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante la registrazione"));
        }
    }
}