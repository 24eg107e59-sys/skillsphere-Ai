package com.skillsphere.dto;

import java.util.List;

public class QuestionReviewItem {

    private int questionId;
    private String question;
    private List<String> options;
    private int selectedOption;
    private int correctOption;
    private boolean isCorrect;
    private String explanation;

    public QuestionReviewItem() {
    }

    public QuestionReviewItem(int questionId, String question, List<String> options,
                              int selectedOption, int correctOption, boolean isCorrect, String explanation) {
        this.questionId = questionId;
        this.question = question;
        this.options = options;
        this.selectedOption = selectedOption;
        this.correctOption = correctOption;
        this.isCorrect = isCorrect;
        this.explanation = explanation;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
