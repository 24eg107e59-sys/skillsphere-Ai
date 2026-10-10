package com.skillsphere.dto;

import java.util.Map;

public class AssessmentSubmission {

    private String assessmentId;
    private Map<Integer, Integer> answers; // questionId -> selectedOptionIndex

    public AssessmentSubmission() {
    }

    public AssessmentSubmission(String assessmentId, Map<Integer, Integer> answers) {
        this.assessmentId = assessmentId;
        this.answers = answers;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Map<Integer, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, Integer> answers) {
        this.answers = answers;
    }
}
