package com.skillsphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsphere.entity.CareerPath;

@Repository
public interface CareerPathRepository extends JpaRepository<CareerPath, Long> {

    Optional<CareerPath> findByCode(String code);

    Optional<CareerPath> findByTitleIgnoreCase(String title);

    boolean existsByCode(String code);
}
