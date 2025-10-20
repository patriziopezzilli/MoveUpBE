package com.moveup.controller;

import com.moveup.service.PointsService;
import com.moveup.service.PointsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@CrossOrigin(origins = "*")
public class PointsController {
    
    @Autowired
    private PointsService pointsService;
    
    /**
     * Assegna punti dopo lezione completata
     * POST /api/points/award/lesson
     */
    @PostMapping("/award/lesson")
    public ResponseEntity<Map<String, Object>> awardPointsForLesson(
            @RequestBody Map<String, String> request
    ) {
        try {
            String userId = request.get("userId");
            String bookingId = request.get("bookingId");
            
            PointsTransaction transaction = pointsService.awardPointsForLesson(userId, bookingId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "transaction", transaction,
                "message", transaction.isLevelUp() 
                    ? "🎉 LEVEL UP! Sei ora " + transaction.getLevelInfo().getName()
                    : "Hai guadagnato " + transaction.getPointsAwarded() + " punti!"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Assegna punti per recensione
     * POST /api/points/award/review
     */
    @PostMapping("/award/review")
    public ResponseEntity<Map<String, Object>> awardPointsForReview(
            @RequestBody Map<String, String> request
    ) {
        try {
            String userId = request.get("userId");
            
            PointsTransaction transaction = pointsService.awardPointsForReview(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "transaction", transaction,
                "message", "Hai guadagnato " + transaction.getPointsAwarded() + " punti!"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Assegna punti per referral
     * POST /api/points/award/referral
     */
    @PostMapping("/award/referral")
    public ResponseEntity<Map<String, Object>> awardPointsForReferral(
            @RequestBody Map<String, String> request
    ) {
        try {
            String userId = request.get("userId");
            String referredUserId = request.get("referredUserId");
            
            PointsTransaction transaction = pointsService.awardPointsForReferral(userId, referredUserId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "transaction", transaction,
                "message", "💎 Hai guadagnato " + transaction.getPointsAwarded() + " punti per il referral!"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Ottieni rewards disponibili
     * GET /api/points/rewards
     */
    @GetMapping("/rewards")
    public ResponseEntity<Map<String, Object>> getAvailableRewards() {
        try {
            List<Reward> rewards = pointsService.getAvailableRewards();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "rewards", rewards,
                "count", rewards.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Riscatta punti per un reward
     * POST /api/points/redeem
     */
    @PostMapping("/redeem")
    public ResponseEntity<Map<String, Object>> redeemPoints(
            @RequestBody Map<String, String> request
    ) {
        try {
            String userId = request.get("userId");
            String rewardId = request.get("rewardId");
            
            RedemptionResult result = pointsService.redeemPoints(userId, rewardId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "result", result,
                "message", result.getMessage()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "error", e.getMessage()
                ));
        }
    }
    
    /**
     * Ottieni balance punti utente
     * GET /api/points/balance?userId=123
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getPointsBalance(
            @RequestParam String userId
    ) {
        try {
            PointsService.UserPointsInfo pointsInfo = pointsService.getUserPointsInfo(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", pointsInfo.getUserId(),
                "points", pointsInfo.getPoints(),
                "level", pointsInfo.getLevel(),
                "levelName", pointsInfo.getLevelName(),
                "levelBadge", pointsInfo.getLevelBadge()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
