package com.moveup.dto;

import com.moveup.model.SkillLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Map;

public class RegisterWithOnboardingDTO {
    
    @Email(message = "Email non valida")
    @NotBlank(message = "Email obbligatoria")
    private String email;
    
    @NotBlank(message = "Password obbligatoria")
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    private String password;
    
    @NotBlank(message = "Nome obbligatorio")
    private String firstName;
    
    @NotBlank(message = "Cognome obbligatorio")
    private String lastName;
    
    @NotNull(message = "Tipo utente obbligatorio")
    private String userType; // "USER" or "INSTRUCTOR"
    
    // Profile data
    private String bio;
    private String phoneNumber;
    
    @Past(message = "La data di nascita deve essere nel passato")
    private LocalDate birthDate;
    
    // Sports with skill levels
    private Map<String, String> sportSkillLevels; // sportId -> "BEGINNER"|"INTERMEDIATE"|"ADVANCED"|"PROFESSIONAL"
    
    // Location data
    private LocationDTO location;
    
    private Double maxDistance = 10.0; // km
    private Boolean notificationsEnabled = true;
    private Boolean marketingEnabled = false;
    
    // Instructor-specific fields
    private String[] certifications;
    private String experience;
    private Double hourlyRate;
    
    // Constructors
    public RegisterWithOnboardingDTO() {}
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public Map<String, String> getSportSkillLevels() { return sportSkillLevels; }
    public void setSportSkillLevels(Map<String, String> sportSkillLevels) { this.sportSkillLevels = sportSkillLevels; }
    
    public LocationDTO getLocation() { return location; }
    public void setLocation(LocationDTO location) { this.location = location; }
    
    public Double getMaxDistance() { return maxDistance; }
    public void setMaxDistance(Double maxDistance) { this.maxDistance = maxDistance; }
    
    public Boolean getNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
    
    public Boolean getMarketingEnabled() { return marketingEnabled; }
    public void setMarketingEnabled(Boolean marketingEnabled) { this.marketingEnabled = marketingEnabled; }
    
    public String[] getCertifications() { return certifications; }
    public void setCertifications(String[] certifications) { this.certifications = certifications; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
}
