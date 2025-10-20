package com.moveup.service;

import com.moveup.model.OutdoorLocation;
import com.moveup.repository.OutdoorLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OutdoorLocationService {
    
    @Autowired
    private OutdoorLocationRepository locationRepository;
    
    /**
     * Trova location outdoor vicine all'utente
     */
    public List<OutdoorLocationResult> findNearbyOutdoorLocations(
            Double userLat,
            Double userLng,
            Double radiusKm,
            String locationType,
            String sport
    ) {
        // Converti coordinate in GeoJsonPoint
        GeoJsonPoint userLocation = new GeoJsonPoint(userLng, userLat);
        double radiusInMeters = (radiusKm != null ? radiusKm : 10.0) * 1000;
        
        List<OutdoorLocation> locations = locationRepository.findByLocationNear(
            userLocation, 
            radiusInMeters
        );
        
        // Filtra per tipo se specificato
        if (locationType != null && !locationType.isEmpty()) {
            locations = locations.stream()
                .filter(loc -> loc.getType() != null && loc.getType().equalsIgnoreCase(locationType))
                .collect(Collectors.toList());
        }
        
        // Converti in risultati con distanza calcolata
        List<OutdoorLocationResult> results = new ArrayList<>();
        for (OutdoorLocation location : locations) {
            double distance = calculateDistance(
                userLat, userLng,
                location.getLatitude(),
                location.getLongitude()
            );
            
            List<String> photos = getLocationPhotos(location);
            List<String> amenities = getAmenities(location);
            
            results.add(new OutdoorLocationResult(location, distance, photos, amenities));
        }
        
        // Ordina per distanza
        results.sort(Comparator.comparing(OutdoorLocationResult::getDistance));
        
        return results;
    }
    
    /**
     * Ottieni location famose per città (con foto iconiche)
     */
    @Cacheable("famousLocations")
    public List<FamousLocation> getFamousLocationsByCity(String city) {
        return switch (city.toLowerCase()) {
            case "milano" -> List.of(
                new FamousLocation("Parco Sempione", "park", "milan_sempione.jpg", 
                    45.4729, 9.1805, "Iconico parco nel cuore di Milano", 
                    List.of("Tennis", "Running", "Yoga", "Fitness")),
                new FamousLocation("Idroscalo", "lake", "milan_idroscalo.jpg",
                    45.4556, 9.2794, "Lago con spiaggia e sport acquatici",
                    List.of("SUP", "Kayak", "Beach Volleyball", "Running")),
                new FamousLocation("Parco Nord", "park", "milan_parconord.jpg",
                    45.5333, 9.2000, "Grande parco verde con piste ciclabili",
                    List.of("Cycling", "Running", "Fitness"))
            );
            
            case "roma" -> List.of(
                new FamousLocation("Villa Borghese", "park", "rome_villaborghese.jpg",
                    41.9142, 12.4922, "Parco storico con viste panoramiche",
                    List.of("Tennis", "Running", "Yoga")),
                new FamousLocation("Ostia Beach", "beach", "rome_ostia.jpg",
                    41.7313, 12.2881, "Spiaggia vicino a Roma",
                    List.of("Beach Volleyball", "SUP", "Surf"))
            );
            
            case "firenze" -> List.of(
                new FamousLocation("Parco delle Cascine", "park", "florence_cascine.jpg",
                    43.7811, 11.2178, "Storico parco lungo l'Arno",
                    List.of("Running", "Cycling", "Tennis"))
            );
            
            default -> List.of();
        };
    }
    
    /**
     * Ottieni location per tipo (park, beach, mountain, lake)
     */
    public Map<String, List<OutdoorLocationResult>> getLocationsByType(
            Double userLat,
            Double userLng,
            Double radiusKm
    ) {
        // TODO: Implement with proper OutdoorLocation model fields
        return new HashMap<>();
        /*
        List<OutdoorLocation> allLocations = locationRepository.findByLocationNear(
            userLat, userLng, radiusKm
        );
        
        Map<String, List<OutdoorLocationResult>> grouped = allLocations.stream()
            .map(location -> {
                double distance = calculateDistance(
                    userLat, userLng,
                    location.getLatitude(), location.getLongitude()
                );
                return new OutdoorLocationResult(location, distance, 
                    getLocationPhotos(location), getAmenities(location));
            })
            .collect(Collectors.groupingBy(
                result -> result.getLocation().getType()
            ));
        
        return grouped;
        */
    }
    
    // Haversine distance calculation
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    private List<String> getLocationPhotos(OutdoorLocation location) {
        String photoBase64 = location.getPhotoBase64();
        return photoBase64 != null ? List.of(photoBase64) : List.of();
    }
    
    private List<String> getAmenities(OutdoorLocation location) {
        List<String> amenities = new ArrayList<>();
        if (location.hasParking()) amenities.add("Parking");
        if (location.hasRestrooms()) amenities.add("Restrooms");
        if (location.hasWater()) amenities.add("Water");
        if (location.hasShade()) amenities.add("Shade");
        return amenities;
    }
    
    // Inner classes
    public static class OutdoorLocationResult {
        private OutdoorLocation location;
        private double distance;
        private List<String> photos;
        private List<String> amenities;
        
        public OutdoorLocationResult(OutdoorLocation location, double distance, 
                                    List<String> photos, List<String> amenities) {
            this.location = location;
            this.distance = distance;
            this.photos = photos;
            this.amenities = amenities;
        }
        
        public OutdoorLocation getLocation() { return location; }
        public double getDistance() { return distance; }
        public List<String> getPhotos() { return photos; }
        public List<String> getAmenities() { return amenities; }
        
        public String getFormattedDistance() {
            if (distance < 1.0) {
                return String.format("%.0f m", distance * 1000);
            }
            return String.format("%.1f km", distance);
        }
    }
    
    public static class FamousLocation {
        private String name;
        private String type;
        private String photoUrl;
        private double latitude;
        private double longitude;
        private String description;
        private List<String> suitableFor;
        
        public FamousLocation(String name, String type, String photoUrl, 
                             double latitude, double longitude, 
                             String description, List<String> suitableFor) {
            this.name = name;
            this.type = type;
            this.photoUrl = photoUrl;
            this.latitude = latitude;
            this.longitude = longitude;
            this.description = description;
            this.suitableFor = suitableFor;
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public String getPhotoBase64() { return photoUrl; } // Per ora restituisce il nome file, poi sarà Base64
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getDescription() { return description; }
        public List<String> getSuitableFor() { return suitableFor; }
    }
}
