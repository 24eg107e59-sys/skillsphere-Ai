package com.skillsphere.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skillsphere.dto.Course;
import com.skillsphere.dto.RoadmapPhase;
import com.skillsphere.dto.RoadmapResponse;
import com.skillsphere.dto.SkillGapResponse;

@Service
public class RoadmapService {

    private final SkillGapService skillGapService;
    private final CourseCatalogService courseCatalogService;

    public RoadmapService(SkillGapService skillGapService, CourseCatalogService courseCatalogService) {
        this.skillGapService = skillGapService;
        this.courseCatalogService = courseCatalogService;
    }

    public RoadmapResponse generateRoadmapForUser(Long userId) {
        SkillGapResponse gap = skillGapService.analyzeGapForUser(userId);
        List<String> missingSkills = gap.getMissingSkills();
        String career = gap.getTargetCareer();

        List<RoadmapPhase> phases = new ArrayList<>();
        int phaseCounter = 1;

        if (missingSkills == null || missingSkills.isEmpty()) {
            // Student already matches 100% of benchmark skills!
            phases.add(new RoadmapPhase(
                1,
                "Advanced System Architecture & Performance",
                "Advanced " + career,
                "2 Weeks",
                "IN_PROGRESS",
                "Deep dive into design patterns, caching (Redis), asynchronous queues, and high-concurrency optimization.",
                List.of("Microservices Patterns", "Distributed Caching", "Database Indexing & Sharding", "Load Balancing"),
                "Build a high-throughput transaction processor capable of handling 5,000 req/sec"
            ));

            phases.add(new RoadmapPhase(
                2,
                "CI/CD Automation & Cloud Deployment",
                "Cloud & DevOps",
                "2 Weeks",
                "NOT_STARTED",
                "Implement end-to-end automated testing, GitHub Actions workflows, container registry publishing, and AWS deployment.",
                List.of("GitHub Actions CI/CD", "Automated JUnit & Integration Tests", "Cloud Container Services", "Monitoring & Alerts"),
                "Deploy production-grade full-stack system with automated smoke tests and zero-downtime rolling updates"
            ));

            phases.add(new RoadmapPhase(
                3,
                "Industry Portfolio & Technical Interview Mastery",
                "Career Readiness",
                "2 Weeks",
                "NOT_STARTED",
                "Refine technical portfolio, prepare for DSA and architectural design interview rounds, and conduct mock assessments.",
                List.of("System Design Primer", "Behavioral Interview STAR Method", "Live Coding Drills", "Open Source Contribution"),
                "Publish open-source capstone repository with production README, architecture diagrams, and live demo link"
            ));
        } else {
            // Generate phases tailored specifically to missing skills
            for (int i = 0; i < missingSkills.size(); i++) {
                String skill = missingSkills.get(i);
                String status = (i == 0) ? "IN_PROGRESS" : "NOT_STARTED";
                phases.add(createPhaseForSkill(phaseCounter++, skill, status));
            }

            // Capstone Integration Phase
            phases.add(new RoadmapPhase(
                phaseCounter,
                "End-to-End Capstone Project & Portfolio Deployment",
                career + " Integration",
                "2 Weeks",
                "NOT_STARTED",
                "Synthesize all acquired skills into a unified, industry-grade portfolio capstone project with live hosting.",
                List.of("End-to-End System Integration", "Authentication & Role Security", "Automated CI/CD Workflow", "Live Cloud Deployment"),
                "Deliver 'SkillSphere Pro' capstone project meeting modern industry recruitment standards"
            ));
        }

        // Fetch curated courses matching missing skills
        List<Course> recommendedCourses = courseCatalogService.getCoursesBySkills(missingSkills);
        if (recommendedCourses.isEmpty()) {
            recommendedCourses = courseCatalogService.getAllCourses().subList(0, Math.min(4, courseCatalogService.getAllCourses().size()));
        }

        int totalWeeks = phases.size() * 2;
        String advice = String.format(
            "This personalized roadmap bridges your %d missing skill(s) for %s over an estimated %d-week structured timeline. Complete Phase 1 to immediately raise your readiness score!",
            missingSkills.size(), career, totalWeeks
        );

        return new RoadmapResponse(
            userId,
            career,
            phases.size(),
            totalWeeks,
            phases,
            recommendedCourses,
            advice
        );
    }

