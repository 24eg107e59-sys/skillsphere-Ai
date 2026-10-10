package com.skillsphere.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.entity.StudentProgress;
import com.skillsphere.service.StudentProgressService;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final StudentProgressService progressService;

    public ProgressController(StudentProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StudentProgress> getProgress(@PathVariable Long userId) {
        StudentProgress progress = progressService.getOrCreateProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<StudentProgress> updateProgress(@PathVariable Long userId,
                                                          @RequestBody Map<String, Integer> payload) {
        int completed = payload.getOrDefault("completedPhases", 0);
        StudentProgress progress = progressService.updateProgress(userId, completed);
        return ResponseEntity.ok(progress);
    }
}
