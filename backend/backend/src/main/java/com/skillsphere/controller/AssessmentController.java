package com.skillsphere.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.dto.AssessmentDetail;
import com.skillsphere.dto.AssessmentReview;
import com.skillsphere.dto.AssessmentSubmission;
import com.skillsphere.entity.AssessmentResult;
import com.skillsphere.service.AssessmentService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping
    public ResponseEntity<List<AssessmentDetail>> getAllAssessments() {
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAssessmentById(@PathVariable String id) {
        Optional<AssessmentDetail> assessment = assessmentService.getAssessmentById(id);
        if (assessment.isPresent()) {
            return ResponseEntity.ok(assessment.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Assessment not found"));
    }

    @PostMapping("/submit/{userId}")
    public ResponseEntity<?> submitAssessment(@PathVariable Long userId,
                                             @RequestBody AssessmentSubmission submission) {
        try {
            AssessmentReview review = assessmentService.submitAssessment(userId, submission);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error submitting assessment: " + e.getMessage()));
        }
    }

    @GetMapping("/results/{userId}")
    public ResponseEntity<List<AssessmentResult>> getUserResults(@PathVariable Long userId) {
        return ResponseEntity.ok(assessmentService.getUserResults(userId));
    }
}
