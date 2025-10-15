package com.moveup.controller;

import com.moveup.dto.LocationDTO;
import com.moveup.service.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geocoding")
@CrossOrigin(origins = "*")
public class GeocodingController {
    
    @Autowired
    private GeocodingService geocodingService;
    
    /**
     * Reverse geocoding: lat/lon → città
     * GET /api/geocoding/reverse?latitude=41.9028&longitude=12.4964
     */
    @GetMapping("/reverse")
    public ResponseEntity<LocationDTO> reverseGeocode(
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        LocationDTO location = geocodingService.reverseGeocode(latitude, longitude);
        return ResponseEntity.ok(location);
    }
    
    /**
     * Forward geocoding: indirizzo → lat/lon
     * GET /api/geocoding/forward?address=Milano,Italia
     */
    @GetMapping("/forward")
    public ResponseEntity<LocationDTO> forwardGeocode(
            @RequestParam String address
    ) {
        LocationDTO location = geocodingService.forwardGeocode(address);
        
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Calcola distanza tra due punti
     * GET /api/geocoding/distance?lat1=41.9&lon1=12.5&lat2=45.4&lon2=9.2
     */
    @GetMapping("/distance")
    public ResponseEntity<DistanceResponse> calculateDistance(
            @RequestParam Double lat1,
            @RequestParam Double lon1,
            @RequestParam Double lat2,
            @RequestParam Double lon2
    ) {
        double distanceMeters = geocodingService.calculateDistance(lat1, lon1, lat2, lon2);
        double distanceKm = distanceMeters / 1000.0;
        
        DistanceResponse response = new DistanceResponse();
        response.setDistanceMeters(distanceMeters);
        response.setDistanceKm(distanceKm);
        
        return ResponseEntity.ok(response);
    }
    
    // Inner class per response
    public static class DistanceResponse {
        private double distanceMeters;
        private double distanceKm;
        
        public double getDistanceMeters() { return distanceMeters; }
        public void setDistanceMeters(double distanceMeters) { this.distanceMeters = distanceMeters; }
        
        public double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    }
}
