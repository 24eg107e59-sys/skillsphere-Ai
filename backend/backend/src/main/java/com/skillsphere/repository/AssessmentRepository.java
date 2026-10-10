package com.skillsphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsphere.entity.AssessmentEntity;

@Repository
public interface AssessmentRepository extends JpaRepository<AssessmentEntity, Long> {

    Optional<AssessmentEntity> findByCode(String code);

    Optional<AssessmentEntity> findBySkillIgnoreCase(String skill);

    boolean existsByCode(String code);
}
