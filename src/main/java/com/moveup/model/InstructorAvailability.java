package com.moveup.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "instructor_availability")
public class InstructorAvailability {
    
    @Id
    private String id;
    
    @Indexed
    private String instructorId;
    
    @Indexed
    private LocalDate date;
    
    private List<TimeSlot> availableSlots = new ArrayList<>();
    private boolean isAvailable = true;
    
    // Constructors
    public InstructorAvailability() {}
    
    public InstructorAvailability(String instructorId, LocalDate date) {
        this.instructorId = instructorId;
        this.date = date;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public List<TimeSlot> getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(List<TimeSlot> availableSlots) { this.availableSlots = availableSlots; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    // Inner class for time slots
    public static class TimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean isBooked = false;
        
        public TimeSlot() {}
        
        public TimeSlot(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        
        public boolean isBooked() { return isBooked; }
        public void setBooked(boolean booked) { isBooked = booked; }
    }
}
