package com.skillsphere.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skillsphere.dto.Course;
import com.skillsphere.entity.CourseEntity;
import com.skillsphere.repository.CourseRepository;

@Service
public class CourseCatalogService {

    private final CourseRepository courseRepository;

    public CourseCatalogService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public List<Course> getCoursesBySkills(List<String> skills) {
        List<Course> all = getAllCourses();
        if (skills == null || skills.isEmpty()) {
            return all;
        }

        Set<String> normalizedSkills = skills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return all.stream()
                .filter(c -> normalizedSkills.contains(c.getSkill().toLowerCase()))
                .collect(Collectors.toList());
    }

    public Optional<Course> getCourseById(String id) {
        if (id == null) return Optional.empty();
        return courseRepository.findByCode(id.trim())
                .map(this::toDto);
    }

    private Course toDto(CourseEntity c) {
        return new Course(
                c.getCode(),
                c.getTitle(),
                c.getSkill(),
                c.getLevel(),
                c.getDuration(),
                c.getRating() != null ? c.getRating() : 4.8,
                c.getDescription(),
                c.getProvider(),
                c.getModules() != null ? c.getModules() : new ArrayList<>(),
                c.getIcon()
        );
    }
}
