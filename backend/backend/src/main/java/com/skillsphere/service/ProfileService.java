package com.skillsphere.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skillsphere.entity.Profile;
import com.skillsphere.entity.User;
import com.skillsphere.repository.ProfileRepository;
import com.skillsphere.repository.UserRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public Profile getProfileByUserId(Long userId) {
        Optional<Profile> existing = profileRepository.findByUserId(userId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Initialize default profile from User entity if exists
        Optional<User> userOpt = userRepository.findById(userId);
        Profile profile = new Profile();
        profile.setUserId(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            profile.setFullName(user.getName());
            profile.setEmail(user.getEmail());
        }
        profile.setExperienceLevel("Beginner");
        profile.setTargetCareer("Full Stack Java Developer");
        profile.setCurrentSkills("");
        profile.setDegree("B.Tech Computer Science");
        profile.setGraduationYear("2026");
        profile.setBio("Passionate learner exploring software development and AI.");

        return profileRepository.save(profile);
    }

    public Profile updateProfile(Long userId, Profile updatedData) {
        Profile profile = getProfileByUserId(userId);

        if (updatedData.getFullName() != null && !updatedData.getFullName().trim().isEmpty()) {
            profile.setFullName(updatedData.getFullName().trim());
        }
        if (updatedData.getPhone() != null) {
            profile.setPhone(updatedData.getPhone().trim());
        }
        if (updatedData.getCollege() != null) {
            profile.setCollege(updatedData.getCollege().trim());
        }
        if (updatedData.getDegree() != null) {
            profile.setDegree(updatedData.getDegree().trim());
        }
        if (updatedData.getGraduationYear() != null) {
            profile.setGraduationYear(updatedData.getGraduationYear().trim());
        }
        if (updatedData.getExperienceLevel() != null) {
            profile.setExperienceLevel(updatedData.getExperienceLevel().trim());
        }
        if (updatedData.getTargetCareer() != null) {
            profile.setTargetCareer(updatedData.getTargetCareer().trim());
        }
        if (updatedData.getCurrentSkills() != null) {
            profile.setCurrentSkills(updatedData.getCurrentSkills().trim());
        }
        if (updatedData.getBio() != null) {
            profile.setBio(updatedData.getBio().trim());
        }

        return profileRepository.save(profile);
    }

    public Profile addSkill(Long userId, String skillName) {
        Profile profile = getProfileByUserId(userId);
        if (skillName == null || skillName.trim().isEmpty()) {
            return profile;
        }

        String cleanSkill = skillName.trim();
        String current = profile.getCurrentSkills();

        List<String> skillsList = new ArrayList<>();
        if (current != null && !current.trim().isEmpty()) {
            skillsList = Arrays.stream(current.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        boolean exists = skillsList.stream().anyMatch(s -> s.equalsIgnoreCase(cleanSkill));
        if (!exists) {
            skillsList.add(cleanSkill);
            profile.setCurrentSkills(String.join(", ", skillsList));
            return profileRepository.save(profile);
        }

        return profile;
    }

    public Profile removeSkill(Long userId, String skillName) {
        Profile profile = getProfileByUserId(userId);
        if (skillName == null || profile.getCurrentSkills() == null || profile.getCurrentSkills().trim().isEmpty()) {
            return profile;
        }

        String cleanSkill = skillName.trim();
        List<String> skillsList = Arrays.stream(profile.getCurrentSkills().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.equalsIgnoreCase(cleanSkill))
                .collect(Collectors.toList());

        profile.setCurrentSkills(String.join(", ", skillsList));
        return profileRepository.save(profile);
    }
}
