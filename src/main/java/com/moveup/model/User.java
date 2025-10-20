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
    private String profileImageBase64; // Immagine profilo salvata come Base64
    
    @NotNull
    private UserType userType;
    
    private boolean isVerified = false;
    private String verificationToken;
    private LocalDateTime verifiedAt;
    private boolean isActive = true;
    private LocalDateTime deactivatedAt;
    private String resetPasswordToken;
    
    // First lesson promotion tracking
    private boolean hasUsedFirstLesson = false;
    private int freeLessonCredits = 0; // Number of free lessons available
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
    
    // Premium features
    private List<String> unlockedPremiumFeatures = new ArrayList<>();
    
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
    private Double hourlyRate; // €/
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
    
    public String getProfileImageBase64() { return profileImageBase64; }
    public void setProfileImageBase64(String profileImageBase64) { this.profileImageBase64 = profileImageBase64; }
    
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
    
    public boolean getHasUsedFirstLesson() { return hasUsedFirstLesson; }
    public void setHasUsedFirstLesson(boolean hasUsedFirstLesson) { this.hasUsedFirstLesson = hasUsedFirstLesson; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public List<String> getBadges() { return badges; }
    public void setBadges(List<String> badges) { this.badges = badges; }
    
    // New onboarding fields
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public LocalDate getDateOfBirth() { return birthDate; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.birthDate = dateOfBirth; }
    
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
    
    // Reward redemption methods (delegate to GameStatus)
    public void addRedeemedReward(String rewardId) {
        if (gameStatus == null) {
            gameStatus = new GameStatus();
        }
        gameStatus.addRedeemedReward(rewardId);
    }
    
    public boolean hasRedeemedReward(String rewardId) {
        return gameStatus != null && gameStatus.hasRedeemedReward(rewardId);
    }
    
    // Placeholder methods for reward features (to be implemented)
    public void addFreeLessonCredit() {
        this.freeLessonCredits++;
    }
    
    public boolean useFreeLessonCredit() {
        if (this.freeLessonCredits > 0) {
            this.freeLessonCredits--;
            return true;
        }
        return false;
    }
    
    public int getFreeLessonCredits() {
        return this.freeLessonCredits;
    }
    
    public void unlockPremiumFeature(String feature) {
        if (this.unlockedPremiumFeatures == null) {
            this.unlockedPremiumFeatures = new ArrayList<>();
        }
        if (!this.unlockedPremiumFeatures.contains(feature)) {
            this.unlockedPremiumFeatures.add(feature);
        }
    }
    
    public boolean hasPremiumFeature(String feature) {
        return this.unlockedPremiumFeatures != null && this.unlockedPremiumFeatures.contains(feature);
    }
    
    public List<String> getUnlockedPremiumFeatures() {
        return this.unlockedPremiumFeatures != null ? this.unlockedPremiumFeatures : new ArrayList<>();
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

// UserPreferences inner class (package-private, only used internally)
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
