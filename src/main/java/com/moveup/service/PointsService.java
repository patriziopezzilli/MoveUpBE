package com.moveup.service;

import com.moveup.model.User;
import com.moveup.model.Booking;
import com.moveup.repository.UserRepository;
import com.moveup.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PointsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    // Points configuration
    private static final int POINTS_PER_LESSON = 20;
    private static final int POINTS_PER_REVIEW = 10;
    private static final int POINTS_FIRST_LESSON_BONUS = 50;
    private static final int POINTS_STREAK_BONUS = 30; // 3 lezioni consecutive
    private static final int POINTS_REFERRAL = 100;
    
    /**
     * Assegna punti dopo una lezione completata
     */
    public PointsTransaction awardPointsForLesson(String userId, String bookingId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
        
        int points = POINTS_PER_LESSON;
        String reason = "Lezione completata";
        List<String> bonuses = new ArrayList<>();
        
        // Bonus prima lezione
        long completedLessons = bookingRepository.countByUserIdAndStatus(userId, Booking.BookingStatus.COMPLETED);
        if (completedLessons == 1) {
            points += POINTS_FIRST_LESSON_BONUS;
            bonuses.add("🎉 Bonus prima lezione: +" + POINTS_FIRST_LESSON_BONUS);
        }
        
        // Bonus streak (3 lezioni consecutive)
        if (checkStreak(userId, 3)) {
            points += POINTS_STREAK_BONUS;
            bonuses.add("🔥 Streak 3 lezioni: +" + POINTS_STREAK_BONUS);
        }
        
        // Aggiungi punti all'utente
        int currentPoints = user.getPoints();
        user.setPoints(currentPoints + points);
        userRepository.save(user);
        
        // Check livello
        LevelInfo newLevel = checkLevelUp(user);
        
        return new PointsTransaction(
            userId,
            points,
            reason,
            bonuses,
            user.getPoints(),
            newLevel
        );
    }
    
    /**
     * Assegna punti per una recensione
     */
    public PointsTransaction awardPointsForReview(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        int currentPoints = user.getPoints();
        user.setPoints(currentPoints + POINTS_PER_REVIEW);
        userRepository.save(user);
        
        return new PointsTransaction(
            userId,
            POINTS_PER_REVIEW,
            "Recensione pubblicata",
            List.of(),
            user.getPoints(),
            null
        );
    }
    
    /**
     * Assegna punti per referral
     */
    public PointsTransaction awardPointsForReferral(String userId, String referredUserId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        int currentPoints = user.getPoints();
        user.setPoints(currentPoints + POINTS_REFERRAL);
        userRepository.save(user);
        
        return new PointsTransaction(
            userId,
            POINTS_REFERRAL,
            "Amico invitato: " + referredUserId,
            List.of("💎 Bonus referral: +" + POINTS_REFERRAL),
            user.getPoints(),
            null
        );
    }
    
    /**
     * Riscatta punti per un reward
     */
    public RedemptionResult redeemPoints(String userId, String rewardId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        Reward reward = getAvailableRewards().stream()
            .filter(r -> r.getId().equals(rewardId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Reward non trovato"));
        
        int currentPoints = user.getPoints();
        
        if (currentPoints < reward.getCost()) {
            throw new RuntimeException("Punti insufficienti. Hai " + currentPoints + " punti, servono " + reward.getCost());
        }
        
        // Sottrai punti
        user.setPoints(currentPoints - reward.getCost());
        userRepository.save(user);
        
        return new RedemptionResult(
            true,
            reward,
            user.getPoints(),
            "Reward riscattato con successo!"
        );
    }
    
    /**
     * Ottieni rewards disponibili
     */
    public List<Reward> getAvailableRewards() {
        return List.of(
            new Reward("1", "🎽 Maglietta MoveUp", 200, "Maglietta ufficiale MoveUp", "tshirt"),
            new Reward("2", "🎟️ Lezione Gratis", 500, "1 lezione gratuita a scelta", "free_lesson"),
            new Reward("3", "💳 Sconto 20%", 300, "20% di sconto sulla prossima lezione", "discount_20"),
            new Reward("4", "🎒 Borsa Sportiva", 400, "Borsa sportiva MoveUp premium", "bag"),
            new Reward("5", "👕 Kit Completo", 800, "Kit completo: maglietta + borsa + bottiglia", "kit"),
            new Reward("6", "🏆 Lezione VIP", 1000, "Lezione privata con trainer TOP", "vip_lesson")
        );
    }
    
    /**
     * Check se l'utente ha uno streak attivo
     */
    private boolean checkStreak(String userId, int requiredCount) {
        List<Booking> recentBookings = bookingRepository
            .findByUserIdAndStatusOrderByCreatedAtDesc(userId, Booking.BookingStatus.COMPLETED);
        
        if (recentBookings.size() < requiredCount) {
            return false;
        }
        
        // Check se le ultime X lezioni sono consecutive (entro 7 giorni)
        for (int i = 0; i < requiredCount - 1; i++) {
            long daysDiff = java.time.Duration.between(
                recentBookings.get(i + 1).getCreatedAt(),
                recentBookings.get(i).getCreatedAt()
            ).toDays();
            
            if (daysDiff > 7) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check level up basato sui punti
     */
    private LevelInfo checkLevelUp(User user) {
        int points = user.getPoints();
        int oldLevel = user.getGameStatus() != null ? user.getGameStatus().getLevel() : 1;
        int newLevel = calculateLevel(points);
        
        if (newLevel > oldLevel) {
            if (user.getGameStatus() != null) {
                user.getGameStatus().setLevel(newLevel);
            }
            userRepository.save(user);
            
            return new LevelInfo(
                newLevel,
                getLevelName(newLevel),
                getLevelBadge(newLevel),
                true
            );
        }
        
        return null;
    }
    
    /**
     * Calcola livello basato sui punti
     * Livello 1: 0-99 punti
     * Livello 2: 100-299 punti
     * Livello 3: 300-599 punti
     * Livello 4: 600-999 punti
     * Livello 5: 1000+ punti
     */
    private int calculateLevel(int points) {
        if (points < 100) return 1;
        if (points < 300) return 2;
        if (points < 600) return 3;
        if (points < 1000) return 4;
        return 5;
    }
    
    private String getLevelName(int level) {
        return switch (level) {
            case 1 -> "Principiante";
            case 2 -> "Appassionato";
            case 3 -> "Atleta";
            case 4 -> "Pro";
            case 5 -> "Leggenda";
            default -> "Utente";
        };
    }
    
    private String getLevelBadge(int level) {
        return switch (level) {
            case 1 -> "🌱";
            case 2 -> "⭐";
            case 3 -> "💪";
            case 4 -> "🏆";
            case 5 -> "👑";
            default -> "🔰";
        };
    }
    
    /**
     * Ottieni informazioni sui punti dell'utente
     */
    public UserPointsInfo getUserPointsInfo(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        int points = user.getPoints();
        int level = calculateLevel(points);
        
        return new UserPointsInfo(
            userId,
            points,
            level,
            getLevelName(level),
            getLevelBadge(level)
        );
    }
    
    // Inner classes
    public static class UserPointsInfo {
        private String userId;
        private int points;
        private int level;
        private String levelName;
        private String levelBadge;
        
        public UserPointsInfo(String userId, int points, int level, String levelName, String levelBadge) {
            this.userId = userId;
            this.points = points;
            this.level = level;
            this.levelName = levelName;
            this.levelBadge = levelBadge;
        }
        
        public String getUserId() { return userId; }
        public int getPoints() { return points; }
        public int getLevel() { return level; }
        public String getLevelName() { return levelName; }
        public String getLevelBadge() { return levelBadge; }
    }
    
    public static class PointsTransaction {
        private String userId;
        private int pointsAwarded;
        private String reason;
        private List<String> bonuses;
        private int totalPoints;
        private LevelInfo levelInfo;
        
        public PointsTransaction(String userId, int pointsAwarded, String reason, 
                                List<String> bonuses, int totalPoints, LevelInfo levelInfo) {
            this.userId = userId;
            this.pointsAwarded = pointsAwarded;
            this.reason = reason;
            this.bonuses = bonuses;
            this.totalPoints = totalPoints;
            this.levelInfo = levelInfo;
        }
        
        // Getters
        public String getUserId() { return userId; }
        public int getPointsAwarded() { return pointsAwarded; }
        public String getReason() { return reason; }
        public List<String> getBonuses() { return bonuses; }
        public int getTotalPoints() { return totalPoints; }
        public LevelInfo getLevelInfo() { return levelInfo; }
        public boolean isLevelUp() { return levelInfo != null && levelInfo.isLevelUp(); }
    }
    
    public static class LevelInfo {
        private int level;
        private String name;
        private String badge;
        private boolean levelUp;
        
        public LevelInfo(int level, String name, String badge, boolean levelUp) {
            this.level = level;
            this.name = name;
            this.badge = badge;
            this.levelUp = levelUp;
        }
        
        public int getLevel() { return level; }
        public String getName() { return name; }
        public String getBadge() { return badge; }
        public boolean isLevelUp() { return levelUp; }
    }
    
    public static class Reward {
        private String id;
        private String name;
        private int cost;
        private String description;
        private String type;
        
        public Reward(String id, String name, int cost, String description, String type) {
            this.id = id;
            this.name = name;
            this.cost = cost;
            this.description = description;
            this.type = type;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public int getCost() { return cost; }
        public String getDescription() { return description; }
        public String getType() { return type; }
    }
    
    public static class RedemptionResult {
        private boolean success;
        private Reward reward;
        private int remainingPoints;
        private String message;
        
        public RedemptionResult(boolean success, Reward reward, int remainingPoints, String message) {
            this.success = success;
            this.reward = reward;
            this.remainingPoints = remainingPoints;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public Reward getReward() { return reward; }
        public int getRemainingPoints() { return remainingPoints; }
        public String getMessage() { return message; }
    }
}
