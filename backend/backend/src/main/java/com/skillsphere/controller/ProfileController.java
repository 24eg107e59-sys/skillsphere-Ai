package com.skillsphere.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.entity.Profile;
import com.skillsphere.service.ProfileService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        try {
            Profile profile = profileService.getProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching profile: " + e.getMessage()));
        }
    }

    @RequestMapping(value = "/{userId}", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody Profile profileData) {
        try {
            Profile updated = profileService.updateProfile(userId, profileData);
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "profile", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating profile: " + e.getMessage()));
        }
    }

    @PostMapping("/{userId}/skills")
    public ResponseEntity<?> addSkill(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        try {
            String skill = body.get("skill");
            if (skill == null || skill.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Skill name cannot be empty"));
            }
            Profile updated = profileService.addSkill(userId, skill.trim());
            return ResponseEntity.ok(Map.of(
                "message", "Skill added successfully",
                "profile", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error adding skill: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/skills/{skillName}")
    public ResponseEntity<?> removeSkill(@PathVariable Long userId, @PathVariable String skillName) {
        try {
            Profile updated = profileService.removeSkill(userId, skillName);
            return ResponseEntity.ok(Map.of(
                "message", "Skill removed successfully",
                "profile", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error removing skill: " + e.getMessage()));
        }
    }
}
