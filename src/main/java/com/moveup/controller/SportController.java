package com.moveup.controller;

import com.moveup.model.Sport;
import com.moveup.service.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sports")
@CrossOrigin(origins = "*")
public class SportController {
    
    @Autowired
    private SportService sportService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSport(@Valid @RequestBody Sport sport) {
        try {
            Sport createdSport = sportService.createSport(sport);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Sport creato con successo", "sport", createdSport));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{sportId}")
    public ResponseEntity<Sport> getSport(@PathVariable String sportId) {
        return sportService.getSportById(sportId)
                .map(sport -> ResponseEntity.ok(sport))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{sportId}")
    public ResponseEntity<Sport> updateSport(@PathVariable String sportId, @Valid @RequestBody Sport updatedSport) {
        try {
            Sport sport = sportService.updateSport(sportId, updatedSport);
            return ResponseEntity.ok(sport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{sportId}")
    public ResponseEntity<Map<String, String>> deleteSport(@PathVariable String sportId) {
        try {
            sportService.deleteSport(sportId);
            return ResponseEntity.ok(Map.of("message", "Sport eliminato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Sport>> getAllActiveSports() {
        List<Sport> sports = sportService.getAllActiveSports();
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Sport>> getSportsByCategory(@PathVariable String category) {
        List<Sport> sports = sportService.getSportsByCategory(category);
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Sport>> searchSports(@RequestParam String query) {
        List<Sport> sports = sportService.searchSportsByName(query);
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/difficulty/{level}")
    public ResponseEntity<List<Sport>> getSportsByDifficulty(@PathVariable String level) {
        List<Sport> sports = sportService.getSportsByDifficultyLevel(level);
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/indoor")
    public ResponseEntity<List<Sport>> getIndoorSports() {
        List<Sport> sports = sportService.getIndoorSports();
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/outdoor")
    public ResponseEntity<List<Sport>> getOutdoorSports() {
        List<Sport> sports = sportService.getOutdoorSports();
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/team")
    public ResponseEntity<List<Sport>> getTeamSports() {
        List<Sport> sports = sportService.getTeamSports();
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/individual")
    public ResponseEntity<List<Sport>> getIndividualSports() {
        List<Sport> sports = sportService.getIndividualSports();
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<Sport>> getPopularSports(@RequestParam(defaultValue = "10") int limit) {
        List<Sport> sports = sportService.getPopularSports(limit);
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = sportService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{sportId}/stats")
    public ResponseEntity<SportService.SportStatistics> getSportStatistics(@PathVariable String sportId) {
        try {
            SportService.SportStatistics stats = sportService.getSportStatistics(sportId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}