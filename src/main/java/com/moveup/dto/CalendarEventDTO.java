package com.moveup.dto;

import java.time.LocalDateTime;

public class CalendarEventDTO {
    private String title;
    private String notes;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String url;
    private Integer[] alarmOffsets; // Minutes before event (e.g., [-15, -60] for 15min and 1h before)
    
    // Constructors
    public CalendarEventDTO() {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder pattern
    public static class Builder {
        private CalendarEventDTO dto = new CalendarEventDTO();
        
        public Builder title(String title) {
            dto.title = title;
            return this;
        }
        
        public Builder notes(String notes) {
            dto.notes = notes;
            return this;
        }
        
        public Builder location(String location) {
            dto.location = location;
            return this;
        }
        
        public Builder startDate(LocalDateTime startDate) {
            dto.startDate = startDate;
            return this;
        }
        
        public Builder endDate(LocalDateTime endDate) {
            dto.endDate = endDate;
            return this;
        }
        
        public Builder url(String url) {
            dto.url = url;
            return this;
        }
        
        public Builder alarmOffsets(Integer[] alarmOffsets) {
            dto.alarmOffsets = alarmOffsets;
            return this;
        }
        
        public CalendarEventDTO build() {
            return dto;
        }
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public Integer[] getAlarmOffsets() { return alarmOffsets; }
    public void setAlarmOffsets(Integer[] alarmOffsets) { this.alarmOffsets = alarmOffsets; }
}
