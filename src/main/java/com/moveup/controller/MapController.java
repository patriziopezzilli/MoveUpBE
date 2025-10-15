package com.moveup.controller;

import com.moveup.dto.InstructorMapDTO;
import com.moveup.service.MapSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@CrossOrigin(origins = "*")
public class MapController {
    
    @Autowired
    private MapSearchService mapSearchService;
    
    /**
     * GET /api/map/instructors/nearby
     * Find instructors near a location
     * 
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param maxDistance Maximum distance in kilometers (default 50km)
     * @param sport Filter by sport (optional)
     * @param minRating Minimum rating (optional)
     * @return List of nearby instructors
     */
    @GetMapping("/instructors/nearby")
    public ResponseEntity<List<InstructorMapDTO>> getNearbyInstructors(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "50.0") Double maxDistance,
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) Double minRating
    ) {
        List<InstructorMapDTO> instructors = mapSearchService.findNearbyInstructors(
            latitude, 
            longitude, 
            maxDistance, 
            sport, 
            minRating
        );
        
        return ResponseEntity.ok(instructors);
    }
    
    /**
     * GET /api/map/events/nearby
     * Find events near a location
     * TODO: Implement when Event model is created
     */
    @GetMapping("/events/nearby")
    public ResponseEntity<?> getNearbyEvents(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "50.0") Double maxDistance,
            @RequestParam(required = false) String sport
    ) {
        List<?> events = mapSearchService.findNearbyEvents(
            latitude, 
            longitude, 
            maxDistance, 
            sport
        );
        
        return ResponseEntity.ok(events);
    }
}
