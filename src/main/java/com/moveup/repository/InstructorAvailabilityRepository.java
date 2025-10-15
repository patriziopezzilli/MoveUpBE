package com.moveup.repository;

import com.moveup.model.InstructorAvailability;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorAvailabilityRepository extends MongoRepository<InstructorAvailability, String> {
    
    List<InstructorAvailability> findByInstructorIdAndDate(String instructorId, LocalDate date);
    
    List<InstructorAvailability> findByInstructorIdAndDateBetween(String instructorId, LocalDate startDate, LocalDate endDate);
    
    Optional<InstructorAvailability> findByInstructorIdAndDateAndIsAvailable(String instructorId, LocalDate date, boolean isAvailable);
}
