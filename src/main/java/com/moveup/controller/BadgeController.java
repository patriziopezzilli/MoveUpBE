package com.moveup.controller;

import com.moveup.model.Badge;
import com.moveup.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
public class BadgeController {
    
    @Autowired
    private BadgeService badgeService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBadge(@Valid @RequestBody Badge badge) {
        try {
            Badge createdBadge = badgeService.createBadge(badge);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Badge creato con successo", "badge", createdBadge));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Badge>> getAllBadges() {
        List<Badge> badges = badgeService.getAllBadges();
        return ResponseEntity.ok(badges);
    }
    
    @GetMapping("/{badgeId}")
    public ResponseEntity<Badge> getBadge(@PathVariable String badgeId) {
        return badgeService.getBadgeById(badgeId)
                .map(badge -> ResponseEntity.ok(badge))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Badge>> getUserBadges(@PathVariable String userId) {
        List<Badge> badges = badgeService.getUserBadges(userId);
        return ResponseEntity.ok(badges);
    }
    
    @PostMapping("/user/{userId}/award/{badgeId}")
    public ResponseEntity<Map<String, String>> awardBadge(@PathVariable String userId, @PathVariable String badgeId) {
        try {
            badgeService.awardBadgeToUser(userId, badgeId);
            return ResponseEntity.ok(Map.of("message", "Badge assegnato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/check/{userId}")
    public ResponseEntity<List<Badge>> checkAndAwardBadges(@PathVariable String userId) {
        List<Badge> newBadges = badgeService.checkAndAwardBadges(userId);
        return ResponseEntity.ok(newBadges);
    }
    public ResponseEntity<Map<String, Object>> updateBadge(@PathVariable String badgeId, @Valid @RequestBody Badge badge) {
        try {
            Badge updatedBadge = badgeService.updateBadge(badgeId, badge);
            return ResponseEntity.ok(Map.of("message", "Badge aggiornato con successo", "badge", updatedBadge));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{badgeId}")
    public ResponseEntity<Map<String, String>> deleteBadge(@PathVariable String badgeId) {
        try {
            badgeService.deleteBadge(badgeId);
            return ResponseEntity.ok(Map.of("message", "Badge eliminato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}