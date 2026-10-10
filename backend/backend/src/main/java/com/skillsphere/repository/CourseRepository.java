package com.skillsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsphere.entity.CourseEntity;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    Optional<CourseEntity> findByCode(String code);

    List<CourseEntity> findBySkillIgnoreCase(String skill);

    List<CourseEntity> findByLevelIgnoreCase(String level);

    boolean existsByCode(String code);
}
