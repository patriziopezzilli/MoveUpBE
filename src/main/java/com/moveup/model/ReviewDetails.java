package com.moveup.model;

import java.util.List;

public class ReviewDetails {
    private List<String> highlights;
    private int punctualityRating;
    private int professionalismRating;
    private int communicationRating;
    private int preparationRating;
    private boolean wouldRecommend = true;
    
    // Constructors
    public ReviewDetails() {}
    
    // Getters and Setters
    public List<String> getHighlights() { return highlights; }
    public void setHighlights(List<String> highlights) { this.highlights = highlights; }
    
    public int getPunctualityRating() { return punctualityRating; }
    public void setPunctualityRating(int punctualityRating) { this.punctualityRating = punctualityRating; }
    
    public int getProfessionalismRating() { return professionalismRating; }
    public void setProfessionalismRating(int professionalismRating) { this.professionalismRating = professionalismRating; }
    
    public int getCommunicationRating() { return communicationRating; }
    public void setCommunicationRating(int communicationRating) { this.communicationRating = communicationRating; }
    
    public int getPreparationRating() { return preparationRating; }
    public void setPreparationRating(int preparationRating) { this.preparationRating = preparationRating; }
    
    public boolean isWouldRecommend() { return wouldRecommend; }
    public void setWouldRecommend(boolean wouldRecommend) { this.wouldRecommend = wouldRecommend; }
    
    // Helper method
    public double getAverageDetailRating() {
        int count = 0;
        int sum = 0;
        
        if (punctualityRating > 0) { count++; sum += punctualityRating; }
        if (professionalismRating > 0) { count++; sum += professionalismRating; }
        if (communicationRating > 0) { count++; sum += communicationRating; }
        if (preparationRating > 0) { count++; sum += preparationRating; }
        
        return count > 0 ? (double) sum / count : 0.0;
    }
}
