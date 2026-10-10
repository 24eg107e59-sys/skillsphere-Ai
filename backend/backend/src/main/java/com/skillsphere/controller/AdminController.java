package com.skillsphere.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.entity.CourseEntity;
import com.skillsphere.entity.Skill;
import com.skillsphere.entity.User;
import com.skillsphere.repository.AssessmentResultRepository;
import com.skillsphere.repository.CareerPathRepository;
import com.skillsphere.repository.CourseRepository;
import com.skillsphere.repository.ProfileRepository;
import com.skillsphere.repository.SkillRepository;
import com.skillsphere.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final SkillRepository skillRepository;
    private final CareerPathRepository careerPathRepository;
    private final CourseRepository courseRepository;
    private final AssessmentResultRepository assessmentResultRepository;

    public AdminController(UserRepository userRepository,
                           ProfileRepository profileRepository,
                           SkillRepository skillRepository,
                           CareerPathRepository careerPathRepository,
                           CourseRepository courseRepository,
                           AssessmentResultRepository assessmentResultRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.skillRepository = skillRepository;
        this.careerPathRepository = careerPathRepository;
        this.courseRepository = courseRepository;
        this.assessmentResultRepository = assessmentResultRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalProfiles", profileRepository.count());
        stats.put("totalSkills", skillRepository.count());
        stats.put("totalCareers", careerPathRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalAssessmentsCompleted", assessmentResultRepository.count());
        stats.put("systemStatus", "OPERATIONAL");

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> sanitized = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("role", u.getRole());
            return map;
        }).toList();

        return ResponseEntity.ok(sanitized);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            if ("admin@skillsphere.com".equalsIgnoreCase(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Cannot delete main administrator"));
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found")));
    }

    @GetMapping("/skills")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillRepository.findAll());
    }

    @PostMapping("/skills")
    public ResponseEntity<?> addSkill(@RequestBody Skill skill) {
        if (skill.getName() == null || skill.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Skill name is required"));
        }
        if (skillRepository.existsByNameIgnoreCase(skill.getName().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Skill already exists"));
        }
        Skill saved = skillRepository.save(new Skill(skill.getName().trim(), skill.getCategory(), skill.getDescription()));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseEntity>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @PostMapping("/courses")
    public ResponseEntity<?> addCourse(@RequestBody CourseEntity course) {
        if (course.getTitle() == null || course.getSkill() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Course title and skill are required"));
        }
        if (course.getCode() == null || course.getCode().trim().isEmpty()) {
            course.setCode("crs-" + System.currentTimeMillis());
        }
        CourseEntity saved = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Course not found"));
        }
        courseRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Course removed successfully"));
    }
}
