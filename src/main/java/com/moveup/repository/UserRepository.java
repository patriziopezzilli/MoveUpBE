package com.moveup.repository;

import com.moveup.model.User;
import com.moveup.model.UserType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    // Find by email
    Optional<User> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find by email (username is now email)
    @Query("{ 'email' : ?0 }")
    Optional<User> findByUsername(String username);
    
    // Check if email exists (username is now email)
    @Query(value = "{ 'email' : ?0 }", exists = true)
    boolean existsByUsername(String username);
    
    // Find by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    // Find by verification token
    Optional<User> findByVerificationToken(String verificationToken);
    
    // Find by reset password token
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
    
    // Find active users
    List<User> findByIsActiveTrue();
    
    // Find verified users
    List<User> findByIsVerifiedTrue();
    
    // Find users by city
    List<User> findByLocationCity(String city);
    
    // Find users created between dates
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find users by level
    List<User> findByGameStatusLevel(int level);
    
    // Find users with points greater than
    @Query("{'gameStatus.totalPoints': {$gte: ?0}}")
    List<User> findByTotalPointsGreaterThanEqual(int points);
    
    // Count total users
    long countByIsActiveTrue();
    
    // Count verified users
    long countByIsVerifiedTrue();
    
    // Search users by name (case insensitive)
    @Query("{ $or: [ " +
           "{ 'firstName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'lastName': { $regex: ?0, $options: 'i' } } " +
           "] }")
    List<User> searchByName(String nameQuery);
    
    // Find top users by points (leaderboard)
    @Query(value = "{}", sort = "{ 'gameStatus.totalPoints': -1 }")
    List<User> findTopUsersByPoints(org.springframework.data.domain.Pageable pageable);
    
    // Find users by user type (CUSTOMER or INSTRUCTOR)
    List<User> findByUserType(UserType userType);
}