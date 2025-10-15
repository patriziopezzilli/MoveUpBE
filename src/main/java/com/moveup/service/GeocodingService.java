package com.moveup.service;

import com.moveup.dto.LocationDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;
import org.json.JSONArray;

@Service
public class GeocodingService {
    
    private final RestTemplate restTemplate;
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org";
    
    public GeocodingService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Reverse geocoding: converte coordinate (lat, lon) in indirizzo
     */
    public LocationDTO reverseGeocode(Double latitude, Double longitude) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(NOMINATIM_URL + "/reverse")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .queryParam("format", "json")
                    .queryParam("addressdetails", 1)
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);
            JSONObject address = json.getJSONObject("address");
            
            LocationDTO location = new LocationDTO();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setCity(extractCity(address));
            location.setCountry(address.optString("country", ""));
            location.setPostalCode(address.optString("postcode", ""));
            location.setFormattedAddress(json.optString("display_name", ""));
            location.setSource("GEOCODED");
            
            return location;
            
        } catch (Exception e) {
            System.err.println("Errore geocoding reverse: " + e.getMessage());
            // Fallback
            LocationDTO location = new LocationDTO();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setCity("Posizione sconosciuta");
            location.setSource("GPS");
            return location;
        }
    }
    
    /**
     * Forward geocoding: converte indirizzo in coordinate
     */
    public LocationDTO forwardGeocode(String address) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(NOMINATIM_URL + "/search")
                    .queryParam("q", address)
                    .queryParam("format", "json")
                    .queryParam("addressdetails", 1)
                    .queryParam("limit", 1)
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(url, String.class);
            JSONArray results = new JSONArray(response);
            
            if (results.length() > 0) {
                JSONObject result = results.getJSONObject(0);
                JSONObject addressDetails = result.getJSONObject("address");
                
                LocationDTO location = new LocationDTO();
                location.setLatitude(result.getDouble("lat"));
                location.setLongitude(result.getDouble("lon"));
                location.setCity(extractCity(addressDetails));
                location.setCountry(addressDetails.optString("country", ""));
                location.setPostalCode(addressDetails.optString("postcode", ""));
                location.setFormattedAddress(result.optString("display_name", ""));
                location.setSource("GEOCODED");
                
                return location;
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Errore geocoding forward: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Estrae il nome della città dall'oggetto address
     */
    private String extractCity(JSONObject address) {
        // Prova diversi campi in ordine di preferenza
        if (address.has("city")) {
            return address.getString("city");
        } else if (address.has("town")) {
            return address.getString("town");
        } else if (address.has("village")) {
            return address.getString("village");
        } else if (address.has("municipality")) {
            return address.getString("municipality");
        } else if (address.has("county")) {
            return address.getString("county");
        }
        return "Sconosciuta";
    }
    
    /**
     * Calcola la distanza tra due punti (in metri) usando la formula di Haversine
     */
    public double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int EARTH_RADIUS = 6371000; // metri
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
}