    private RoadmapPhase createPhaseForSkill(int phaseNum, String skill, String status) {
        switch (skill.toLowerCase()) {
            case "spring boot":
                return new RoadmapPhase(
                    phaseNum,
                    "Enterprise Backend Architecture with Spring Boot",
                    "Spring Boot",
                    "2-3 Weeks",
                    status,
                    "Master Spring Boot 3 fundamentals, dependency injection, JPA repository mapping, DTO layers, and centralized exception handling.",
                    List.of("Spring IoC & Auto-configuration", "Spring Data JPA & Hibernate", "REST Controller Layer", "Service Layer Transaction Management"),
                    "Build an E-Commerce Product Catalog REST Service with pagination, search, and MySQL persistence"
                );
            case "react":
                return new RoadmapPhase(
                    phaseNum,
                    "Modern Frontend Engineering with React",
                    "React",
                    "2 Weeks",
                    status,
                    "Develop interactive, component-based user interfaces with React 19, hooks (useState, useEffect, custom hooks), and React Router.",
                    List.of("JSX & Component Lifecycle", "State Management & Side Effects", "Client-side Routing", "Asynchronous API Integration"),
                    "Build a responsive Interactive Learning Dashboard with live charts and real-time state"
                );
            case "rest apis":
                return new RoadmapPhase(
                    phaseNum,
                    "RESTful API Design & HTTP Standards",
                    "REST APIs",
                    "1-2 Weeks",
                    status,
                    "Learn API architectural standards: idempotency, proper status codes, pagination, error schemas, CORS, and Swagger/OpenAPI docs.",
                    List.of("HTTP Methods & Status Codes", "REST URI Naming Conventions", "JSON Request/Response DTOs", "API Testing with Postman"),
                    "Design and document a complete RESTful Booking System API specification with OpenAPI/Swagger"
                );
            case "docker":
                return new RoadmapPhase(
                    phaseNum,
                    "Containerization & Microservices with Docker",
                    "Docker",
                    "1-2 Weeks",
                    status,
                    "Package applications into lightweight, reproducible containers using multi-stage Dockerfiles and Docker Compose.",
                    List.of("Containers vs Virtual Machines", "Multi-stage Dockerfile Optimization", "Docker Compose Multi-Container Orchestration", "Environment Configuration"),
                    "Containerize both Spring Boot backend and React frontend into an isolated Docker network"
                );
            case "machine learning":
                return new RoadmapPhase(
                    phaseNum,
                    "Applied Machine Learning & Predictive Modeling",
                    "Machine Learning",
                    "3 Weeks",
                    status,
                    "Understand core ML algorithms: regression, decision trees, random forests, clustering, and cross-validation techniques.",
                    List.of("Feature Engineering & Scaling", "Scikit-Learn Model Pipelines", "Cross-Validation & Metrics", "Model Persistence with Joblib"),
                    "Train and evaluate a student career recommendation classifier with 85%+ accuracy"
                );
            case "deep learning":
                return new RoadmapPhase(
                    phaseNum,
                    "Neural Networks & Deep Learning Architectures",
                    "Deep Learning",
                    "3 Weeks",
                    status,
                    "Construct neural networks with PyTorch: forward passes, loss functions, backpropagation, CNNs, and sequence models.",
                    List.of("Tensors & Gradient Descent", "Multi-Layer Perceptrons (MLP)", "Convolutional Neural Networks", "Transfer Learning"),
                    "Build an image recognition or natural language sentiment classifier using PyTorch"
                );
            case "kubernetes":
                return new RoadmapPhase(
                    phaseNum,
                    "Container Orchestration with Kubernetes",
                    "Kubernetes",
                    "2 Weeks",
                    status,
                    "Deploy, scale, and manage containerized applications across clustered environments with declarative manifests.",
                    List.of("Pods, ReplicaSets & Deployments", "Cluster Services & Ingress", "ConfigMaps & Secrets", "Horizontal Pod Autoscaling"),
                    "Deploy an auto-scaling microservices cluster with ingress routing and health checks"
                );
            case "aws":
                return new RoadmapPhase(
                    phaseNum,
                    "Cloud Infrastructure & Deployment on AWS",
                    "AWS",
                    "2 Weeks",
                    status,
                    "Utilize Amazon Web Services compute, storage, databases, and networking to deploy resilient web applications.",
                    List.of("EC2 Virtual Servers", "S3 Storage & CloudFront", "RDS Managed Relational Databases", "IAM Security Roles"),
                    "Deploy a high-availability cloud architecture with RDS backend and S3 static frontend"
                );
            default:
                return new RoadmapPhase(
                    phaseNum,
                    "Competency Mastery: " + skill,
                    skill,
                    "2 Weeks",
                    status,
                    "Focus dedicated study on foundational principles, hands-on tutorials, and mini-exercises for " + skill + ".",
                    List.of("Core Principles & Syntax", "Standard Libraries & Tooling", "Real-world Best Practices", "Testing & Debugging"),
                    "Develop a portfolio mini-project demonstrating practical mastery of " + skill
                );
        }
    }
}
