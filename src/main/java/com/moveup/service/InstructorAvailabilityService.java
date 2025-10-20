package com.moveup.service;

import com.moveup.model.Instructor;
import com.moveup.model.InstructorAvailability;
import com.moveup.repository.InstructorRepository;
import com.moveup.repository.InstructorAvailabilityRepository;
import com.moveup.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructorAvailabilityService {
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private InstructorAvailabilityRepository availabilityRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    /**
     * INSTANT BOOKING: Trova trainer disponibili SUBITO nelle prossime 2 ore
     * Stile "Uber for Sports"
     */
    public List<InstantBookingResult> findAvailableNow(
            Double userLat,
            Double userLng,
            Double radiusKm,
            String sport,
            Integer maxResults
    ) {
        // 1. Trova trainer nel raggio geografico
        double radiusMeters = (radiusKm != null ? radiusKm : 10.0) * 1000; // converti km in metri
        List<Instructor> nearbyInstructors = instructorRepository.findByLocationWithinRadius(
            userLat, userLng, radiusMeters
        );

        // 2. Filtra per sport se specificato
        if (sport != null && !sport.isEmpty()) {
            nearbyInstructors = nearbyInstructors.stream()
                .filter(instructor -> instructor.getSpecializations() != null &&
                                    instructor.getSpecializations().contains(sport))
                .collect(Collectors.toList());
        }

        // 3. Filtra trainer attivi e disponibili
        nearbyInstructors = nearbyInstructors.stream()
            .filter(instructor -> instructor.isActive() && instructor.isAvailable())
            .collect(Collectors.toList());

        // 4. Crea risultati con calcolo distanza e ETA
        List<InstantBookingResult> results = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Instructor instructor : nearbyInstructors) {
            // Calcola distanza
            double distance = calculateDistance(
                userLat, userLng,
                instructor.getLocation().getLatitude(),
                instructor.getLocation().getLongitude()
            );

            // ETA stimato: 5 min + (distanza * 2 min/km per traffico)
            int etaMinutes = 5 + (int)(distance * 2);

            // Prossimo slot disponibile (semplificato: ora + ETA)
            LocalTime nextSlot = now.plusMinutes(etaMinutes).toLocalTime();

            // Prezzo (usa tariffa oraria come base)
            Double price = instructor.getHourlyRate();

            results.add(new InstantBookingResult(
                instructor,
                distance,
                etaMinutes,
                nextSlot,
                instructor.getLocation().getAddress(),
                price
            ));
        }

        // 5. Ordina per ETA e limita risultati
        return results.stream()
            .sorted(Comparator.comparing(InstantBookingResult::getEtaMinutes))
            .limit(maxResults != null ? maxResults : 10)
            .collect(Collectors.toList());
    }
    
    /**
     * Trova trainer disponibili in un range orario specifico
     * TODO: Implement with proper repository methods and model fields
     */
    public List<InstantBookingResult> findAvailableInTimeRange(
            Double userLat,
            Double userLng,
            Double radiusKm,
            String sport,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        // TODO: This method requires:
        // - findByLocationNear in InstructorRepository
        // - findByInstructorIdAndDayOfWeek in InstructorAvailabilityRepository
        // - getStartTime/getEndTime in InstructorAvailability (use TimeSlot objects)
        // - getLatitude/getLongitude in Instructor model
        // - getLocation in InstructorAvailability model
        throw new RuntimeException("findAvailableInTimeRange not yet implemented");
    }
    
    // Haversine formula per calcolare distanza tra due coordinate
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raggio della Terra in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    private Double calculatePriceForSport(Instructor instructor, String sport) {
        // Logica per calcolare il prezzo basato sullo sport
        // Per ora ritorna un prezzo di default basato sulla tariffa oraria
        double rate = instructor.getHourlyRate();
        return rate > 0 ? rate : 40.0;
    }
    
    // Inner class per il risultato
    public static class InstantBookingResult {
        private Instructor instructor;
        private double distance; // in km
        private int etaMinutes; // tempo stimato di arrivo
        private LocalTime nextAvailableSlot;
        private String location;
        private Double price;
        
        public InstantBookingResult(
                Instructor instructor,
                double distance,
                int etaMinutes,
                LocalTime nextAvailableSlot,
                String location,
                Double price
        ) {
            this.instructor = instructor;
            this.distance = distance;
            this.etaMinutes = etaMinutes;
            this.nextAvailableSlot = nextAvailableSlot;
            this.location = location;
            this.price = price;
        }
        
        // Getters
        public Instructor getInstructor() { return instructor; }
        public double getDistance() { return distance; }
        public int getEtaMinutes() { return etaMinutes; }
        public LocalTime getNextAvailableSlot() { return nextAvailableSlot; }
        public String getLocation() { return location; }
        public Double getPrice() { return price; }
        
        public String getFormattedDistance() {
            if (distance < 1.0) {
                return String.format("%.0f m", distance * 1000);
            }
            return String.format("%.1f km", distance);
        }
        
        public String getFormattedEta() {
            if (etaMinutes < 60) {
                return etaMinutes + " min";
            }
            int hours = etaMinutes / 60;
            int mins = etaMinutes % 60;
            return hours + "h " + mins + "m";
        }
        
        public boolean isAvailableNow() {
            return etaMinutes <= 30; // Disponibile se raggiungibile in 30 min
        }
    }
}
