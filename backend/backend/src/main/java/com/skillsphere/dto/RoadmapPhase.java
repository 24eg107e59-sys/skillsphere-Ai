package com.skillsphere.dto;

import java.util.List;

public class RoadmapPhase {

    private int phase;
    private String title;
    private String skillFocus;
    private String duration;
    private String status;
    private String description;
    private List<String> topics;
    private String portfolioProject;

    public RoadmapPhase() {
    }

    public RoadmapPhase(int phase, String title, String skillFocus, String duration,
                        String status, String description, List<String> topics, String portfolioProject) {
        this.phase = phase;
        this.title = title;
        this.skillFocus = skillFocus;
        this.duration = duration;
        this.status = status;
        this.description = description;
        this.topics = topics;
        this.portfolioProject = portfolioProject;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSkillFocus() {
        return skillFocus;
    }

    public void setSkillFocus(String skillFocus) {
        this.skillFocus = skillFocus;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public String getPortfolioProject() {
        return portfolioProject;
    }

    public void setPortfolioProject(String portfolioProject) {
        this.portfolioProject = portfolioProject;
    }
}
