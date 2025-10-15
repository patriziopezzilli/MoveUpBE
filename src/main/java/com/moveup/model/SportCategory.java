package com.moveup.model;

public enum SportCategory {
    RACQUET("Racchette"),
    TEAM("Sport di squadra"), 
    WATER("Sport acquatici"),
    FITNESS("Fitness"),
    WELLNESS("Benessere"),
    RUNNING("Corsa"),
    COMBAT("Arti marziali"),
    WINTER("Sport invernali"),
    OTHER("Altri");
    
    private final String displayName;
    
    SportCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
