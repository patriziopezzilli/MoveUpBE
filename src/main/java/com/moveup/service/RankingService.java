package com.moveup.service;

import com.moveup.model.Instructor;
import com.moveup.model.Booking;
import com.moveup.repository.InstructorRepository;
import com.moveup.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingService {
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    /**
     * RANKING LOCALE: Top trainer per città
     * Cache settimanale per performance
     */
    @Cacheable(value = "cityRankings", key = "#city + '-' + #sport")
    public List<RankedTrainer> getTopTrainersByCity(String city, String sport, Integer limit) {
        // 1. Trova tutti i trainer della città
        List<Instructor> instructors = instructorRepository.findByCity(city);
        
        // 2. Filtra per sport se specificato
        if (sport != null && !sport.isEmpty()) {
            instructors = instructors.stream()
                .filter(instructor -> instructor.getSports().contains(sport))
                .collect(Collectors.toList());
        }
        
        // 3. Calcola score per ogni trainer
        List<RankedTrainer> rankedTrainers = new ArrayList<>();
        
        for (Instructor instructor : instructors) {
            RankingScore score = calculateRankingScore(instructor);
            
            rankedTrainers.add(new RankedTrainer(
                instructor,
                score,
                getRankBadge(score.getTotalScore())
            ));
        }
        
        // 4. Ordina per score decrescente
        rankedTrainers.sort(Comparator
            .comparing(RankedTrainer::getTotalScore)
            .reversed()
        );
        
        // 5. Assegna posizione e badge speciali
        for (int i = 0; i < rankedTrainers.size(); i++) {
            rankedTrainers.get(i).setRank(i + 1);
            
            // Badge speciali per top 3
            if (i == 0) {
                rankedTrainers.get(i).setSpecialBadge("🥇 Top Trainer");
            } else if (i == 1) {
                rankedTrainers.get(i).setSpecialBadge("🥈 Runner-up");
            } else if (i == 2) {
                rankedTrainers.get(i).setSpecialBadge("🥉 Terzo posto");
            }
        }
        
        // 6. Limita risultati
        if (limit != null && rankedTrainers.size() > limit) {
            rankedTrainers = rankedTrainers.subList(0, limit);
        }
        
        return rankedTrainers;
    }
    
    /**
     * Ranking globale (tutte le città)
     */
    public List<RankedTrainer> getGlobalTopTrainers(String sport, Integer limit) {
        List<Instructor> instructors = sport != null 
            ? instructorRepository.findBySportsContaining(sport)
            : instructorRepository.findAll();
        
        List<RankedTrainer> rankedTrainers = instructors.stream()
            .map(instructor -> {
                RankingScore score = calculateRankingScore(instructor);
                return new RankedTrainer(instructor, score, getRankBadge(score.getTotalScore()));
            })
            .sorted(Comparator.comparing(RankedTrainer::getTotalScore).reversed())
            .collect(Collectors.toList());
        
        // Assegna rank
        for (int i = 0; i < rankedTrainers.size(); i++) {
            rankedTrainers.get(i).setRank(i + 1);
        }
        
        if (limit != null && rankedTrainers.size() > limit) {
            rankedTrainers = rankedTrainers.subList(0, limit);
        }
        
        return rankedTrainers;
    }
    
    /**
     * Rising stars: trainer in crescita
     */
    public List<RankedTrainer> getRisingStars(String city, Integer limit) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);
        
        List<Instructor> instructors = city != null 
            ? instructorRepository.findByCity(city)
            : instructorRepository.findAll();
        
        List<RankedTrainer> risingStars = new ArrayList<>();
        
        for (Instructor instructor : instructors) {
            // Lezioni ultimo mese
            long recentLessons = bookingRepository.findByInstructorIdAndCreatedAtAfter(
                instructor.getId(), thirtyDaysAgo
            ).size();
            
            // Lezioni mese precedente
            long previousLessons = bookingRepository.findByInstructorIdAndCreatedAtBetween(
                instructor.getId(), sixtyDaysAgo, thirtyDaysAgo
            ).size();
            
            // Calcola crescita percentuale
            double growthRate = previousLessons > 0 
                ? ((double) (recentLessons - previousLessons) / previousLessons) * 100
                : (recentLessons > 0 ? 100 : 0);
            
            if (growthRate > 20) { // Almeno 20% di crescita
                RankingScore score = calculateRankingScore(instructor);
                RankedTrainer ranked = new RankedTrainer(
                    instructor, 
                    score, 
                    "🚀 Rising Star"
                );
                ranked.setGrowthRate(growthRate);
                risingStars.add(ranked);
            }
        }
        
        // Ordina per crescita
        risingStars.sort(Comparator
            .comparing(RankedTrainer::getGrowthRate)
            .reversed()
        );
        
        if (limit != null && risingStars.size() > limit) {
            risingStars = risingStars.subList(0, limit);
        }
        
        return risingStars;
    }
    
    /**
     * Calcola score di ranking per un trainer
     * Algoritmo: (lessons * 10) + (rating * 20) + (reviews * 5)
     */
    private RankingScore calculateRankingScore(Instructor instructor) {
        // Conta lezioni completate
        long completedLessons = bookingRepository.countByInstructorIdAndStatus(
            instructor.getId(), 
            "COMPLETED"
        );
        
        // Rating medio (1-5)
        Double averageRating = instructor.getAverageRating() != null 
            ? instructor.getAverageRating() 
            : 0.0;
        
        // Numero recensioni
        Integer reviewCount = instructor.getReviewCount() != null 
            ? instructor.getReviewCount() 
            : 0;
        
        // Calcola componenti score
        double lessonsScore = completedLessons * 10;
        double ratingScore = averageRating * 20;
        double reviewsScore = reviewCount * 5;
        
        // Bonus per trainer verificati
        double verifiedBonus = instructor.isVerified() ? 50 : 0;
        
        // Bonus per trainer con molte discipline
        double versatilityBonus = instructor.getSports().size() * 10;
        
        double totalScore = lessonsScore + ratingScore + reviewsScore + verifiedBonus + versatilityBonus;
        
        return new RankingScore(
            lessonsScore,
            ratingScore,
            reviewsScore,
            verifiedBonus,
            versatilityBonus,
            totalScore
        );
    }
    
    /**
     * Determina badge basato su score
     */
    private String getRankBadge(double score) {
        if (score >= 500) return "👑 Leggenda";
        if (score >= 300) return "💎 Elite";
        if (score >= 200) return "⭐ Pro";
        if (score >= 100) return "✨ Expert";
        if (score >= 50) return "🔰 Rising";
        return "🌱 Nuovo";
    }
    
    // Inner classes
    public static class RankedTrainer {
        private Instructor instructor;
        private RankingScore score;
        private String rankBadge;
        private String specialBadge;
        private int rank;
        private double growthRate;
        
        public RankedTrainer(Instructor instructor, RankingScore score, String rankBadge) {
            this.instructor = instructor;
            this.score = score;
            this.rankBadge = rankBadge;
        }
        
        // Getters and setters
        public Instructor getInstructor() { return instructor; }
        public RankingScore getScore() { return score; }
        public double getTotalScore() { return score.getTotalScore(); }
        public String getRankBadge() { return rankBadge; }
        public String getSpecialBadge() { return specialBadge; }
        public void setSpecialBadge(String specialBadge) { this.specialBadge = specialBadge; }
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
        public double getGrowthRate() { return growthRate; }
        public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }
    }
    
    public static class RankingScore {
        private double lessonsScore;
        private double ratingScore;
        private double reviewsScore;
        private double verifiedBonus;
        private double versatilityBonus;
        private double totalScore;
        
        public RankingScore(double lessonsScore, double ratingScore, double reviewsScore, 
                          double verifiedBonus, double versatilityBonus, double totalScore) {
            this.lessonsScore = lessonsScore;
            this.ratingScore = ratingScore;
            this.reviewsScore = reviewsScore;
            this.verifiedBonus = verifiedBonus;
            this.versatilityBonus = versatilityBonus;
            this.totalScore = totalScore;
        }
        
        // Getters
        public double getLessonsScore() { return lessonsScore; }
        public double getRatingScore() { return ratingScore; }
        public double getReviewsScore() { return reviewsScore; }
        public double getVerifiedBonus() { return verifiedBonus; }
        public double getVersatilityBonus() { return versatilityBonus; }
        public double getTotalScore() { return totalScore; }
    }
}
