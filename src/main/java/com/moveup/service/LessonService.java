package com.moveup.service;

import com.moveup.model.Lesson;
import com.moveup.repository.LessonRepository;
import com.moveup.repository.InstructorRepository;
import com.moveup.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LessonService {
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private SportRepository sportRepository;
    
    // Create new lesson
    public Lesson createLesson(Lesson lesson) {
        // Verify instructor exists and is active
        if (!instructorRepository.existsById(lesson.getInstructorId())) {
            throw new RuntimeException("Istruttore non trovato");
        }
        
        // Verify sport exists
        if (!sportRepository.existsById(lesson.getSportId())) {
            throw new RuntimeException("Sport non trovato");
        }
        
        return lessonRepository.save(lesson);
    }
    
    // Get lesson by ID
    public Optional<Lesson> getLessonById(String lessonId) {
        return lessonRepository.findById(lessonId);
    }
    
    // Update lesson
    public Lesson updateLesson(String lessonId, Lesson updatedLesson) {
        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lezione non trovata"));
        
        // Update fields
        if (updatedLesson.getTitle() != null) {
            existingLesson.setTitle(updatedLesson.getTitle());
        }
        if (updatedLesson.getDescription() != null) {
            existingLesson.setDescription(updatedLesson.getDescription());
        }
        if (updatedLesson.getPrice() != null) {
            existingLesson.setPrice(updatedLesson.getPrice());
        }
        if (updatedLesson.getDuration() != null) {
            existingLesson.setDuration(updatedLesson.getDuration());
        }
        if (updatedLesson.getLevel() != null) {
            existingLesson.setLevel(updatedLesson.getLevel());
        }
        if (updatedLesson.getLocation() != null) {
            existingLesson.setLocation(updatedLesson.getLocation());
        }
        if (updatedLesson.getEquipment() != null) {
            existingLesson.setEquipment(updatedLesson.getEquipment());
        }
        
        return lessonRepository.save(existingLesson);
    }
    
    // Delete lesson (soft delete - mark as inactive)
    public void deleteLesson(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lezione non trovata"));
        
        lesson.setActive(false);
        lessonRepository.save(lesson);
    }
    
    // Get lessons by instructor
    public List<Lesson> getLessonsByInstructor(String instructorId) {
        return lessonRepository.findByInstructorIdAndIsActiveTrue(instructorId);
    }
    
    // Get lessons by sport
    public List<Lesson> getLessonsBySport(String sportId) {
        return lessonRepository.findBySportId(sportId);
    }
    
    // Search lessons
    public List<Lesson> searchLessons(String query) {
        return lessonRepository.searchByTitleOrDescription(query);
    }
    
    // Get lessons by price range
    public List<Lesson> getLessonsByPriceRange(double minPrice, double maxPrice) {
        return lessonRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    // Get lessons by duration
    public List<Lesson> getLessonsByDuration(int duration) {
        return lessonRepository.findByDuration(duration);
    }
    
    // Get lessons by level
    public List<Lesson> getLessonsByLevel(String level) {
        return lessonRepository.findByLevel(level);
    }
    
    // Get lessons by type
    public List<Lesson> getLessonsByType(String type) {
        return lessonRepository.findByType(type);
    }
    
    // Get lessons with equipment provided
    public List<Lesson> getLessonsWithEquipmentProvided() {
        return lessonRepository.findWithEquipmentProvided();
    }
    
    // Get lessons by city
    public List<Lesson> getLessonsByCity(String city) {
        return lessonRepository.findByLocationCity(city);
    }
    
    // Get lessons near location
    public List<Lesson> getLessonsNearLocation(double latitude, double longitude, double radiusKm) {
        double radiusMeters = radiusKm * 1000;
        return lessonRepository.findByLocationWithinRadius(latitude, longitude, radiusMeters);
    }
    
    // Get popular lessons
    public List<Lesson> getPopularLessons(int limit) {
        return lessonRepository.findPopularLessons(PageRequest.of(0, limit));
    }
    
    // Get recent lessons
    public List<Lesson> getRecentLessons(int limit) {
        return lessonRepository.findRecentLessons(PageRequest.of(0, limit));
    }
    
    // Get lessons with special offers
    public List<Lesson> getLessonsWithSpecialOffers() {
        return lessonRepository.findWithSpecialOffers();
    }
    
    // Advanced search with multiple criteria
    public List<Lesson> searchLessonsAdvanced(SearchCriteria criteria) {
        return lessonRepository.findByMultipleCriteria(
            criteria.getSportIds(),
            criteria.getLevels(),
            criteria.getMinPrice(),
            criteria.getMaxPrice()
        );
    }
    
    // Increment booking count
    public void incrementBookingCount(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lezione non trovata"));
        
        lesson.incrementBookingCount();
        lessonRepository.save(lesson);
    }
    
    // Update lesson rating
    public void updateLessonRating(String lessonId, double newRating) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lezione non trovata"));
        
        // Update average rating logic would go here
        // This could be calculated from reviews
        lessonRepository.save(lesson);
    }
    
    // Get lesson statistics
    public LessonStatistics getLessonStatistics(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lezione non trovata"));
        
        LessonStatistics stats = new LessonStatistics();
        stats.setTotalBookings(lesson.getBookingCount());
        stats.setViewCount(lesson.getViewCount());
        // Add more statistics as needed
        
        return stats;
    }
    
    // Get lessons for admin dashboard
    public List<Lesson> getAllActiveLessons() {
        return lessonRepository.findByIsActiveTrue();
    }
    
    // Count lessons by instructor
    public long countLessonsByInstructor(String instructorId) {
        return lessonRepository.countByInstructorIdAndIsActiveTrue(instructorId);
    }
    
    // Count lessons by sport
    public long countLessonsBySport(String sportId) {
        return lessonRepository.countBySportId(sportId);
    }
    
    // Helper classes
    public static class SearchCriteria {
        private List<String> sportIds;
        private List<String> levels;
        private double minPrice;
        private double maxPrice;
        private String city;
        private double latitude;
        private double longitude;
        private double radiusKm;
        
        // Getters and setters
        public List<String> getSportIds() { return sportIds; }
        public void setSportIds(List<String> sportIds) { this.sportIds = sportIds; }
        
        public List<String> getLevels() { return levels; }
        public void setLevels(List<String> levels) { this.levels = levels; }
        
        public double getMinPrice() { return minPrice; }
        public void setMinPrice(double minPrice) { this.minPrice = minPrice; }
        
        public double getMaxPrice() { return maxPrice; }
        public void setMaxPrice(double maxPrice) { this.maxPrice = maxPrice; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        
        public double getRadiusKm() { return radiusKm; }
        public void setRadiusKm(double radiusKm) { this.radiusKm = radiusKm; }
    }
    
    public static class LessonStatistics {
        private int totalBookings;
        private int viewCount;
        private double averageRating;
        private int totalReviews;
        
        // Getters and setters
        public int getTotalBookings() { return totalBookings; }
        public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
        
        public int getViewCount() { return viewCount; }
        public void setViewCount(int viewCount) { this.viewCount = viewCount; }
        
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
    }
}