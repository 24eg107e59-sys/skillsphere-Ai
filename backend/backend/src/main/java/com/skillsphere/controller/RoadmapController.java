package com.skillsphere.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.dto.RoadmapResponse;
import com.skillsphere.service.RoadmapService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;

    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRoadmap(@PathVariable Long userId) {
        try {
            RoadmapResponse response = roadmapService.generateRoadmapForUser(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error generating roadmap: " + e.getMessage()));
        }
    }
}
