package com.moveup.dto;

public class InstructorMapDTO {
    private String id;
    private String name;
    private CoordinateDTO coordinate;
    private String sport;
    private Double rating;
    private Integer distanceMeters;
    private Double hourlyRate;
    
    // Constructors
    public InstructorMapDTO() {}
    
    public InstructorMapDTO(String id, String name, CoordinateDTO coordinate, String sport, Double rating) {
        this.id = id;
        this.name = name;
        this.coordinate = coordinate;
        this.sport = sport;
        this.rating = rating;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public CoordinateDTO getCoordinate() { return coordinate; }
    public void setCoordinate(CoordinateDTO coordinate) { this.coordinate = coordinate; }
    
    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public Integer getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(Integer distanceMeters) { this.distanceMeters = distanceMeters; }
    
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
}
