package com.moveup.controller;

import com.moveup.model.User;
import com.moveup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Get user profile
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserProfile(@PathVariable String userId) {
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update user profile
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUserProfile(@PathVariable String userId, 
                                                 @Valid @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(userId, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Change password
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable String userId,
                                                            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            userService.changePassword(userId, currentPassword, newPassword);
            
            return ResponseEntity.ok(Map.of("message", "Password cambiata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get user statistics
    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserService.UserStatistics> getUserStatistics(@PathVariable String userId) {
        try {
            UserService.UserStatistics stats = userService.getUserStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Add points to user
    @PostMapping("/{userId}/points")
    public ResponseEntity<Map<String, String>> addPoints(@PathVariable String userId,
                                                       @RequestBody Map<String, Object> request) {
        try {
            int points = (Integer) request.get("points");
            String reason = (String) request.get("reason");
            
            userService.addPoints(userId, points, reason);
            
            return ResponseEntity.ok(Map.of("message", "Punti aggiunti con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Deduct points from user
    @DeleteMapping("/{userId}/points")
    public ResponseEntity<Map<String, String>> deductPoints(@PathVariable String userId,
                                                          @RequestBody Map<String, Integer> request) {
        try {
            int points = request.get("points");
            boolean success = userService.deductPoints(userId, points);
            
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Punti detratti con successo"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Punti insufficienti"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get leaderboard
    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        List<User> leaderboard = userService.getLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }
    
    // Search users
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }
    
    // Get users by city
    @GetMapping("/city/{city}")
    public ResponseEntity<List<User>> getUsersByCity(@PathVariable String city) {
        List<User> users = userService.getUsersByCity(city);
        return ResponseEntity.ok(users);
    }
    
    // Deactivate user account
    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable String userId) {
        try {
            userService.deactivateUser(userId);
            return ResponseEntity.ok(Map.of("message", "Account disattivato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Reactivate user account
    @PostMapping("/{userId}/reactivate")
    public ResponseEntity<Map<String, String>> reactivateUser(@PathVariable String userId) {
        try {
            userService.reactivateUser(userId);
            return ResponseEntity.ok(Map.of("message", "Account riattivato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get active users (admin endpoint)
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }
    
    // Get user count (admin endpoint)
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        long count = userService.getUserCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    // Check if user is active
    @GetMapping("/{userId}/active")
    public ResponseEntity<Map<String, Boolean>> isUserActive(@PathVariable String userId) {
        try {
            boolean isActive = userService.isActiveUser(userId);
            return ResponseEntity.ok(Map.of("isActive", isActive));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}