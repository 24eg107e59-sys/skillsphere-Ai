package com.skillsphere.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillsphere.dto.CareerOption;
import com.skillsphere.entity.CareerPath;
import com.skillsphere.entity.Skill;
import com.skillsphere.repository.CareerPathRepository;

@Service
public class CareerService {

    private final CareerPathRepository careerPathRepository;

    public CareerService(CareerPathRepository careerPathRepository) {
        this.careerPathRepository = careerPathRepository;
    }

    public List<CareerOption> getAllCareers() {
        return careerPathRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<CareerOption> getCareerByTitle(String title) {
        if (title == null) return Optional.empty();
        return careerPathRepository.findByTitleIgnoreCase(title.trim())
                .map(this::toDto);
    }

    public Optional<CareerOption> getCareerByCode(String code) {
        if (code == null) return Optional.empty();
        return careerPathRepository.findByCode(code.trim())
                .map(this::toDto);
    }

    private CareerOption toDto(CareerPath cp) {
        List<String> skills = cp.getRequiredSkills().stream()
                .map(Skill::getName)
                .sorted()
                .toList();
        return new CareerOption(
                cp.getCode(),
                cp.getTitle(),
                cp.getDescription(),
                cp.getIcon(),
                skills
        );
    }
}
