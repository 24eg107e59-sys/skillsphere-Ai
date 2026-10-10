package com.skillsphere.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.skillsphere.entity.AssessmentEntity;
import com.skillsphere.entity.CareerPath;
import com.skillsphere.entity.CourseEntity;
import com.skillsphere.entity.QuizQuestionEntity;
import com.skillsphere.entity.Skill;
import com.skillsphere.entity.User;
import com.skillsphere.repository.AssessmentRepository;
import com.skillsphere.repository.CareerPathRepository;
import com.skillsphere.repository.CourseRepository;
import com.skillsphere.repository.SkillRepository;
import com.skillsphere.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final SkillRepository skillRepository;
    private final CareerPathRepository careerPathRepository;
    private final CourseRepository courseRepository;
    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${skillsphere.admin.email:admin@skillsphere.com}")
    private String adminEmail;

    @Value("${skillsphere.admin.password:}")
    private String adminPassword;

    public DataInitializer(SkillRepository skillRepository,
                           CareerPathRepository careerPathRepository,
                           CourseRepository courseRepository,
                           AssessmentRepository assessmentRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.skillRepository = skillRepository;
        this.careerPathRepository = careerPathRepository;
        this.courseRepository = courseRepository;
        this.assessmentRepository = assessmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedSkillsAndCareers();
        seedCourses();
        seedAssessments();
    }

    private void seedAdminUser() {
        if (adminPassword == null || adminPassword.isBlank()) {
            logger.info("Admin account seeding skipped; set SKILLSPHERE_ADMIN_PASSWORD to provision an administrator.");
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        if (!userRepository.existsByEmail(normalizedEmail)) {
            User admin = new User(
                "SkillSphere Administrator",
                normalizedEmail,
                passwordEncoder.encode(adminPassword),
                "ADMIN"
            );
            userRepository.save(admin);
            logger.info("Seeded administrator account: {}", normalizedEmail);
        }
    }

    private void seedSkillsAndCareers() {
        if (skillRepository.count() > 0 && careerPathRepository.count() > 0) {
            return;
        }

        logger.info("Seeding standardized Skills and Career Paths into MySQL...");

        // Define Skill library
        Map<String, String> skillCat = new HashMap<>();
        skillCat.put("Java", "Programming Language");
        skillCat.put("Spring Boot", "Backend Framework");
        skillCat.put("React", "Frontend Library");
        skillCat.put("MySQL", "Relational Database");
        skillCat.put("REST APIs", "Web Architecture");
        skillCat.put("Git", "Version Control");
        skillCat.put("Docker", "Containerization");
        skillCat.put("Python", "Programming Language");
        skillCat.put("Machine Learning", "AI / ML");
        skillCat.put("Deep Learning", "AI / ML");
        skillCat.put("SQL", "Database Querying");
        skillCat.put("Pandas", "Data Processing");
        skillCat.put("Scikit-Learn", "Machine Learning Library");
        skillCat.put("HTML", "Web Core");
        skillCat.put("CSS", "Web Styling");
        skillCat.put("JavaScript", "Programming Language");
        skillCat.put("TypeScript", "Programming Language");
        skillCat.put("Tailwind CSS", "CSS Framework");
        skillCat.put("Linux", "Operating System");
        skillCat.put("Kubernetes", "Container Orchestration");
        skillCat.put("AWS", "Cloud Computing");
        skillCat.put("CI/CD", "DevOps");
        skillCat.put("Terraform", "Infrastructure as Code");
        skillCat.put("Statistics", "Mathematics & Analytics");
        skillCat.put("Data Analysis", "Analytics");
        skillCat.put("PowerBI", "Data Visualization");
        skillCat.put("Network Security", "Cybersecurity");
        skillCat.put("Ethical Hacking", "Cybersecurity");
        skillCat.put("Cryptography", "Cybersecurity");
        skillCat.put("SIEM", "Security Monitoring");

        Map<String, Skill> savedSkills = new HashMap<>();
        for (Map.Entry<String, String> entry : skillCat.entrySet()) {
            Skill s = skillRepository.findByNameIgnoreCase(entry.getKey())
                .orElseGet(() -> skillRepository.save(new Skill(entry.getKey(), entry.getValue(), "Industry benchmark skill: " + entry.getKey())));
            savedSkills.put(entry.getKey(), s);
        }

        // Define Careers
        createCareerIfNotExists(
            "fullstack-java",
            "Full Stack Java Developer",
            "Build robust enterprise web applications using Spring Boot, React, and relational databases.",
            "💻",
            List.of("Java", "Spring Boot", "React", "MySQL", "REST APIs", "Git", "Docker"),
            savedSkills
        );

        createCareerIfNotExists(
            "ai-ml-engineer",
            "AI / Machine Learning Engineer",
            "Develop intelligent algorithms, predictive models, and deploy neural network architectures.",
            "🤖",
            List.of("Python", "Machine Learning", "Deep Learning", "SQL", "Pandas", "Scikit-Learn", "Git"),
            savedSkills
        );

        createCareerIfNotExists(
            "frontend-developer",
            "Frontend Web Developer",
            "Design responsive, accessible, and high-performance interactive user interfaces.",
            "🎨",
            List.of("HTML", "CSS", "JavaScript", "React", "TypeScript", "Tailwind CSS", "Git"),
            savedSkills
        );

        createCareerIfNotExists(
            "cloud-devops",
            "Cloud & DevOps Engineer",
            "Automate deployment pipelines, cloud infrastructure, containerization, and monitoring.",
            "☁️",
            List.of("Linux", "Docker", "Kubernetes", "AWS", "CI/CD", "Git", "Terraform"),
            savedSkills
        );

        createCareerIfNotExists(
            "data-scientist",
            "Data Scientist",
            "Extract insights from complex data through statistical modeling and machine learning.",
            "📊",
            List.of("Python", "SQL", "Statistics", "Data Analysis", "Machine Learning", "PowerBI"),
            savedSkills
        );

        createCareerIfNotExists(
            "cybersecurity-analyst",
            "Cybersecurity Analyst",
            "Protect networks, assess vulnerabilities, and safeguard sensitive digital systems.",
            "🔒",
            List.of("Network Security", "Linux", "Ethical Hacking", "Cryptography", "SIEM", "Python"),
            savedSkills
        );

        logger.info("Seeded Career Paths successfully!");
    }

    private void createCareerIfNotExists(String code, String title, String description, String icon, List<String> skillNames, Map<String, Skill> savedSkills) {
        if (!careerPathRepository.existsByCode(code)) {
            CareerPath cp = new CareerPath(code, title, description, icon);
            for (String sName : skillNames) {
                Skill s = savedSkills.get(sName);
                if (s != null) {
                    cp.addSkill(s);
                }
            }
            careerPathRepository.save(cp);
        }
    }

    private void seedCourses() {
        if (courseRepository.count() > 0) return;

        logger.info("Seeding Course catalog into MySQL...");

        List<CourseEntity> courses = List.of(
            new CourseEntity("crs-spring-boot", "Spring Boot 3 & Enterprise Architecture", "Spring Boot", "Intermediate", "18 Hours", 4.9, "Master backend web architecture, dependency injection, JPA/Hibernate, and production-ready microservices.", "SkillSphere Academy", "💻", List.of("Spring IoC & Beans", "Spring Data JPA & Hibernate", "Spring Security 6", "Exception Handling & Logging")),
            new CourseEntity("crs-react-modern", "Modern React 19: The Complete Guide", "React", "Intermediate", "16 Hours", 4.8, "Build dynamic single-page applications with Hooks, Component state, Router 7, and clean responsive CSS.", "SkillSphere Academy", "⚛️", List.of("Components & Props", "React State & Effects", "React Router Navigation", "REST API Data Fetching")),
            new CourseEntity("crs-rest-apis", "RESTful API Design & Best Practices", "REST APIs", "Beginner", "8 Hours", 4.7, "Design clean, scalable HTTP interfaces with proper status codes, pagination, filtering, and Swagger documentation.", "SkillSphere Academy", "🔌", List.of("HTTP Verbs & Status Codes", "JSON Resource Modeling", "API Versioning", "Testing with Postman & Curl")),
            new CourseEntity("crs-docker-devs", "Docker & Containerization for Developers", "Docker", "Intermediate", "10 Hours", 4.8, "Containerize Java, Node, and Python apps with Dockerfiles, manage multi-container apps with Docker Compose.", "SkillSphere Academy", "🐳", List.of("Images & Containers", "Dockerfile Best Practices", "Docker Compose Multi-Service", "Volume Persistence & Networking")),
            new CourseEntity("crs-java-mastery", "Core & Advanced Java 21 Masterclass", "Java", "Beginner", "22 Hours", 4.9, "Object-oriented programming, Collections framework, Streams, Concurrency, and modern Java features.", "SkillSphere Academy", "☕", List.of("OOP Fundamentals", "Collections & Generics", "Lambdas & Streams", "Multithreading & Exceptions")),
            new CourseEntity("crs-mysql-db", "Relational Database Design & MySQL", "MySQL", "Beginner", "12 Hours", 4.7, "Master relational schemas, normalization, complex SQL joins, indexing, and transactional integrity.", "SkillSphere Academy", "🗄️", List.of("Relational Modeling", "DDL & DML Queries", "Joins & Subqueries", "Indexes & Query Optimization")),
            new CourseEntity("crs-git-github", "Git & GitHub for Team Collaboration", "Git", "Beginner", "6 Hours", 4.8, "Version control essentials: branches, merge conflict resolution, pull requests, and CI/CD triggers.", "SkillSphere Academy", "🐙", List.of("Version Control Basics", "Branching Strategies", "Rebase vs Merge", "Collaborative PR Workflows")),
            new CourseEntity("crs-python-ml", "Python for Data Science & Machine Learning", "Python", "Beginner", "20 Hours", 4.9, "Master NumPy, Pandas, Matplotlib, and scikit-learn for end-to-end data processing and model training.", "SkillSphere Academy", "🐍", List.of("Python Core Syntax", "NumPy & Vectorized Math", "Pandas DataFrames", "Exploratory Data Analysis")),
            new CourseEntity("crs-ml-algorithms", "Machine Learning Algorithms & Predictive Modeling", "Machine Learning", "Intermediate", "18 Hours", 4.8, "Supervised & unsupervised learning: Regression, Classification, Random Forests, SVMs, and Model Evaluation.", "SkillSphere Academy", "🤖", List.of("Supervised Algorithms", "Decision Trees & Ensembles", "Clustering & K-Means", "Hyperparameter Tuning")),
            new CourseEntity("crs-deep-learning", "Deep Learning & Neural Networks with PyTorch", "Deep Learning", "Advanced", "24 Hours", 4.8, "Build Convolutional Networks (CNNs), Recurrent Networks (RNNs), and Transformers with PyTorch.", "SkillSphere Academy", "🧠", List.of("Tensors & Autograd", "Feedforward Networks", "CNNs for Computer Vision", "Transformers Overview")),
            new CourseEntity("crs-aws-cloud", "AWS Cloud Architecture Essentials", "AWS", "Intermediate", "14 Hours", 4.7, "Deploy highly available applications on AWS using EC2, S3, RDS, Lambda, and IAM security policies.", "SkillSphere Academy", "☁️", List.of("Compute & EC2", "S3 Storage & IAM", "Relational RDS Databases", "Serverless Lambda")),
            new CourseEntity("crs-kubernetes", "Kubernetes Orchestration & Helm Charts", "Kubernetes", "Advanced", "16 Hours", 4.8, "Orchestrate container clusters, deployments, services, ingress controllers, and auto-scaling.", "SkillSphere Academy", "☸️", List.of("Pods & Deployments", "Cluster Services & Ingress", "ConfigMaps & Secrets", "Helm Packaging")),
            new CourseEntity("crs-linux-sys", "Linux System Administration & Shell Scripting", "Linux", "Beginner", "10 Hours", 4.8, "Command line mastery, file permissions, process management, Bash scripting, and SSH security.", "SkillSphere Academy", "🐧", List.of("File System Navigation", "Permissions & Users", "Bash Automation", "Networking Tools")),
            new CourseEntity("crs-cybersecurity", "Network Security & Ethical Hacking Defense", "Network Security", "Intermediate", "16 Hours", 4.7, "Understand vulnerabilities, packet analysis with Wireshark, penetration testing methodology, and defense.", "SkillSphere Academy", "🛡️", List.of("Network Protocols", "Threat Modeling", "Vulnerability Assessment", "Incident Response Basics"))
        );

        courseRepository.saveAll(courses);
        logger.info("Seeded 14 Courses into MySQL!");
    }

    private void seedAssessments() {
        if (assessmentRepository.count() > 0) return;

        logger.info("Seeding Assessments into MySQL...");

        AssessmentEntity springAsm = new AssessmentEntity("asm-spring-boot", "Spring Boot & REST Architecture Assessment", "Spring Boot", "Intermediate", 10, "💻");
        springAsm.addQuestion(new QuizQuestionEntity("Which annotation is used to map HTTP GET requests onto specific handler methods in Spring Boot?", List.of("@PostMapping", "@GetMapping", "@PutMapping", "@DeleteMapping"), 1, "@GetMapping is a composed annotation that acts as a shortcut for @RequestMapping(method = RequestMethod.GET)."));
        springAsm.addQuestion(new QuizQuestionEntity("In Spring Data JPA, what interface provides basic CRUD operations as well as pagination and sorting?", List.of("CrudRepository", "JpaRepository", "QuerydslRepository", "JpaSpecificationExecutor"), 1, "JpaRepository extends PagingAndSortingRepository and CrudRepository, providing full JPA operations."));
        springAsm.addQuestion(new QuizQuestionEntity("What is the default embedded servlet container used by Spring Boot starter web?", List.of("Jetty", "Undertow", "Apache Tomcat", "GlassFish"), 2, "Apache Tomcat is the default embedded servlet container configured in spring-boot-starter-web."));
        springAsm.addQuestion(new QuizQuestionEntity("Which annotation enables Spring Boot's auto-configuration, component scanning, and extra configuration?", List.of("@SpringBootApplication", "@EnableAutoConfiguration", "@ComponentScan", "@Configuration"), 0, "@SpringBootApplication bundles @Configuration, @EnableAutoConfiguration, and @ComponentScan together."));
        springAsm.addQuestion(new QuizQuestionEntity("Where are default application properties typically configured in a Spring Boot Maven project?", List.of("src/main/resources/application.properties", "src/main/java/config.properties", "pom.xml", "META-INF/spring.factories"), 0, "application.properties or application.yml inside src/main/resources is the standard Spring Boot configuration location."));
        assessmentRepository.save(springAsm);

        AssessmentEntity reactAsm = new AssessmentEntity("asm-react", "Modern React & Frontend Engineering Assessment", "React", "Intermediate", 10, "⚛️");
        reactAsm.addQuestion(new QuizQuestionEntity("Which React hook is used to manage mutable local state inside a functional component?", List.of("useEffect", "useState", "useContext", "useRef"), 1, "useState declares a state variable that persists between re-renders."));
        reactAsm.addQuestion(new QuizQuestionEntity("When does the cleanup function of a useEffect hook execute?", List.of("Only on initial mount", "After every child render", "Right before the component unmounts and before re-running the effect", "Never in production"), 2, "The cleanup function runs right before the component unmounts and before the effect runs again when dependencies change."));
        reactAsm.addQuestion(new QuizQuestionEntity("What is the virtual DOM in React?", List.of("A copy of the HTML file on disk", "A lightweight JavaScript representation of the actual DOM in memory", "A browser extension for debugging", "A native Web Component"), 1, "The Virtual DOM is a lightweight JavaScript representation of the actual DOM used for high-performance reconciliation."));
        reactAsm.addQuestion(new QuizQuestionEntity("Why must keys be provided when rendering a dynamic list in React?", List.of("To enforce alphabetical sorting", "To apply CSS stylesheets", "To help React identify which items have changed, added, or removed", "Keys are optional and not recommended"), 2, "Keys help React identify which items have changed, added, or removed, enabling fast DOM updates."));
        reactAsm.addQuestion(new QuizQuestionEntity("What library is commonly used for client-side routing in single-page React applications?", List.of("React Redux", "React Router", "React Query", "Axios"), 1, "React Router provides declarative routing for React web applications."));
        assessmentRepository.save(reactAsm);

        AssessmentEntity dockerAsm = new AssessmentEntity("asm-docker", "Docker Containerization & DevOps Assessment", "Docker", "Intermediate", 10, "🐳");
        dockerAsm.addQuestion(new QuizQuestionEntity("What command builds a Docker image from a Dockerfile in the current directory?", List.of("docker run .", "docker build -t <tag> .", "docker create image .", "docker init"), 1, "'docker build -t <tag> .' builds a new image from the Dockerfile in the specified directory."));
        dockerAsm.addQuestion(new QuizQuestionEntity("What is the primary difference between a Docker image and a Docker container?", List.of("They are identical concepts", "An image is a static template; a container is a runnable running instance of an image", "Containers create images at build time", "Images run on Linux only, containers on Windows only"), 1, "An image is a static, read-only template with instructions, while a container is a runnable instance of an image."));
        dockerAsm.addQuestion(new QuizQuestionEntity("Which Docker tool allows you to define and run multi-container applications using a YAML file?", List.of("Docker Hub", "Docker Compose", "Docker Swarm", "Docker Registry"), 1, "Docker Compose defines multi-container microservices architectures via docker-compose.yml."));
        dockerAsm.addQuestion(new QuizQuestionEntity("Which Dockerfile instruction sets the default command that runs when a container launches?", List.of("RUN", "ENV", "CMD", "WORKDIR"), 2, "CMD specifies the default command executed when the container starts."));
        dockerAsm.addQuestion(new QuizQuestionEntity("Which command lists all currently running and stopped Docker containers?", List.of("docker ps -a", "docker images", "docker list", "docker inspect"), 0, "'docker ps -a' lists all containers regardless of whether they are active or stopped."));
        assessmentRepository.save(dockerAsm);

        AssessmentEntity sqlAsm = new AssessmentEntity("asm-sql", "Relational Database Design & MySQL Assessment", "MySQL", "Beginner", 10, "🗄️");
        sqlAsm.addQuestion(new QuizQuestionEntity("Which SQL clause is used to filter query results based on aggregated values (e.g. COUNT, AVG)?", List.of("WHERE", "HAVING", "GROUP BY", "ORDER BY"), 1, "HAVING filters records after aggregation, whereas WHERE filters individual rows before grouping."));
        sqlAsm.addQuestion(new QuizQuestionEntity("What type of JOIN returns all records from both tables when there is a match in either?", List.of("INNER JOIN", "LEFT JOIN", "FULL OUTER JOIN", "CROSS JOIN"), 2, "A FULL OUTER JOIN returns all matching records from both tables and fills non-matches with NULL."));
        sqlAsm.addQuestion(new QuizQuestionEntity("What database object speeds up retrieval of rows by creating sorted pointer lookups?", List.of("Trigger", "Index", "View", "Stored Procedure"), 1, "An Index provides fast random access to rows based on indexed column values, reducing full table scans."));
        sqlAsm.addQuestion(new QuizQuestionEntity("In relational database design, what does Third Normal Form (3NF) eliminate?", List.of("Primary keys", "Transitive functional dependencies", "Foreign keys", "Duplicate table names"), 1, "3NF requires the schema to be in 2NF and have no transitive functional dependencies."));
        sqlAsm.addQuestion(new QuizQuestionEntity("Which SQL constraint ensures that all values in a column are distinct and not null?", List.of("UNIQUE", "FOREIGN KEY", "PRIMARY KEY", "CHECK"), 2, "PRIMARY KEY uniquely identifies each record and does not accept NULL values."));
        assessmentRepository.save(sqlAsm);

        logger.info("Seeded Assessments successfully!");
    }
}
