package com.skillsphere.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skillsphere.dto.CareerOption;
import com.skillsphere.dto.SkillGapResponse;
import com.skillsphere.entity.Profile;

@Service
public class SkillGapService {

    private final ProfileService profileService;
    private final CareerService careerService;

    public SkillGapService(ProfileService profileService, CareerService careerService) {
        this.profileService = profileService;
        this.careerService = careerService;
    }

    public SkillGapResponse analyzeGapForUser(Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);
        String targetCareer = profile.getTargetCareer();
        if (targetCareer == null || targetCareer.trim().isEmpty()) {
            targetCareer = "Full Stack Java Developer";
        }

        // Get required skills for chosen career
        Optional<CareerOption> careerOpt = careerService.getCareerByTitle(targetCareer);
        CareerOption career = careerOpt.orElseGet(() -> careerService.getAllCareers().get(0));

        List<String> requiredSkills = career.getRequiredSkills();

        // Parse student's current skills
        List<String> studentSkills = new ArrayList<>();
        if (profile.getCurrentSkills() != null && !profile.getCurrentSkills().trim().isEmpty()) {
            studentSkills = Arrays.stream(profile.getCurrentSkills().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        // Normalize student skills for case-insensitive lookup
        Set<String> studentSkillsNormalized = studentSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String req : requiredSkills) {
            if (studentSkillsNormalized.contains(req.toLowerCase())) {
                matchedSkills.add(req);
            } else {
                missingSkills.add(req);
            }
        }

        // Find additional skills known by student not in required benchmark list
        Set<String> requiredNormalized = requiredSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<String> additionalSkills = studentSkills.stream()
                .filter(s -> !requiredNormalized.contains(s.toLowerCase()))
                .collect(Collectors.toList());

        // Calculate score
        int totalRequired = requiredSkills.size();
        int matchedCount = matchedSkills.size();
        int missingCount = missingSkills.size();
        int readinessScore = totalRequired > 0 ? (matchedCount * 100) / totalRequired : 0;

        // Determine readiness level
        String readinessLevel;
        if (readinessScore >= 80) {
            readinessLevel = "High Readiness";
        } else if (readinessScore >= 45) {
            readinessLevel = "Moderate Gap";
        } else {
            readinessLevel = "High Gap";
        }

        // Generate personalized AI insights summary
        String aiSummary = generateAiSummary(career.getTitle(), readinessScore, readinessLevel, matchedSkills, missingSkills);

        return new SkillGapResponse(
                userId,
                career.getTitle(),
                readinessScore,
                totalRequired,
                matchedCount,
                missingCount,
                matchedSkills,
                missingSkills,
                additionalSkills,
                readinessLevel,
                aiSummary
        );
    }

    private String generateAiSummary(String careerTitle, int score, String level, List<String> matched, List<String> missing) {
        if (score == 100) {
            return String.format(
                "Outstanding! You have a 100%% match with all core competencies required for %s. You are prepared for technical interviews and advanced project implementation.",
                careerTitle
            );
        }

        if (score >= 70) {
            return String.format(
                "Great foundation! You are %d%% aligned with %s requirements (%d of %d skills mastered). Focusing on your missing competencies (%s) will elevate your profile to top candidate status.",
                score, careerTitle, matched.size(), (matched.size() + missing.size()), String.join(", ", missing)
            );
        }

        if (score >= 40) {
            return String.format(
                "You have a promising baseline with %d%% readiness for %s. To bridge the remaining gap, focus your next learning milestones on %s.",
                score, careerTitle, String.join(", ", missing)
            );
        }

        return String.format(
            "You are at the early stage (%d%% readiness) for %s. A structured step-by-step roadmap focusing on core foundational tools (%s) will help you rapidly close this gap.",
            score, careerTitle, String.join(", ", missing)
        );
    }
}
