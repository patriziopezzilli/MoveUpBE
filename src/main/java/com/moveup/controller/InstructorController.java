package com.moveup.controller;

import com.moveup.model.Instructor;
import com.moveup.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructors")
@CrossOrigin(origins = "*")
public class InstructorController {
    
    @Autowired
    private InstructorService instructorService;
    
    // Register new instructor
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerInstructor(@Valid @RequestBody Instructor instructor) {
        try {
            Instructor createdInstructor = instructorService.createInstructor(instructor);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", "Istruttore registrato con successo. Controlla la tua email per la verifica.",
                        "instructor", createdInstructor
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get instructor profile
    @GetMapping("/{instructorId}")
    public ResponseEntity<Instructor> getInstructorProfile(@PathVariable String instructorId) {
        return instructorService.getInstructorById(instructorId)
                .map(instructor -> {
                    // Remove sensitive information
                    instructor.setPassword(null);
                    instructor.setVerificationToken(null);
                    return ResponseEntity.ok(instructor);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update instructor profile
    @PutMapping("/{instructorId}")
    public ResponseEntity<Instructor> updateInstructorProfile(@PathVariable String instructorId, 
                                                            @Valid @RequestBody Instructor updatedInstructor) {
        try {
            Instructor instructor = instructorService.updateInstructor(instructorId, updatedInstructor);
            instructor.setPassword(null);
            return ResponseEntity.ok(instructor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Verify instructor email
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String verificationToken = request.get("token");
            boolean verified = instructorService.verifyEmail(verificationToken);
            
            if (verified) {
                return ResponseEntity.ok(Map.of("message", "Email verificata con successo. Il tuo account è in attesa di approvazione."));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token di verifica non valido o scaduto"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante la verifica email"));
        }
    }
    
    // Get instructor statistics
    @GetMapping("/{instructorId}/stats")
    public ResponseEntity<InstructorService.InstructorStatistics> getInstructorStatistics(@PathVariable String instructorId) {
        try {
            InstructorService.InstructorStatistics stats = instructorService.getInstructorStatistics(instructorId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Set instructor availability
    @PostMapping("/{instructorId}/availability")
    public ResponseEntity<Map<String, String>> setAvailability(@PathVariable String instructorId,
                                                             @RequestBody Map<String, Boolean> request) {
        try {
            boolean available = request.get("available");
            instructorService.setAvailability(instructorId, available);
            
            String message = available ? "Disponibilità attivata" : "Disponibilità disattivata";
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Search instructors
    @GetMapping("/search")
    public ResponseEntity<List<Instructor>> searchInstructors(@RequestParam String query) {
        List<Instructor> instructors = instructorService.searchInstructors(query);
        return ResponseEntity.ok(instructors);
    }
    
    // Get instructors by sport
    @GetMapping("/sport/{sportId}")
    public ResponseEntity<List<Instructor>> getInstructorsBySport(@PathVariable String sportId) {
        List<Instructor> instructors = instructorService.getInstructorsBySport(sportId);
        return ResponseEntity.ok(instructors);
    }
    
    // Get instructors by city
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Instructor>> getInstructorsByCity(@PathVariable String city) {
        List<Instructor> instructors = instructorService.getInstructorsByCity(city);
        return ResponseEntity.ok(instructors);
    }
    
    // Get instructors by minimum rating
    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<Instructor>> getInstructorsByMinRating(@PathVariable double minRating) {
        List<Instructor> instructors = instructorService.getInstructorsByMinRating(minRating);
        return ResponseEntity.ok(instructors);
    }
    
    // Get top-rated instructors
    @GetMapping("/top-rated")
    public ResponseEntity<List<Instructor>> getTopRatedInstructors(@RequestParam(defaultValue = "10") int limit) {
        List<Instructor> instructors = instructorService.getTopRatedInstructors(limit);
        return ResponseEntity.ok(instructors);
    }
    
    // Get available instructors
    @GetMapping("/available")
    public ResponseEntity<List<Instructor>> getAvailableInstructors() {
        List<Instructor> instructors = instructorService.getAvailableInstructors();
        return ResponseEntity.ok(instructors);
    }
    
    // Get instructors with upcoming availability
    @GetMapping("/upcoming-availability")
    public ResponseEntity<List<Instructor>> getInstructorsWithUpcomingAvailability() {
        List<Instructor> instructors = instructorService.getInstructorsWithUpcomingAvailability();
        return ResponseEntity.ok(instructors);
    }
    
    // Get instructors near location
    @GetMapping("/near")
    public ResponseEntity<List<Instructor>> getInstructorsNearLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radiusKm) {
        List<Instructor> instructors = instructorService.getInstructorsNearLocation(latitude, longitude, radiusKm);
        return ResponseEntity.ok(instructors);
    }
    
    // Change password
    @PostMapping("/{instructorId}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable String instructorId,
                                                            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            instructorService.changePassword(instructorId, currentPassword, newPassword);
            
            return ResponseEntity.ok(Map.of("message", "Password cambiata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Deactivate instructor
    @PostMapping("/{instructorId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateInstructor(@PathVariable String instructorId) {
        try {
            instructorService.deactivateInstructor(instructorId);
            return ResponseEntity.ok(Map.of("message", "Account istruttore disattivato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Admin endpoints
    
    // Approve instructor (admin only)
    @PostMapping("/{instructorId}/approve")
    public ResponseEntity<Map<String, String>> approveInstructor(@PathVariable String instructorId) {
        try {
            instructorService.approveInstructor(instructorId);
            return ResponseEntity.ok(Map.of("message", "Istruttore approvato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Reject instructor (admin only)
    @PostMapping("/{instructorId}/reject")
    public ResponseEntity<Map<String, String>> rejectInstructor(@PathVariable String instructorId,
                                                              @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            instructorService.rejectInstructor(instructorId, reason);
            return ResponseEntity.ok(Map.of("message", "Istruttore respinto"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get instructor count (admin only)
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getInstructorCounts() {
        long totalCount = instructorService.getInstructorCount();
        long verifiedCount = instructorService.getVerifiedInstructorCount();
        
        return ResponseEntity.ok(Map.of(
            "total", totalCount,
            "verified", verifiedCount
        ));
    }
}