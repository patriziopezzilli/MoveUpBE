package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Email
    @NotBlank
    @Indexed(unique = true)
    private String email;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private String phoneNumber;
    private String profileImageUrl;
    
    @NotNull
    private UserType userType;
    
    private boolean isVerified = false;
    private String verificationToken;
    private LocalDateTime verifiedAt;
    private boolean isActive = true;
    private LocalDateTime deactivatedAt;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpiry;
    private int points = 0;
    private List<String> badges = new ArrayList<>();
    
    // Onboarding data
    private String bio;
    private LocalDate birthDate;
    private Double maxDistance = 10.0; // km - default 10km radius
    private Boolean notificationsEnabled = true;
    private Boolean marketingEnabled = false;
    
    // Sports with skill levels (per-sport expertise)
    private Map<String, SkillLevel> sportSkillLevels = new HashMap<>();
    // Example: {"tennis_id": "INTERMEDIATE", "fitness_id": "BEGINNER"}
    
    // Legacy fields - to be deprecated
    @Deprecated
    private List<String> sportsInterests = new ArrayList<>();
    @Deprecated
    private SkillLevel skillLevel;
    
    private Location location;
    private UserPreferences preferences = new UserPreferences();
    private GameStatus gameStatus = new GameStatus();
    
    // Instructor-specific fields
    private List<String> certifications = new ArrayList<>();
    private String experience; // Years of experience
    private Double hourlyRate; // €/hour
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {}
    
    public User(String email, String password, String firstName, String lastName, UserType userType) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
    
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    
    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
    
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getDeactivatedAt() { return deactivatedAt; }
    public void setDeactivatedAt(LocalDateTime deactivatedAt) { this.deactivatedAt = deactivatedAt; }
    
    public String getResetPasswordToken() { return resetPasswordToken; }
    public void setResetPasswordToken(String resetPasswordToken) { this.resetPasswordToken = resetPasswordToken; }
    
    public LocalDateTime getResetPasswordTokenExpiry() { return resetPasswordTokenExpiry; }
    public void setResetPasswordTokenExpiry(LocalDateTime resetPasswordTokenExpiry) { this.resetPasswordTokenExpiry = resetPasswordTokenExpiry; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public List<String> getBadges() { return badges; }
    public void setBadges(List<String> badges) { this.badges = badges; }
    
    // New onboarding fields
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public Double getMaxDistance() { return maxDistance; }
    public void setMaxDistance(Double maxDistance) { this.maxDistance = maxDistance; }
    
    public Boolean getNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
    
    public Boolean getMarketingEnabled() { return marketingEnabled; }
    public void setMarketingEnabled(Boolean marketingEnabled) { this.marketingEnabled = marketingEnabled; }
    
    public Map<String, SkillLevel> getSportSkillLevels() { return sportSkillLevels; }
    public void setSportSkillLevels(Map<String, SkillLevel> sportSkillLevels) { this.sportSkillLevels = sportSkillLevels; }
    
    // Instructor fields
    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    
    // Legacy fields - deprecated
    @Deprecated
    public List<String> getSportsInterests() { return sportsInterests; }
    @Deprecated
    public void setSportsInterests(List<String> sportsInterests) { this.sportsInterests = sportsInterests; }
    
    @Deprecated
    public SkillLevel getSkillLevel() { return skillLevel; }
    @Deprecated
    public void setSkillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public UserPreferences getPreferences() { return preferences; }
    public void setPreferences(UserPreferences preferences) { this.preferences = preferences; }
    
    public GameStatus getGameStatus() { return gameStatus; }
    public void setGameStatus(GameStatus gameStatus) { this.gameStatus = gameStatus; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }
    
    public void deductPoints(int pointsToDeduct) {
        this.points -= pointsToDeduct;
        if (this.points < 0) this.points = 0;
    }
    
    public void addBadge(String badgeId) {
        if (!badges.contains(badgeId)) {
            badges.add(badgeId);
        }
    }
    
    public boolean hasBadge(String badgeId) {
        return badges.contains(badgeId);
    }
    
    public void addExperience(int experiencePoints) {
        if (gameStatus != null) {
            gameStatus.setExperiencePoints(gameStatus.getExperiencePoints() + experiencePoints);
        }
    }
    
    // Convenience methods for location
    public String getAddress() {
        return location != null ? location.getAddress() : null;
    }
    
    public void setAddress(String address) {
        if (location == null) {
            location = new Location();
        }
        location.setAddress(address);
    }
}

// Enum classes
public static enum UserType {
    USER, INSTRUCTOR, ADMIN
}

public static enum SkillLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, PROFESSIONAL
}

// Embedded classes
public static class Location {
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

class UserPreferences {
    private boolean notifications = true;
    private boolean newsletter = true;
    private String language = "it";
    
    // Getters and Setters
    public boolean isNotifications() { return notifications; }
    public void setNotifications(boolean notifications) { this.notifications = notifications; }
    
    public boolean isNewsletter() { return newsletter; }
    public void setNewsletter(boolean newsletter) { this.newsletter = newsletter; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}

public static class GameStatus {
    private int totalPoints = 0;
    private int level = 1;
    private List<String> badges = new ArrayList<>();
    private int totalLessons = 0;
    private int totalReviews = 0;
    private double averageRating = 0.0;
    private int experiencePoints = 0;
    private List<String> redeemedRewards = new ArrayList<>();
    
    // Getters and Setters
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public List<String> getBadges() { return badges; }
    public void setBadges(List<String> badges) { this.badges = badges; }
    
    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }
    
    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
    
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    
    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }
    
    public List<String> getRedeemedRewards() { return redeemedRewards; }
    public void setRedeemedRewards(List<String> redeemedRewards) { this.redeemedRewards = redeemedRewards; }
}
