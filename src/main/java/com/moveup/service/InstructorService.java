package com.moveup.service;

import com.moveup.model.Instructor;
import com.moveup.model.Sport;
import com.moveup.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class InstructorService {
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;
    
    // Create new instructor
    public Instructor createInstructor(Instructor instructor) {
        // Check if email already exists
        if (instructorRepository.existsByEmail(instructor.getEmail())) {
            throw new RuntimeException("Email già registrata");
        }
        
        // TODO: Refactor - Instructor doesn't have password/username/verification fields
        // These should be managed through associated User entity
        /*
        // Check if username already exists
        if (instructor.getUsername() != null && instructorRepository.existsByUsername(instructor.getUsername())) {
            throw new RuntimeException("Username già utilizzato");
        }
        
        // Encode password
        instructor.setPassword(passwordEncoder.encode(instructor.getPassword()));
        
        // Generate verification token
        instructor.setVerificationToken(UUID.randomUUID().toString());
        
        // Set default values
        instructor.setActive(true);
        instructor.setVerified(false);
        instructor.setAvailable(false); // Needs verification first
        */
        
        Instructor savedInstructor = instructorRepository.save(instructor);
        
        // Send verification email
        emailService.sendInstructorVerificationEmail(savedInstructor);
        
        return savedInstructor;
    }
    
    // Get instructor by ID
    public Optional<Instructor> getInstructorById(String instructorId) {
        return instructorRepository.findById(instructorId);
    }
    
    // Get instructor by email
    public Optional<Instructor> getInstructorByEmail(String email) {
        return instructorRepository.findByEmail(email);
    }
    
    // Update instructor
    public Instructor updateInstructor(String instructorId, Instructor updatedInstructor) {
        Instructor existingInstructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        // Update basic info
        if (updatedInstructor.getFirstName() != null) {
            existingInstructor.setFirstName(updatedInstructor.getFirstName());
        }
        if (updatedInstructor.getLastName() != null) {
            existingInstructor.setLastName(updatedInstructor.getLastName());
        }
        if (updatedInstructor.getDateOfBirth() != null) {
            existingInstructor.setDateOfBirth(updatedInstructor.getDateOfBirth());
        }
        if (updatedInstructor.getPhoneNumber() != null) {
            existingInstructor.setPhoneNumber(updatedInstructor.getPhoneNumber());
        }
        if (updatedInstructor.getAddress() != null) {
            existingInstructor.setAddress(updatedInstructor.getAddress());
        }
        if (updatedInstructor.getProfessionalInfo() != null) {
            existingInstructor.setProfessionalInfo(updatedInstructor.getProfessionalInfo());
        }
        if (updatedInstructor.getSports() != null) {
            existingInstructor.setSports(updatedInstructor.getSports());
        }
        
        return instructorRepository.save(existingInstructor);
    }
    
    // Verify instructor email
    public boolean verifyEmail(String verificationToken) {
        // Delegate email verification to UserService
        return userService.verifyEmail(verificationToken);
    }
    
    // Approve instructor (admin function)
    public void approveInstructor(String instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        instructor.setAvailable(true);
        instructor.setApprovalStatus("APPROVED");
        instructor.setApprovedAt(LocalDateTime.now());
        
        instructorRepository.save(instructor);
        
        // Send approval notification
        notificationService.createNotification(
            instructorId,
            "Account Approvato!",
            "Il tuo account istruttore è stato approvato. Ora puoi iniziare a offrire lezioni!",
            "SYSTEM_UPDATE"
        );
    }
    
    // Reject instructor (admin function)
    public void rejectInstructor(String instructorId, String reason) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        instructor.setApprovalStatus("REJECTED");
        instructor.setRejectionReason(reason);
        
        instructorRepository.save(instructor);
        
        // Send rejection notification
        notificationService.createNotification(
            instructorId,
            "Candidatura Respinta",
            "La tua candidatura come istruttore è stata respinta: " + reason,
            "SYSTEM_UPDATE"
        );
    }
    
    // Set instructor availability
    public void setAvailability(String instructorId, boolean available) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        instructor.setAvailable(available);
        instructorRepository.save(instructor);
    }
    
    // Add sport to instructor
    public void addSport(String instructorId, Sport sport, double hourlyRate) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        instructor.getSpecializations().add(sport.getId());
        instructorRepository.save(instructor);
    }
    
    // Update rating
    public void updateRating(String instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        // Calculate new average rating from reviews
        double averageRating = reviewService.calculateAverageRatingForInstructor(instructorId);
        long totalReviews = reviewService.countReviewsForInstructor(instructorId);
        
        instructor.setRating(averageRating);
        instructor.setTotalReviews((int) totalReviews);
        
        instructorRepository.save(instructor);
    }
    
    // Search instructors
    public List<Instructor> searchInstructors(String query) {
        return instructorRepository.searchByName(query);
    }
    
    // Find instructors by sport
    public List<Instructor> getInstructorsBySport(String sportId) {
        return instructorRepository.findBySportId(sportId);
    }
    
    // Find instructors by location
    public List<Instructor> getInstructorsByCity(String city) {
        return instructorRepository.findByAddressCity(city);
    }
    
    // Find instructors by rating
    public List<Instructor> getInstructorsByMinRating(double minRating) {
        return instructorRepository.findByAverageRatingGreaterThanEqual(minRating);
    }
    
    // Find top-rated instructors
    public List<Instructor> getTopRatedInstructors(int limit) {
        return instructorRepository.findTopRatedInstructors(PageRequest.of(0, limit));
    }
    
    // Find available instructors
    public List<Instructor> getAvailableInstructors() {
        return instructorRepository.findByIsAvailableTrue();
    }
    
    // Find instructors with upcoming availability
    public List<Instructor> getInstructorsWithUpcomingAvailability() {
        return instructorRepository.findWithUpcomingAvailability(LocalDateTime.now());
    }
    
    // Find instructors by location radius
    public List<Instructor> getInstructorsNearLocation(double latitude, double longitude, double radiusKm) {
        double radiusMeters = radiusKm * 1000;
        return instructorRepository.findByLocationWithinRadius(latitude, longitude, radiusMeters);
    }
    
    // Get instructor statistics
    public InstructorStatistics getInstructorStatistics(String instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        InstructorStatistics stats = new InstructorStatistics();
        stats.setAverageRating(instructor.getRating());
        stats.setTotalReviews(instructor.getTotalReviews());
        stats.setTotalLessons(instructor.getTotalLessons());
        stats.setYearsOfExperience(instructor.getYearsOfExperience());
        // Add more statistics from bookings, reviews, etc.
        
        return stats;
    }
    
    // Change password
    public void changePassword(String instructorId, String currentPassword, String newPassword) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        // Delegate password change to UserService
        userService.changePassword(instructor.getUserId(), currentPassword, newPassword);
    }
    
    // Deactivate instructor
    public void deactivateInstructor(String instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        instructor.setActive(false);
        instructor.setAvailable(false);
        instructorRepository.save(instructor);
    }
    
    // Get instructor count
    public long getInstructorCount() {
        return instructorRepository.countByIsActiveTrue();
    }
    
    // Get verified instructor count
    public long getVerifiedInstructorCount() {
        return instructorRepository.countByIsVerifiedTrue();
    }
    
    // Helper class for instructor statistics
    public static class InstructorStatistics {
        private double averageRating;
        private int totalReviews;
        private int totalLessons;
        private int yearsOfExperience;
        private int totalStudents;
        private double totalEarnings;
        
        // Getters and setters
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
        
        public int getTotalLessons() { return totalLessons; }
        public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }
        
        public int getYearsOfExperience() { return yearsOfExperience; }
        public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public double getTotalEarnings() { return totalEarnings; }
        public void setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; }
    }
}