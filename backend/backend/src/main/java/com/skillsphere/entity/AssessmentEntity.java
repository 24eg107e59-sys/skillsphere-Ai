package com.skillsphere.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "assessments")
public class AssessmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g. asm-spring-boot, asm-react

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String skill;

    private String difficulty; // Beginner, Intermediate, Advanced
    private int timeLimitMinutes;
    private String icon;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<QuizQuestionEntity> questions = new ArrayList<>();

    public AssessmentEntity() {
    }

    public AssessmentEntity(String code, String title, String skill, String difficulty, int timeLimitMinutes, String icon) {
        this.code = code;
        this.title = title;
        this.skill = skill;
        this.difficulty = difficulty;
        this.timeLimitMinutes = timeLimitMinutes;
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public List<QuizQuestionEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionEntity> questions) {
        this.questions = questions;
    }

    public void addQuestion(QuizQuestionEntity question) {
        questions.add(question);
        question.setAssessment(this);
    }
}
