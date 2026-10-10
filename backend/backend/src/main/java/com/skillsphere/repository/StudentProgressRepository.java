package com.skillsphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsphere.entity.StudentProgress;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {

    Optional<StudentProgress> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
