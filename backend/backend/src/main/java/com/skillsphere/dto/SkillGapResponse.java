package com.skillsphere.dto;

import java.util.List;

public class SkillGapResponse {

    private Long userId;
    private String targetCareer;
    private int readinessScore;
    private int totalRequiredCount;
    private int matchedCount;
    private int missingCount;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> additionalSkills;
    private String readinessLevel;
    private String aiSummary;

    public SkillGapResponse() {
    }

    public SkillGapResponse(Long userId, String targetCareer, int readinessScore, int totalRequiredCount,
                            int matchedCount, int missingCount, List<String> matchedSkills,
                            List<String> missingSkills, List<String> additionalSkills,
                            String readinessLevel, String aiSummary) {
        this.userId = userId;
        this.targetCareer = targetCareer;
        this.readinessScore = readinessScore;
        this.totalRequiredCount = totalRequiredCount;
        this.matchedCount = matchedCount;
        this.missingCount = missingCount;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.additionalSkills = additionalSkills;
        this.readinessLevel = readinessLevel;
        this.aiSummary = aiSummary;
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

    public int getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(int readinessScore) {
        this.readinessScore = readinessScore;
    }

    public int getTotalRequiredCount() {
        return totalRequiredCount;
    }

    public void setTotalRequiredCount(int totalRequiredCount) {
        this.totalRequiredCount = totalRequiredCount;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public void setMatchedCount(int matchedCount) {
        this.matchedCount = matchedCount;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public void setMissingCount(int missingCount) {
        this.missingCount = missingCount;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getAdditionalSkills() {
        return additionalSkills;
    }

    public void setAdditionalSkills(List<String> additionalSkills) {
        this.additionalSkills = additionalSkills;
    }

    public String getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(String readinessLevel) {
        this.readinessLevel = readinessLevel;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }
}
