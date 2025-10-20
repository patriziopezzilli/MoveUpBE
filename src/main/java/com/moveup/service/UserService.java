package com.moveup.service;

import com.moveup.dto.LocationDTO;
import com.moveup.dto.RegisterWithOnboardingDTO;
import com.moveup.model.SkillLevel;
import com.moveup.model.User;
import com.moveup.model.UserType;
import com.moveup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationService notificationService;
    
    // Create new user
    public User createUser(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email già registrata");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Generate verification token
        user.setVerificationToken(UUID.randomUUID().toString());
        
        // Set default values
        user.setActive(true);
        user.setVerified(false);
        
        User savedUser = userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser);
        
        return savedUser;
    }
    
    // Create user with complete onboarding data
    public User createUserWithOnboarding(RegisterWithOnboardingDTO dto) {
        // Validate email not exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email già registrata");
        }
        
        // Validate age (minimum 16 years)
        if (dto.getBirthDate() != null) {
            int age = Period.between(dto.getBirthDate(), java.time.LocalDate.now()).getYears();
            if (age < 16) {
                throw new RuntimeException("Devi avere almeno 16 anni per registrarti");
            }
        }
        
        // Create user
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUserType(UserType.valueOf(dto.getUserType()));
        
        // Onboarding data
        user.setBio(dto.getBio());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setBirthDate(dto.getBirthDate());
        user.setMaxDistance(dto.getMaxDistance());
        user.setNotificationsEnabled(dto.getNotificationsEnabled());
        user.setMarketingEnabled(dto.getMarketingEnabled());
        
        // Convert sport skill levels from String to SkillLevel enum
        if (dto.getSportSkillLevels() != null) {
            Map<String, SkillLevel> sportSkillLevels = new HashMap<>();
            dto.getSportSkillLevels().forEach((sportId, level) -> {
                sportSkillLevels.put(sportId, SkillLevel.valueOf(level.toUpperCase()));
            });
            user.setSportSkillLevels(sportSkillLevels);
        }
        
        // Location data
        if (dto.getLocation() != null) {
            LocationDTO locDTO = dto.getLocation();
            // Create Location object (you'll need to adapt based on your Location class structure)
            // user.setLocation(...);
        }
        
        // Instructor-specific data
        if (dto.getUserType().equals("INSTRUCTOR")) {
            if (dto.getCertifications() != null) {
                user.setCertifications(Arrays.asList(dto.getCertifications()));
            }
            user.setExperience(dto.getExperience());
            
            // Validate hourly rate
            if (dto.getHourlyRate() != null) {
                if (dto.getHourlyRate() < 10.0 || dto.getHourlyRate() > 500.0) {
                    throw new RuntimeException("La tariffa oraria deve essere tra 10€ e 500€");
                }
                user.setHourlyRate(dto.getHourlyRate());
            }
        }
        
        // Generate verification token
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setActive(true);
        user.setVerified(false);
        
        User savedUser = userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser);
        
        // Send welcome notification
        notificationService.sendWelcomeNotification(savedUser);
        
        return savedUser;
    }
    
    // Calculate profile completion percentage
    public double calculateProfileCompletion(User user) {
        int totalFields = 0;
        int completedFields = 0;
        
        // Base fields (always count)
        totalFields += 6;
        if (user.getEmail() != null && !user.getEmail().isEmpty()) completedFields++;
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) completedFields++;
        if (user.getLastName() != null && !user.getLastName().isEmpty()) completedFields++;
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) completedFields++;
        if (user.getBio() != null && !user.getBio().isEmpty()) completedFields++;
        if (user.getBirthDate() != null) completedFields++;
        
        // Optional fields
        totalFields += 4;
        if (user.getProfileImageBase64() != null && !user.getProfileImageBase64().isEmpty()) completedFields++;
        if (user.getLocation() != null) completedFields++;
        if (user.getSportSkillLevels() != null && !user.getSportSkillLevels().isEmpty()) completedFields++;
        if (user.getMaxDistance() != null) completedFields++;
        
        // Instructor-specific fields
        if (user.getUserType() == UserType.INSTRUCTOR) {
            totalFields += 3;
            if (user.getCertifications() != null && !user.getCertifications().isEmpty()) completedFields++;
            if (user.getExperience() != null && !user.getExperience().isEmpty()) completedFields++;
            if (user.getHourlyRate() != null) completedFields++;
        }
        
        return (double) completedFields / totalFields * 100.0;
    }
    
    // Get user by ID
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }
    
    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Update user
    public User updateUser(String userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        // Update fields
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        }
        if (updatedUser.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getPreferences() != null) {
            existingUser.setPreferences(updatedUser.getPreferences());
        }
        
        return userRepository.save(existingUser);
    }
    
    // Change password
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Password attuale non corretta");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // Verify user email
    public boolean verifyEmail(String verificationToken) {
        Optional<User> userOptional = userRepository.findByVerificationToken(verificationToken);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setVerified(true);
            user.setVerificationToken(null);
            user.setVerifiedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        
        return false;
    }
    
    // Request password reset
    public void requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetPasswordToken(resetToken);
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(24));
            userRepository.save(user);
            
            emailService.sendPasswordResetEmail(user, resetToken);
        }
    }
    
    // Reset password with token
    public boolean resetPassword(String resetToken, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetPasswordToken(resetToken);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (user.getResetPasswordTokenExpiry().isAfter(LocalDateTime.now())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetPasswordToken(null);
                user.setResetPasswordTokenExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        
        return false;
    }
    
    // Deactivate user
    public void deactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        user.setActive(false);
        user.setDeactivatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    // Reactivate user
    public void reactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        user.setActive(true);
        user.setDeactivatedAt(null);
        userRepository.save(user);
    }
    
    // Add points to user
    public void addPoints(String userId, int points, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        user.addPoints(points);
        userRepository.save(user);
        
        // Send notification about points earned
        notificationService.sendPointsEarnedNotification(user, points, reason);
    }
    
    // Deduct points from user
    public boolean deductPoints(String userId, int points) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        if (user.getGameStatus().getTotalPoints() >= points) {
            user.deductPoints(points);
            userRepository.save(user);
            return true;
        }
        
        return false;
    }
    
    // Get user leaderboard
    public List<User> getLeaderboard(int limit) {
        return userRepository.findTopUsersByPoints(PageRequest.of(0, limit));
    }
    
    // Search users
    public List<User> searchUsers(String query) {
        return userRepository.searchByName(query);
    }
    
    // Get users by city
    public List<User> getUsersByCity(String city) {
        return userRepository.findByAddressCity(city);
    }
    
    // Get user statistics
    public UserStatistics getUserStatistics(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        UserStatistics stats = new UserStatistics();
        stats.setTotalPoints(user.getGameStatus().getTotalPoints());
        stats.setLevel(user.getGameStatus().getLevel());
        stats.setBadgeCount(user.getGameStatus().getBadges().size());
        // Add more statistics as needed
        
        return stats;
    }
    
    // Check user activity
    public boolean isActiveUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        return user.isActive() && user.isVerified();
    }
    
    // Get all active users
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    // Get user count
    public long getUserCount() {
        return userRepository.countByIsActiveTrue();
    }
    
    // Helper class for user statistics
    public static class UserStatistics {
        private int totalPoints;
        private int level;
        private int badgeCount;
        private int totalBookings;
        private double averageRating;
        
        // Getters and setters
        public int getTotalPoints() { return totalPoints; }
        public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
        
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
        
        public int getBadgeCount() { return badgeCount; }
        public void setBadgeCount(int badgeCount) { this.badgeCount = badgeCount; }
        
        public int getTotalBookings() { return totalBookings; }
        public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
        
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    }
}