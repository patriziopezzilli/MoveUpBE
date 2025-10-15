package com.moveup.controller;

import com.moveup.service.RankingService;
import com.moveup.service.RankingService.RankedTrainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rankings")
@CrossOrigin(origins = "*")
public class RankingController {
    
    @Autowired
    private RankingService rankingService;
    
    /**
     * Top trainer per città
     * GET /api/rankings/city/Milano?sport=Tennis&limit=10
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<Map<String, Object>> getTopTrainersByCity(
            @PathVariable String city,
            @RequestParam(required = false) String sport,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        try {
            List<RankedTrainer> rankings = rankingService.getTopTrainersByCity(city, sport, limit);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "city", city,
                "sport", sport != null ? sport : "all",
                "rankings", rankings,
                "totalTrainers", rankings.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Top trainer globali
     * GET /api/rankings/global?sport=Padel&limit=20
     */
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalTopTrainers(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false, defaultValue = "20") Integer limit
    ) {
        try {
            List<RankedTrainer> rankings = rankingService.getGlobalTopTrainers(sport, limit);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "sport", sport != null ? sport : "all",
                "rankings", rankings,
                "totalTrainers", rankings.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Rising stars: trainer in crescita
     * GET /api/rankings/rising-stars?city=Roma&limit=5
     */
    @GetMapping("/rising-stars")
    public ResponseEntity<Map<String, Object>> getRisingStars(
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "5") Integer limit
    ) {
        try {
            List<RankedTrainer> risingStars = rankingService.getRisingStars(city, limit);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "city", city != null ? city : "all",
                "risingStars", risingStars,
                "count", risingStars.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
