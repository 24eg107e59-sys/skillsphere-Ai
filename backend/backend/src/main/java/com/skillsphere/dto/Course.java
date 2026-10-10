package com.skillsphere.dto;

import java.util.List;

public class Course {

    private String id;
    private String title;
    private String skill;
    private String level;
    private String duration;
    private double rating;
    private String description;
    private String provider;
    private List<String> modules;
    private String icon;

    public Course() {
    }

    public Course(String id, String title, String skill, String level, String duration,
                  double rating, String description, String provider, List<String> modules, String icon) {
        this.id = id;
        this.title = title;
        this.skill = skill;
        this.level = level;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
        this.provider = provider;
        this.modules = modules;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
