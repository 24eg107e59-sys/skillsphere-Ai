package com.skillsphere.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.dto.Course;
import com.skillsphere.dto.SkillGapResponse;
import com.skillsphere.service.CourseCatalogService;
import com.skillsphere.service.SkillGapService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseCatalogService courseCatalogService;
    private final SkillGapService skillGapService;

    public CourseController(CourseCatalogService courseCatalogService, SkillGapService skillGapService) {
        this.courseCatalogService = courseCatalogService;
        this.skillGapService = skillGapService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(@RequestParam(required = false) String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            return ResponseEntity.ok(courseCatalogService.getCoursesBySkills(List.of(skill.trim())));
        }
        return ResponseEntity.ok(courseCatalogService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable String id) {
        Optional<Course> course = courseCatalogService.getCourseById(id);
        if (course.isPresent()) {
            return ResponseEntity.ok(course.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Course not found"));
    }

    @GetMapping("/recommended/{userId}")
    public ResponseEntity<?> getRecommendedCourses(@PathVariable Long userId) {
        try {
            SkillGapResponse gap = skillGapService.analyzeGapForUser(userId);
            List<Course> recommended = courseCatalogService.getCoursesBySkills(gap.getMissingSkills());
            if (recommended.isEmpty()) {
                recommended = courseCatalogService.getAllCourses().subList(0, Math.min(4, courseCatalogService.getAllCourses().size()));
            }
            return ResponseEntity.ok(recommended);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching recommended courses: " + e.getMessage()));
        }
    }
}
