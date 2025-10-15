package com.moveup.service;

import com.moveup.model.Sport;
import com.moveup.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SportService {
    
    @Autowired
    private SportRepository sportRepository;
    
    // Create new sport
    public Sport createSport(Sport sport) {
        // Check if sport name already exists
        if (sportRepository.existsByName(sport.getName())) {
            throw new RuntimeException("Uno sport con questo nome esiste già");
        }
        
        return sportRepository.save(sport);
    }
    
    // Get sport by ID
    public Optional<Sport> getSportById(String sportId) {
        return sportRepository.findById(sportId);
    }
    
    // Get sport by name
    public Optional<Sport> getSportByName(String name) {
        return sportRepository.findByName(name);
    }
    
    // Update sport
    public Sport updateSport(String sportId, Sport updatedSport) {
        Sport existingSport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport non trovato"));
        
        // Update fields
        if (updatedSport.getName() != null && !updatedSport.getName().equals(existingSport.getName())) {
            // Check if new name already exists
            if (sportRepository.existsByName(updatedSport.getName())) {
                throw new RuntimeException("Uno sport con questo nome esiste già");
            }
            existingSport.setName(updatedSport.getName());
        }
        
        if (updatedSport.getDescription() != null) {
            existingSport.setDescription(updatedSport.getDescription());
        }
        if (updatedSport.getCategory() != null) {
            existingSport.setCategory(updatedSport.getCategory());
        }
        if (updatedSport.getDifficultyLevel() != null) {
            existingSport.setDifficultyLevel(updatedSport.getDifficultyLevel());
        }
        if (updatedSport.getIconUrl() != null) {
            existingSport.setIconUrl(updatedSport.getIconUrl());
        }
        if (updatedSport.getCharacteristics() != null) {
            existingSport.setCharacteristics(updatedSport.getCharacteristics());
        }
        if (updatedSport.getEquipment() != null) {
            existingSport.setEquipment(updatedSport.getEquipment());
        }
        
        return sportRepository.save(existingSport);
    }
    
    // Delete sport (soft delete - mark as inactive)
    public void deleteSport(String sportId) {
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport non trovato"));
        
        sport.setActive(false);
        sportRepository.save(sport);
    }
    
    // Get all active sports
    public List<Sport> getAllActiveSports() {
        return sportRepository.findByIsActiveTrue();
    }
    
    // Get sports by category
    public List<Sport> getSportsByCategory(String category) {
        return sportRepository.findByCategory(category);
    }
    
    // Get sports by multiple categories
    public List<Sport> getSportsByCategories(List<String> categories) {
        return sportRepository.findByCategoryIn(categories);
    }
    
    // Search sports by name
    public List<Sport> searchSportsByName(String query) {
        return sportRepository.searchByName(query);
    }
    
    // Get sports by difficulty level
    public List<Sport> getSportsByDifficultyLevel(String difficultyLevel) {
        return sportRepository.findByDifficultyLevel(difficultyLevel);
    }
    
    // Get indoor sports
    public List<Sport> getIndoorSports() {
        return sportRepository.findIndoorSports();
    }
    
    // Get outdoor sports
    public List<Sport> getOutdoorSports() {
        return sportRepository.findOutdoorSports();
    }
    
    // Get team sports
    public List<Sport> getTeamSports() {
        return sportRepository.findTeamSports();
    }
    
    // Get individual sports
    public List<Sport> getIndividualSports() {
        return sportRepository.findIndividualSports();
    }
    
    // Get sports with equipment
    public List<Sport> getSportsWithEquipment() {
        return sportRepository.findWithEquipment();
    }
    
    // Get sports with seasonal availability
    public List<Sport> getSportsWithSeasonalAvailability() {
        return sportRepository.findWithSeasonalAvailability();
    }
    
    // Get popular sports
    public List<Sport> getPopularSports(int limit) {
        return sportRepository.findPopularSports(PageRequest.of(0, limit));
    }
    
    // Get recommended sports
    public List<Sport> getRecommendedSports(int minPopularity) {
        return sportRepository.findRecommendedSports(minPopularity);
    }
    
    // Increment sport popularity
    public void incrementPopularity(String sportId) {
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport non trovato"));
        
        sport.incrementPopularity();
        sportRepository.save(sport);
    }
    
    // Get sport categories
    public List<String> getAllCategories() {
        return sportRepository.findAllCategories()
                .stream()
                .map(Sport::getCategory)
                .distinct()
                .toList();
    }
    
    // Count sports by category
    public long countSportsByCategory(String category) {
        return sportRepository.countByCategory(category);
    }
    
    // Count all active sports
    public long countActiveSports() {
        return sportRepository.countByIsActiveTrue();
    }
    
    // Get sport statistics
    public SportStatistics getSportStatistics(String sportId) {
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport non trovato"));
        
        SportStatistics stats = new SportStatistics();
        stats.setPopularity(sport.getPopularity());
        stats.setCategory(sport.getCategory());
        stats.setDifficultyLevel(sport.getDifficultyLevel());
        stats.setIsIndoor(sport.getCharacteristics().isIndoor());
        stats.setIsOutdoor(sport.getCharacteristics().isOutdoor());
        stats.setIsTeamSport(sport.getCharacteristics().isTeamSport());
        
        // You could add more statistics like:
        // - Number of instructors teaching this sport
        // - Number of lessons available
        // - Average lesson price
        // - Number of bookings
        
        return stats;
    }
    
    // Add equipment to sport
    public void addEquipmentToSport(String sportId, Sport.Equipment equipment) {
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport non trovato"));
        
        sport.getEquipment().add(equipment);
        sportRepository.save(sport);
    }
    
    // Remove equipment from sport
    public void removeEquipmentFromSport(String sportId, String equipmentName) {
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport non trovato"));
        
        sport.getEquipment().removeIf(eq -> eq.getName().equals(equipmentName));
        sportRepository.save(sport);
    }
    
    // Helper class for sport statistics
    public static class SportStatistics {
        private int popularity;
        private String category;
        private String difficultyLevel;
        private boolean isIndoor;
        private boolean isOutdoor;
        private boolean isTeamSport;
        private int totalInstructors;
        private int totalLessons;
        private double averageLessonPrice;
        private int totalBookings;
        
        // Getters and setters
        public int getPopularity() { return popularity; }
        public void setPopularity(int popularity) { this.popularity = popularity; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getDifficultyLevel() { return difficultyLevel; }
        public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
        
        public boolean isIndoor() { return isIndoor; }
        public void setIsIndoor(boolean isIndoor) { this.isIndoor = isIndoor; }
        
        public boolean isOutdoor() { return isOutdoor; }
        public void setIsOutdoor(boolean isOutdoor) { this.isOutdoor = isOutdoor; }
        
        public boolean isTeamSport() { return isTeamSport; }
        public void setIsTeamSport(boolean isTeamSport) { this.isTeamSport = isTeamSport; }
        
        public int getTotalInstructors() { return totalInstructors; }
        public void setTotalInstructors(int totalInstructors) { this.totalInstructors = totalInstructors; }
        
        public int getTotalLessons() { return totalLessons; }
        public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }
        
        public double getAverageLessonPrice() { return averageLessonPrice; }
        public void setAverageLessonPrice(double averageLessonPrice) { this.averageLessonPrice = averageLessonPrice; }
        
        public int getTotalBookings() { return totalBookings; }
        public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
    }
}