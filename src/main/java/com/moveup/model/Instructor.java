package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "instructors")
public class Instructor {
    
    @Id
    private String id;
    
    @NotNull
    @Indexed
    private String userId; // Reference to User
    
    @NotBlank
    private String bio;
    
    private List<String> specializations = new ArrayList<>(); // Sport IDs
    private List<Certification> certifications = new ArrayList<>();
    
    @Min(0)
    private double hourlyRate;
    
    private List<Availability> availability = new ArrayList<>();
    
    @NotNull
    private Location location;
    
    private boolean isApproved = false;
    private double rating = 0.0;
    private int totalLessons = 0;
    private double totalEarnings = 0.0;
    private double profileCompletion = 0.0;
    private InstructorAnalytics analytics = new InstructorAnalytics();
    
    // QR Code & Apple Wallet Pass
    private InstructorQRPassInfo qrPass;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public Instructor() {}
    
    public Instructor(String userId, String bio, double hourlyRate, Location location) {
        this.userId = userId;
        this.bio = bio;
        this.hourlyRate = hourlyRate;
        this.location = location;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public List<String> getSpecializations() { return specializations; }
    public void setSpecializations(List<String> specializations) { this.specializations = specializations; }
    
    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }
    
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public List<Availability> getAvailability() { return availability; }
    public void setAvailability(List<Availability> availability) { this.availability = availability; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }
    
    public double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; }
    
    public double getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(double profileCompletion) { this.profileCompletion = profileCompletion; }
    
    public InstructorAnalytics getAnalytics() { return analytics; }
    public void setAnalytics(InstructorAnalytics analytics) { this.analytics = analytics; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public InstructorQRPassInfo getQrPass() { return qrPass; }
    public void setQrPass(InstructorQRPassInfo qrPass) { this.qrPass = qrPass; }
    
    // Business methods
    public void addSpecialization(String sportId) {
        if (!specializations.contains(sportId)) {
            specializations.add(sportId);
        }
    }
    
    public void updateRating(double newRating) {
        this.rating = newRating;
    }
    
    public void incrementLessons() {
        this.totalLessons++;
    }
    
    public void addEarnings(double amount) {
        this.totalEarnings += amount;
    }
}

// Embedded classes
class Certification {
    private String name;
    private String issuingOrganization;
    private LocalDateTime issueDate;
    private LocalDateTime expirationDate;
    private String certificateUrl;
    private boolean isVerified = false;
    
    // Constructors
    public Certification() {}
    
    public Certification(String name, String issuingOrganization, LocalDateTime issueDate) {
        this.name = name;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIssuingOrganization() { return issuingOrganization; }
    public void setIssuingOrganization(String issuingOrganization) { this.issuingOrganization = issuingOrganization; }
    
    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
    
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    
    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }
    
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}

class Availability {
    private WeekDay dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable = true;
    
    // Constructors
    public Availability() {}
    
    public Availability(WeekDay dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // Getters and Setters
    public WeekDay getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(WeekDay dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}

class InstructorAnalytics {
    private double completionRate = 0.0;
    private long responseTime = 0; // in milliseconds
    private List<MonthlyStats> monthlyStats = new ArrayList<>();
    
    // Getters and Setters
    public double getCompletionRate() { return completionRate; }
    public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }
    
    public long getResponseTime() { return responseTime; }
    public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
    
    public List<MonthlyStats> getMonthlyStats() { return monthlyStats; }
    public void setMonthlyStats(List<MonthlyStats> monthlyStats) { this.monthlyStats = monthlyStats; }
}

class MonthlyStats {
    private int month;
    private int year;
    private int lessonsCount;
    private double earnings;
    private int newStudents;
    
    // Constructors
    public MonthlyStats() {}
    
    public MonthlyStats(int month, int year, int lessonsCount, double earnings, int newStudents) {
        this.month = month;
        this.year = year;
        this.lessonsCount = lessonsCount;
        this.earnings = earnings;
        this.newStudents = newStudents;
    }
    
    // Getters and Setters
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public int getLessonsCount() { return lessonsCount; }
    public void setLessonsCount(int lessonsCount) { this.lessonsCount = lessonsCount; }
    
    public double getEarnings() { return earnings; }
    public void setEarnings(double earnings) { this.earnings = earnings; }
    
    public int getNewStudents() { return newStudents; }
    public void setNewStudents(int newStudents) { this.newStudents = newStudents; }
}

enum WeekDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

// Instructor QR Pass Info
class InstructorQRPassInfo {
    private String serialNumber;
    private LocalDateTime generatedAt;
    private int totalScans = 0;
    private LocalDateTime lastScan;
    private boolean passAddedToWallet = false;
    
    public InstructorQRPassInfo() {}
    
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public int getTotalScans() { return totalScans; }
    public void setTotalScans(int totalScans) { this.totalScans = totalScans; }
    
    public LocalDateTime getLastScan() { return lastScan; }
    public void setLastScan(LocalDateTime lastScan) { this.lastScan = lastScan; }
    
    public boolean isPassAddedToWallet() { return passAddedToWallet; }
    public void setPassAddedToWallet(boolean passAddedToWallet) { this.passAddedToWallet = passAddedToWallet; }
    
    public void recordScan() {
        this.totalScans++;
        this.lastScan = LocalDateTime.now();
    }
}
```