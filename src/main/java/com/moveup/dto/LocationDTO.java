package com.moveup.dto;

public class LocationDTO {
    private String city;
    private Double latitude;
    private Double longitude;
    private String formattedAddress;
    private String country;
    private String postalCode;
    private String source; // "GPS", "MANUAL", "GEOCODED"
    
    // Constructors
    public LocationDTO() {}
    
    public LocationDTO(String city, Double latitude, Double longitude) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public String getFormattedAddress() { return formattedAddress; }
    public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
