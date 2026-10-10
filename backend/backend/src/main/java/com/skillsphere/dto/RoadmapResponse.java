package com.skillsphere.dto;

import java.util.List;

public class RoadmapResponse {

    private Long userId;
    private String targetCareer;
    private int totalPhases;
    private int estimatedWeeks;
    private List<RoadmapPhase> phases;
    private List<Course> recommendedCourses;
    private String aiRoadmapAdvice;

    public RoadmapResponse() {
    }

    public RoadmapResponse(Long userId, String targetCareer, int totalPhases, int estimatedWeeks,
                           List<RoadmapPhase> phases, List<Course> recommendedCourses, String aiRoadmapAdvice) {
        this.userId = userId;
        this.targetCareer = targetCareer;
        this.totalPhases = totalPhases;
        this.estimatedWeeks = estimatedWeeks;
        this.phases = phases;
        this.recommendedCourses = recommendedCourses;
        this.aiRoadmapAdvice = aiRoadmapAdvice;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTargetCareer() {
        return targetCareer;
    }

    public void setTargetCareer(String targetCareer) {
        this.targetCareer = targetCareer;
    }

    public int getTotalPhases() {
        return totalPhases;
    }

    public void setTotalPhases(int totalPhases) {
        this.totalPhases = totalPhases;
    }

    public int getEstimatedWeeks() {
        return estimatedWeeks;
    }

    public void setEstimatedWeeks(int estimatedWeeks) {
        this.estimatedWeeks = estimatedWeeks;
    }

    public List<RoadmapPhase> getPhases() {
        return phases;
    }

    public void setPhases(List<RoadmapPhase> phases) {
        this.phases = phases;
    }

    public List<Course> getRecommendedCourses() {
        return recommendedCourses;
    }

    public void setRecommendedCourses(List<Course> recommendedCourses) {
        this.recommendedCourses = recommendedCourses;
    }

    public String getAiRoadmapAdvice() {
        return aiRoadmapAdvice;
    }

    public void setAiRoadmapAdvice(String aiRoadmapAdvice) {
        this.aiRoadmapAdvice = aiRoadmapAdvice;
    }
}
