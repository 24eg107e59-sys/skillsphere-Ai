package com.skillsphere.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_progress")
public class StudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true)
    private Long userId;

    private String targetCareer;
    private int readinessScore;
    private int completedPhases;
    private int totalPhases = 3;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    public StudentProgress() {
    }

    public StudentProgress(Long userId, String targetCareer, int readinessScore, int completedPhases, int totalPhases) {
        this.userId = userId;
        this.targetCareer = targetCareer;
        this.readinessScore = readinessScore;
        this.completedPhases = completedPhases;
        this.totalPhases = totalPhases;
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
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

    public int getCompletedPhases() {
        return completedPhases;
    }

    public void setCompletedPhases(int completedPhases) {
        this.completedPhases = completedPhases;
    }

    public int getTotalPhases() {
        return totalPhases;
    }

    public void setTotalPhases(int totalPhases) {
        this.totalPhases = totalPhases;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
