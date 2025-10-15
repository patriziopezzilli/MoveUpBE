package com.moveup.model;

public class Location {
    private double latitude;
    private double longitude;
    private String address;
    private String city;
    private String region;
    private String country;
    
    // New GPS fields
    private String formattedAddress;  // "Via Roma 123, Milano, MI 20100"
    private String postalCode;        // "20100"
    private LocationSource source = LocationSource.MANUAL;  // GPS, MANUAL, GEOCODED
    
    // Constructors
    public Location() {}
    
    public Location(double latitude, double longitude, String address, String city, String region, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.region = region;
        this.country = country;
    }
    
    // Getters and Setters
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getFormattedAddress() { return formattedAddress; }
    public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public LocationSource getSource() { return source; }
    public void setSource(LocationSource source) { this.source = source; }
}

enum LocationSource {
    GPS,        // Obtained from GPS device
    MANUAL,     // Manually entered by user
    GEOCODED    // Calculated from geocoding service
}
