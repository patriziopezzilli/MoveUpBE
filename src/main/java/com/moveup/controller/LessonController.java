package com.moveup.controller;

import com.moveup.model.Lesson;
import com.moveup.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
public class LessonController {
    
    @Autowired
    private LessonService lessonService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createLesson(@Valid @RequestBody Lesson lesson) {
        try {
            Lesson createdLesson = lessonService.createLesson(lesson);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Lezione creata con successo", "lesson", createdLesson));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{lessonId}")
    public ResponseEntity<Lesson> getLesson(@PathVariable String lessonId) {
        return lessonService.getLessonById(lessonId)
                .map(lesson -> ResponseEntity.ok(lesson))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable String lessonId, @Valid @RequestBody Lesson updatedLesson) {
        try {
            Lesson lesson = lessonService.updateLesson(lessonId, updatedLesson);
            return ResponseEntity.ok(lesson);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Map<String, String>> deleteLesson(@PathVariable String lessonId) {
        try {
            lessonService.deleteLesson(lessonId);
            return ResponseEntity.ok(Map.of("message", "Lezione eliminata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Lesson>> getLessonsByInstructor(@PathVariable String instructorId) {
        List<Lesson> lessons = lessonService.getLessonsByInstructor(instructorId);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/sport/{sportId}")
    public ResponseEntity<List<Lesson>> getLessonsBySport(@PathVariable String sportId) {
        List<Lesson> lessons = lessonService.getLessonsBySport(sportId);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Lesson>> searchLessons(@RequestParam String query) {
        List<Lesson> lessons = lessonService.searchLessons(query);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<List<Lesson>> getLessonsByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice) {
        List<Lesson> lessons = lessonService.getLessonsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/level/{level}")
    public ResponseEntity<List<Lesson>> getLessonsByLevel(@PathVariable String level) {
        List<Lesson> lessons = lessonService.getLessonsByLevel(level);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Lesson>> getLessonsByCity(@PathVariable String city) {
        List<Lesson> lessons = lessonService.getLessonsByCity(city);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/near")
    public ResponseEntity<List<Lesson>> getLessonsNearLocation(@RequestParam double latitude, @RequestParam double longitude, @RequestParam(defaultValue = "10") double radiusKm) {
        List<Lesson> lessons = lessonService.getLessonsNearLocation(latitude, longitude, radiusKm);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<Lesson>> getPopularLessons(@RequestParam(defaultValue = "10") int limit) {
        List<Lesson> lessons = lessonService.getPopularLessons(limit);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Lesson>> getRecentLessons(@RequestParam(defaultValue = "10") int limit) {
        List<Lesson> lessons = lessonService.getRecentLessons(limit);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/special-offers")
    public ResponseEntity<List<Lesson>> getLessonsWithSpecialOffers() {
        List<Lesson> lessons = lessonService.getLessonsWithSpecialOffers();
        return ResponseEntity.ok(lessons);
    }
    
    @PostMapping("/search-advanced")
    public ResponseEntity<List<Lesson>> searchLessonsAdvanced(@RequestBody LessonService.SearchCriteria criteria) {
        List<Lesson> lessons = lessonService.searchLessonsAdvanced(criteria);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/{lessonId}/stats")
    public ResponseEntity<LessonService.LessonStatistics> getLessonStatistics(@PathVariable String lessonId) {
        try {
            LessonService.LessonStatistics stats = lessonService.getLessonStatistics(lessonId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}