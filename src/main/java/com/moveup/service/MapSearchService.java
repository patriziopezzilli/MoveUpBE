package com.moveup.service;

import com.moveup.dto.CoordinateDTO;
import com.moveup.dto.InstructorMapDTO;
import com.moveup.model.Instructor;
import com.moveup.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapSearchService {
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private GeocodingService geocodingService;
    
    /**
     * Find nearby instructors based on coordinates and filters
     * @param latitude Current user latitude
     * @param longitude Current user longitude
     * @param maxDistanceKm Maximum distance in kilometers
     * @param sport Filter by sport (optional)
     * @param minRating Minimum rating (optional)
     * @return List of instructors within range
     */
    public List<InstructorMapDTO> findNearbyInstructors(
            Double latitude, 
            Double longitude, 
            Double maxDistanceKm,
            String sport,
            Double minRating
    ) {
        // Get all instructors (in production, use geospatial query)
        List<Instructor> instructors = instructorRepository.findAll();
        
        List<InstructorMapDTO> nearbyInstructors = new ArrayList<>();
        
        for (Instructor instructor : instructors) {
            // Skip if no location
            if (instructor.getLocation() == null || 
                instructor.getLocation().getLatitude() == 0.0 || 
                instructor.getLocation().getLongitude() == 0.0) {
                continue;
            }
            
            // Calculate distance
            double distanceMeters = geocodingService.calculateDistance(
                latitude, 
                longitude,
                instructor.getLocation().getLatitude(),
                instructor.getLocation().getLongitude()
            );
            
            double distanceKm = distanceMeters / 1000.0;
            
            // Check if within range
            if (distanceKm <= maxDistanceKm) {
                // Apply filters
                if (sport != null && !sport.isEmpty()) {
                    if (instructor.getSpecializations() == null || 
                        !instructor.getSpecializations().contains(sport)) {
                        continue;
                    }
                }
                
                if (minRating != null) {
                    if (instructor.getRating() < minRating) {
                        continue;
                    }
                }
                
                // Build DTO
                InstructorMapDTO dto = new InstructorMapDTO();
                dto.setId(instructor.getId());
                dto.setName(instructor.getFirstName() + " " + instructor.getLastName());
                
                CoordinateDTO coordinate = new CoordinateDTO();
                coordinate.setLatitude(instructor.getLocation().getLatitude());
                coordinate.setLongitude(instructor.getLocation().getLongitude());
                dto.setCoordinate(coordinate);
                
                // Get primary sport (first in specializations)
                if (instructor.getSpecializations() != null && !instructor.getSpecializations().isEmpty()) {
                    String primarySport = instructor.getSpecializations().get(0);
                    dto.setSport(primarySport);
                }
                
                dto.setRating(instructor.getRating());
                dto.setDistanceMeters((int) distanceMeters);
                dto.setHourlyRate(instructor.getHourlyRate());
                
                nearbyInstructors.add(dto);
            }
        }
        
        // Sort by distance
        nearbyInstructors.sort((a, b) -> 
            Integer.compare(a.getDistanceMeters(), b.getDistanceMeters())
        );
        
        return nearbyInstructors;
    }
    
    /**
     * Find nearby events (trainings, workshops)
     * TODO: Implement when Event model is created
     */
    public List<?> findNearbyEvents(
            Double latitude, 
            Double longitude, 
            Double maxDistanceKm,
            String sport
    ) {
        // Placeholder for future implementation
        return new ArrayList<>();
    }
}
