package com.skillsphere.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillsphere.dto.AssessmentDetail;
import com.skillsphere.dto.AssessmentReview;
import com.skillsphere.dto.AssessmentSubmission;
import com.skillsphere.dto.QuestionReviewItem;
import com.skillsphere.dto.QuizQuestion;
import com.skillsphere.entity.AssessmentEntity;
import com.skillsphere.entity.AssessmentResult;
import com.skillsphere.entity.Profile;
import com.skillsphere.entity.QuizQuestionEntity;
import com.skillsphere.repository.AssessmentRepository;
import com.skillsphere.repository.AssessmentResultRepository;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentResultRepository assessmentResultRepository;
    private final ProfileService profileService;

    public AssessmentService(AssessmentRepository assessmentRepository,
                             AssessmentResultRepository assessmentResultRepository,
                             ProfileService profileService) {
        this.assessmentRepository = assessmentRepository;
        this.assessmentResultRepository = assessmentResultRepository;
        this.profileService = profileService;
    }

    public List<AssessmentDetail> getAllAssessments() {
        return assessmentRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<AssessmentDetail> getAssessmentById(String id) {
        if (id == null) return Optional.empty();
        return assessmentRepository.findByCode(id.trim())
                .map(this::toDto);
    }

    private AssessmentDetail toDto(AssessmentEntity entity) {
        List<QuizQuestion> questionDtos = new ArrayList<>();
        int qId = 1;
        for (QuizQuestionEntity qe : entity.getQuestions()) {
            questionDtos.add(new QuizQuestion(
                qId++,
                qe.getQuestionText(),
                qe.getOptions(),
                qe.getCorrectIndex(),
                qe.getExplanation()
            ));
        }

        return new AssessmentDetail(
            entity.getCode(),
            entity.getTitle(),
            entity.getSkill(),
            entity.getDifficulty(),
            entity.getTimeLimitMinutes(),
            entity.getIcon(),
            questionDtos
        );
    }

    public AssessmentReview submitAssessment(Long userId, AssessmentSubmission submission) {
        AssessmentDetail assessment = getAssessmentById(submission.getAssessmentId())
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found: " + submission.getAssessmentId()));

        Map<Integer, Integer> answers = submission.getAnswers() != null ? submission.getAnswers() : Map.of();
        List<QuizQuestion> questions = assessment.getQuestions();

        int correctCount = 0;
        List<QuestionReviewItem> reviews = new ArrayList<>();

        for (QuizQuestion q : questions) {
            int selected = answers.getOrDefault(q.getId(), -1);
            boolean isCorrect = (selected == q.getCorrectIndex());
            if (isCorrect) correctCount++;

            reviews.add(new QuestionReviewItem(
                q.getId(),
                q.getQuestion(),
                q.getOptions(),
                selected,
                q.getCorrectIndex(),
                isCorrect,
                q.getExplanation()
            ));
        }

        int total = questions.size();
        int percentage = total > 0 ? (correctCount * 100) / total : 0;
        boolean passed = percentage >= 60;

        String badge = passed ? (percentage >= 80 ? "Gold Certified " + assessment.getSkill() : "Silver Certified " + assessment.getSkill()) : "Participation Badge";
        String feedback = passed
                ? String.format("Congratulations! You passed the %s assessment with a score of %d%% (%d/%d). You have demonstrated verified proficiency in %s!",
                assessment.getTitle(), percentage, correctCount, total, assessment.getSkill())
                : String.format("You scored %d%% (%d/%d). The passing benchmark is 60%%. Review the explanations below and recommended courses before re-attempting.",
                percentage, correctCount, total);

        // Save result in MySQL database
        AssessmentResult resultEntity = new AssessmentResult(
            userId,
            assessment.getId(),
            assessment.getTitle(),
            assessment.getSkill(),
            correctCount,
            total,
            percentage,
            passed
        );
        assessmentResultRepository.save(resultEntity);

        // If passed, record the skill in student's profile
        if (passed) {
            try {
                Profile profile = profileService.getProfileByUserId(userId);
                String skills = profile.getCurrentSkills() != null ? profile.getCurrentSkills() : "";
                if (!skills.toLowerCase().contains(assessment.getSkill().toLowerCase())) {
                    String updatedSkills = skills.trim().isEmpty() ? assessment.getSkill() : skills + ", " + assessment.getSkill();
                    profile.setCurrentSkills(updatedSkills);
                    profileService.updateProfile(userId, profile);
                }
            } catch (Exception e) {
                // Ignore profile update error if profile not yet completed
            }
        }

        return new AssessmentReview(
            assessment.getId(),
            assessment.getTitle(),
            assessment.getSkill(),
            correctCount,
            total,
            percentage,
            passed,
            badge,
            feedback,
            reviews
        );
    }

    public List<AssessmentResult> getUserResults(Long userId) {
        return assessmentResultRepository.findByUserIdOrderByCompletedAtDesc(userId);
    }
}
