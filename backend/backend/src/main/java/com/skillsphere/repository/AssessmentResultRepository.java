package com.skillsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillsphere.entity.AssessmentResult;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {

    List<AssessmentResult> findByUserIdOrderByCompletedAtDesc(Long userId);

    List<AssessmentResult> findByUserIdAndSkill(Long userId, String skill);
}
