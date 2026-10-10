package com.skillsphere.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    @JsonIgnore
    private AssessmentEntity assessment;

    @Column(nullable = false, length = 1000)
    private String questionText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quiz_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "sequence_number")
    @Column(name = "option_text")
    private List<String> options = new ArrayList<>();

    private int correctIndex;

    @Column(length = 1000)
    private String explanation;

    public QuizQuestionEntity() {
    }

    public QuizQuestionEntity(String questionText, List<String> options, int correctIndex, String explanation) {
        this.questionText = questionText;
        if (options != null) {
            this.options = options;
        }
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public AssessmentEntity getAssessment() {
        return assessment;
    }

    public void setAssessment(AssessmentEntity assessment) {
        this.assessment = assessment;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
