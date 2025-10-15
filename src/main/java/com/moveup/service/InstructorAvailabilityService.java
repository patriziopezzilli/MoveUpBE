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
        // TODO: Implement instant booking with proper repository methods
        return new ArrayList<>(); // Return empty list for now
        
        /*
        // 1. Trova trainer nel raggio geografico
        List<Instructor> nearbyInstructors = instructorRepository.findByLocationNear(
            userLat, 
            userLng, 
            radiusKm != null ? radiusKm : 10.0 // default 10km
        );
        
        // 2. Filtra per sport se specificato
        if (sport != null && !sport.isEmpty()) {
            nearbyInstructors = nearbyInstructors.stream()
                .filter(instructor -> instructor.getSpecializations().contains(sport))
                .collect(Collectors.toList());
        }
        
        // 3. Trova disponibilità nelle prossime 2 ore
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursLater = now.plusHours(2);
        
        List<InstantBookingResult> results = new ArrayList<>();
        
        for (Instructor instructor : nearbyInstructors) {
            // Check disponibilità oggi
            List<InstructorAvailability> todayAvailability = availabilityRepository
                .findByInstructorIdAndDayOfWeek(instructor.getId(), now.getDayOfWeek().getValue());
            
            for (InstructorAvailability availability : todayAvailability) {
                if (!availability.isAvailable()) continue;
                
                // Use availability slots instead of getStartTime/getEndTime
                for (var slot : availability.getAvailableSlots()) {
                    LocalTime availStart = slot.getStartTime();
                    LocalTime availEnd = slot.getEndTime();
                    LocalTime currentTime = now.toLocalTime();
                
                    // Check se la disponibilità copre le prossime 2 ore
                    if (availStart.isBefore(twoHoursLater.toLocalTime()) && 
                        availEnd.isAfter(currentTime)) {
                    
                    // Verifica che non ci siano booking in conflitto
                    boolean hasConflict = !bookingRepository.findConflictingBookings(
                        instructor.getId(),
                        now,
                        currentTime
                    ).isEmpty();
                    
                    if (!hasConflict) {
                        // Calcola distanza
                        double distance = calculateDistance(
                            userLat, userLng,
                            instructor.getLatitude(), instructor.getLongitude()
                        );
                        
                        // Calcola tempo stimato di arrivo (15 km/h media)
                        int etaMinutes = (int) ((distance / 15.0) * 60);
                        
                        // Calcola slot disponibili nelle prossime 2 ore
                        LocalTime nextAvailableSlot = currentTime.isBefore(availStart) 
                            ? availStart 
                            : currentTime.plusMinutes(30 - (currentTime.getMinute() % 30)); // arrotonda a mezz'ora
                        
                        results.add(new InstantBookingResult(
                            instructor,
                            distance,
                            etaMinutes,
                            nextAvailableSlot,
                            availability.getLocation(),
                            calculatePriceForSport(instructor, sport)
                        ));
                    }
                }
            }
        }
        
        // 4. Ordina per distanza e disponibilità
        results.sort(Comparator
            .comparing(InstantBookingResult::getDistance)
            .thenComparing(InstantBookingResult::getEtaMinutes)
        );
        
        // 5. Limita risultati
        if (maxResults != null && results.size() > maxResults) {
            results = results.subList(0, maxResults);
        }
        
        return results;
        */
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
        /*
        List<Instructor> nearbyInstructors = instructorRepository.findByLocationNear(
            userLat, userLng, radiusKm != null ? radiusKm : 10.0
        );
        
        if (sport != null && !sport.isEmpty()) {
            nearbyInstructors = nearbyInstructors.stream()
                .filter(instructor -> instructor.getSports().contains(sport))
                .collect(Collectors.toList());
        }
        
        List<InstantBookingResult> results = new ArrayList<>();
        
        for (Instructor instructor : nearbyInstructors) {
            List<InstructorAvailability> availability = availabilityRepository
                .findByInstructorIdAndDayOfWeek(instructor.getId(), startTime.getDayOfWeek().getValue());
            
            for (InstructorAvailability avail : availability) {
                if (avail.isAvailable() && 
                    avail.getStartTime().isBefore(endTime.toLocalTime()) &&
                    avail.getEndTime().isAfter(startTime.toLocalTime())) {
                    
                    boolean hasConflict = !bookingRepository.findConflictingBookings(
                        instructor.getId(),
                        startTime,
                        startTime.toLocalTime()
                    ).isEmpty();
                    
                    if (!hasConflict) {
                        double distance = calculateDistance(
                            userLat, userLng,
                            instructor.getLatitude(), instructor.getLongitude()
                        );
                        
                        int etaMinutes = (int) ((distance / 15.0) * 60);
                        
                        results.add(new InstantBookingResult(
                            instructor,
                            distance,
                            etaMinutes,
                            startTime.toLocalTime(),
                            avail.getLocation(),
                            calculatePriceForSport(instructor, sport)
                        ));
                    }
                }
            }
        }
        
        results.sort(Comparator.comparing(InstantBookingResult::getDistance));
        
        return results;
        */
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
        // Per ora ritorna un prezzo di default
        return instructor.getHourlyRate() != null ? instructor.getHourlyRate() : 40.0;
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
