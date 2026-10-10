package com.skillsphere.dto;

import java.util.List;

public class CareerOption {

    private String id;
    private String title;
    private String description;
    private String icon;
    private List<String> requiredSkills;

    public CareerOption() {
    }

    public CareerOption(String id, String title, String description, String icon, List<String> requiredSkills) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.requiredSkills = requiredSkills;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
