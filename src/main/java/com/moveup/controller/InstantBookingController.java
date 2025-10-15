package com.moveup.controller;

import com.moveup.service.InstructorAvailabilityService;
import com.moveup.service.InstructorAvailabilityService.InstantBookingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instant-booking")
@CrossOrigin(origins = "*")
public class InstantBookingController {
    
    @Autowired
    private InstructorAvailabilityService availabilityService;
    
    /**
     * INSTANT BOOKING: Trova trainer disponibili ORA nelle prossime 2 ore
     * Stile "Uber for Sports"
     * 
     * GET /api/instant-booking/now?lat=45.4642&lng=9.1900&radius=5&sport=Tennis
     */
    @GetMapping("/now")
    public ResponseEntity<Map<String, Object>> findAvailableNow(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false, defaultValue = "10.0") Double radius,
            @RequestParam(required = false) String sport,
            @RequestParam(required = false, defaultValue = "20") Integer maxResults
    ) {
        try {
            List<InstantBookingResult> results = availabilityService.findAvailableNow(
                lat, lng, radius, sport, maxResults
            );
            
            // Separa risultati in categorie
            List<InstantBookingResult> availableNow = results.stream()
                .filter(InstantBookingResult::isAvailableNow)
                .toList();
            
            List<InstantBookingResult> availableSoon = results.stream()
                .filter(r -> !r.isAvailableNow())
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "availableNow", availableNow,
                "availableSoon", availableSoon,
                "totalFound", results.size(),
                "searchRadius", radius,
                "userLocation", Map.of("lat", lat, "lng", lng)
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Trova trainer disponibili in un range orario specifico
     * 
     * GET /api/instant-booking/time-range?lat=45.4642&lng=9.1900&startTime=2025-10-14T18:00&endTime=2025-10-14T20:00
     */
    @GetMapping("/time-range")
    public ResponseEntity<Map<String, Object>> findAvailableInTimeRange(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false, defaultValue = "10.0") Double radius,
            @RequestParam(required = false) String sport,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        try {
            List<InstantBookingResult> results = availabilityService.findAvailableInTimeRange(
                lat, lng, radius, sport, startTime, endTime
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "results", results,
                "totalFound", results.size(),
                "timeRange", Map.of(
                    "start", startTime,
                    "end", endTime
                )
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Quick stats per la home "X trainer disponibili ora vicino a te"
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getInstantStats(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false, defaultValue = "5.0") Double radius
    ) {
        try {
            List<InstantBookingResult> results = availabilityService.findAvailableNow(
                lat, lng, radius, null, null
            );
            
            long availableNowCount = results.stream()
                .filter(InstantBookingResult::isAvailableNow)
                .count();
            
            // Conta sport unici disponibili
            long uniqueSportsCount = results.stream()
                .flatMap(r -> r.getInstructor().getSports().stream())
                .distinct()
                .count();
            
            return ResponseEntity.ok(Map.of(
                "availableNowCount", availableNowCount,
                "totalInRadius", results.size(),
                "uniqueSports", uniqueSportsCount,
                "radius", radius
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
