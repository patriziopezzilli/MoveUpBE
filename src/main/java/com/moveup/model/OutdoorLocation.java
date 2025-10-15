package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "outdoor_locations")
public class OutdoorLocation {
    
    @Id
    private String id;
    
    private String name;
    private String type; // PARK, BEACH, TRAIL, etc.
    private String description;
    
    @GeoSpatialIndexed
    private GeoJsonPoint location;
    
    private String address;
    private List<String> amenities = new ArrayList<>();
    private List<String> photos = new ArrayList<>();
    private Double rating;
    
    // Constructors
    public OutdoorLocation() {}
    
    public OutdoorLocation(String name, String type, GeoJsonPoint location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public GeoJsonPoint getLocation() { return location; }
    public void setLocation(GeoJsonPoint location) { this.location = location; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    
    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}
