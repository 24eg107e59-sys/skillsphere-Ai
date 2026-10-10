package com.skillsphere.dto;

import java.util.List;

public class AssessmentReview {

    private String assessmentId;
    private String assessmentTitle;
    private String skill;
    private int score;
    private int totalQuestions;
    private int percentage;
    private boolean passed;
    private String badgeAwarded;
    private String feedback;
    private List<QuestionReviewItem> reviews;

    public AssessmentReview() {
    }

    public AssessmentReview(String assessmentId, String assessmentTitle, String skill, int score,
                            int totalQuestions, int percentage, boolean passed, String badgeAwarded,
                            String feedback, List<QuestionReviewItem> reviews) {
        this.assessmentId = assessmentId;
        this.assessmentTitle = assessmentTitle;
        this.skill = skill;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.passed = passed;
        this.badgeAwarded = badgeAwarded;
        this.feedback = feedback;
        this.reviews = reviews;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getBadgeAwarded() {
        return badgeAwarded;
    }

    public void setBadgeAwarded(String badgeAwarded) {
        this.badgeAwarded = badgeAwarded;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<QuestionReviewItem> getReviews() {
        return reviews;
    }

    public void setReviews(List<QuestionReviewItem> reviews) {
        this.reviews = reviews;
    }
}
