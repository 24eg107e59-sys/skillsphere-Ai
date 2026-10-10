package com.skillsphere.dto;

import java.util.List;

public class AssessmentDetail {

    private String id;
    private String title;
    private String skill;
    private String difficulty;
    private int timeLimitMinutes;
    private String icon;
    private List<QuizQuestion> questions;

    public AssessmentDetail() {
    }

    public AssessmentDetail(String id, String title, String skill, String difficulty,
                            int timeLimitMinutes, String icon, List<QuizQuestion> questions) {
        this.id = id;
        this.title = title;
        this.skill = skill;
        this.difficulty = difficulty;
        this.timeLimitMinutes = timeLimitMinutes;
        this.icon = icon;
        this.questions = questions;
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }
}
