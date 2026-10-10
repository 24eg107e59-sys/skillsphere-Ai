package com.skillsphere.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.skillsphere.dto.SkillGapResponse;
import com.skillsphere.entity.StudentProgress;
import com.skillsphere.repository.StudentProgressRepository;

@Service
public class StudentProgressService {

    private final StudentProgressRepository progressRepository;
    private final SkillGapService skillGapService;

    public StudentProgressService(StudentProgressRepository progressRepository,
                                  SkillGapService skillGapService) {
        this.progressRepository = progressRepository;
        this.skillGapService = skillGapService;
    }

    public StudentProgress getOrCreateProgress(Long userId) {
        return progressRepository.findByUserId(userId).orElseGet(() -> {
            SkillGapResponse gap = skillGapService.analyzeGapForUser(userId);
            int score = gap.getReadinessScore();
            int completed = score >= 80 ? 2 : (score >= 40 ? 1 : 0);

            StudentProgress progress = new StudentProgress(
                userId,
                gap.getTargetCareer(),
                score,
                completed,
                3
            );
            return progressRepository.save(progress);
        });
    }

    public StudentProgress updateProgress(Long userId, int completedPhases) {
        StudentProgress progress = getOrCreateProgress(userId);
        progress.setCompletedPhases(Math.min(completedPhases, progress.getTotalPhases()));
        progress.setLastUpdated(LocalDateTime.now());
        return progressRepository.save(progress);
    }
}
