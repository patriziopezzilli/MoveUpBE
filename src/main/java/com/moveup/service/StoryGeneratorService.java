package com.moveup.service;

import com.moveup.model.Booking;
import com.moveup.model.User;
import com.moveup.model.Instructor;
import com.moveup.repository.UserRepository;
import com.moveup.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StoryGeneratorService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private PointsService pointsService;
    
    /**
     * Genera storia automatica post-lezione
     */
    public StoryData generateLessonStory(Booking booking, int pointsEarned) {
        User user = userRepository.findById(booking.getUserId())
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Instructor instructor = instructorRepository.findById(booking.getInstructorId())
            .orElseThrow(() -> new RuntimeException("Istruttore non trovato"));
        
        // Determina template basato sullo sport
        StoryTemplate template = getTemplateForSport(booking.getSport());
        
        // Genera badge per l'achievement
        String badge = generateBadge(booking, user, pointsEarned);
        
        // Genera testo motivazionale
        String motivationalText = generateMotivationalText(booking.getSport(), user);
        
        // CTA personalizzato
        String cta = generateCTA(booking.getSport());
        
        return new StoryData(
            template,
            booking.getSport(),
            instructor.getFirstName() + " " + instructor.getLastName(),
            instructor.getAverageRating(),
            pointsEarned,
            badge,
            motivationalText,
            cta,
            generateHashtags(booking.getSport()),
            booking.getScheduledDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
            user.getLevel(),
            user.getPoints()
        );
    }
    
    /**
     * Genera achievement badge basato su milestone
     */
    private String generateBadge(Booking booking, User user, int pointsEarned) {
        long completedLessons = user.getTotalLessons() != null ? user.getTotalLessons() : 0;
        
        // Prima lezione
        if (completedLessons == 1) {
            return "🌟 Prima Lezione Completata!";
        }
        
        // Milestone lezioni
        if (completedLessons == 5) {
            return "🔥 5 Lezioni Completate!";
        }
        if (completedLessons == 10) {
            return "💪 10 Lezioni - Sei un Atleta!";
        }
        if (completedLessons == 25) {
            return "🏆 25 Lezioni - Sei un Pro!";
        }
        if (completedLessons == 50) {
            return "👑 50 Lezioni - Leggenda!";
        }
        
        // Punti earned
        if (pointsEarned >= 50) {
            return "💎 Hai guadagnato " + pointsEarned + " punti!";
        }
        
        // Default
        return "💪 Hai guadagnato " + pointsEarned + " punti MoveUp";
    }
    
    /**
     * Genera testo motivazionale per sport
     */
    private String generateMotivationalText(String sport, User user) {
        List<String> motivations = switch (sport.toLowerCase()) {
            case "tennis" -> List.of(
                "Il tuo gioco migliora ad ogni sessione! 🎾",
                "Ace dopo ace, diventi sempre più forte! 🎾",
                "Il campo ti aspetta per la prossima vittoria! 🎾"
            );
            case "padel" -> List.of(
                "Il muro è il tuo alleato, continua così! 🎾",
                "Smash perfetto! Sei sulla strada giusta! 🎾",
                "La tua tecnica migliora ad ogni partita! 🎾"
            );
            case "golf" -> List.of(
                "Il tuo swing è sempre più preciso! ⛳",
                "Birdie dopo birdie, il green ti aspetta! ⛳",
                "La perfezione è nel dettaglio, continua! ⛳"
            );
            case "fitness" -> List.of(
                "Il tuo corpo ti ringrazia! Continua così! 💪",
                "Ogni workout ti avvicina ai tuoi obiettivi! 🏋️",
                "La forza è nella costanza! 💪"
            );
            case "yoga" -> List.of(
                "Mente e corpo in armonia! 🧘",
                "Il tuo equilibrio migliora ogni giorno! 🧘",
                "Namaste, continua il tuo percorso! 🧘"
            );
            default -> List.of(
                "Grande lavoro oggi! Continua così! 💪",
                "Ogni allenamento conta! Vai avanti! 🔥",
                "Il tuo impegno paga! Keep going! ⭐"
            );
        };
        
        return motivations.get(new Random().nextInt(motivations.size()));
    }
    
    /**
     * Genera CTA per la storia
     */
    private String generateCTA(String sport) {
        return "Prenota anche tu su MoveUp! 🚀";
    }
    
    /**
     * Genera hashtags per sport
     */
    private List<String> generateHashtags(String sport) {
        List<String> common = List.of("#MoveUp", "#Sport", "#Fitness", "#Training");
        
        List<String> sportSpecific = switch (sport.toLowerCase()) {
            case "tennis" -> List.of("#Tennis", "#TennisLife", "#TennisTraining");
            case "padel" -> List.of("#Padel", "#PadelTime", "#PadelTraining");
            case "golf" -> List.of("#Golf", "#GolfLife", "#GolfTraining");
            case "fitness" -> List.of("#Fitness", "#Workout", "#FitnessMotivation");
            case "yoga" -> List.of("#Yoga", "#YogaLife", "#Mindfulness");
            default -> List.of("#" + sport);
        };
        
        List<String> all = new ArrayList<>(common);
        all.addAll(sportSpecific);
        return all;
    }
    
    /**
     * Ottieni template visivo per sport
     */
    private StoryTemplate getTemplateForSport(String sport) {
        return switch (sport.toLowerCase()) {
            case "tennis" -> new StoryTemplate(
                "tennis_story_bg.jpg",
                "#00B894", // Verde tennis
                "#FFFFFF",
                "tennis_icon.png"
            );
            case "padel" -> new StoryTemplate(
                "padel_story_bg.jpg",
                "#6C5CE7", // Viola padel
                "#FFFFFF",
                "padel_icon.png"
            );
            case "golf" -> new StoryTemplate(
                "golf_story_bg.jpg",
                "#27AE60", // Verde golf
                "#FFFFFF",
                "golf_icon.png"
            );
            case "fitness" -> new StoryTemplate(
                "fitness_story_bg.jpg",
                "#E74C3C", // Rosso fitness
                "#FFFFFF",
                "fitness_icon.png"
            );
            case "yoga" -> new StoryTemplate(
                "yoga_story_bg.jpg",
                "#9B59B6", // Viola yoga
                "#FFFFFF",
                "yoga_icon.png"
            );
            default -> new StoryTemplate(
                "default_story_bg.jpg",
                "#3498DB",
                "#FFFFFF",
                "sport_icon.png"
            );
        };
    }
    
    // Inner classes
    public static class StoryData {
        private StoryTemplate template;
        private String sport;
        private String trainerName;
        private Double trainerRating;
        private int pointsEarned;
        private String badge;
        private String motivationalText;
        private String cta;
        private List<String> hashtags;
        private String lessonDate;
        private Integer userLevel;
        private Integer userTotalPoints;
        
        public StoryData(StoryTemplate template, String sport, String trainerName,
                        Double trainerRating, int pointsEarned, String badge,
                        String motivationalText, String cta, List<String> hashtags,
                        String lessonDate, Integer userLevel, Integer userTotalPoints) {
            this.template = template;
            this.sport = sport;
            this.trainerName = trainerName;
            this.trainerRating = trainerRating;
            this.pointsEarned = pointsEarned;
            this.badge = badge;
            this.motivationalText = motivationalText;
            this.cta = cta;
            this.hashtags = hashtags;
            this.lessonDate = lessonDate;
            this.userLevel = userLevel;
            this.userTotalPoints = userTotalPoints;
        }
        
        // Getters
        public StoryTemplate getTemplate() { return template; }
        public String getSport() { return sport; }
        public String getTrainerName() { return trainerName; }
        public Double getTrainerRating() { return trainerRating; }
        public int getPointsEarned() { return pointsEarned; }
        public String getBadge() { return badge; }
        public String getMotivationalText() { return motivationalText; }
        public String getCta() { return cta; }
        public List<String> getHashtags() { return hashtags; }
        public String getLessonDate() { return lessonDate; }
        public Integer getUserLevel() { return userLevel; }
        public Integer getUserTotalPoints() { return userTotalPoints; }
    }
    
    public static class StoryTemplate {
        private String backgroundImage;
        private String primaryColor;
        private String textColor;
        private String iconImage;
        
        public StoryTemplate(String backgroundImage, String primaryColor, 
                           String textColor, String iconImage) {
            this.backgroundImage = backgroundImage;
            this.primaryColor = primaryColor;
            this.textColor = textColor;
            this.iconImage = iconImage;
        }
        
        public String getBackgroundImage() { return backgroundImage; }
        public String getPrimaryColor() { return primaryColor; }
        public String getTextColor() { return textColor; }
        public String getIconImage() { return iconImage; }
    }
}
