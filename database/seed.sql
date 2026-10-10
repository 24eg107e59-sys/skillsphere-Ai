-- Re-runnable catalog data for SkillSphere AI. No users or passwords are seeded.
USE skillsphere;

INSERT IGNORE INTO skills (name, category, description) VALUES
    ('Java', 'Programming Language', 'Object-oriented programming and the Java platform.'),
    ('Spring Boot', 'Backend Framework', 'Java framework for REST services and backend applications.'),
    ('React', 'Frontend Library', 'Component-based JavaScript user interface library.'),
    ('MySQL', 'Relational Database', 'Relational database design, SQL, indexes, and transactions.'),
    ('REST APIs', 'Web Architecture', 'HTTP resource design and REST API implementation.'),
    ('Git', 'Version Control', 'Source control, branching, and collaborative workflows.'),
    ('Docker', 'Containerization', 'Container images, containers, and Compose.'),
    ('Python', 'Programming Language', 'General-purpose programming used in analytics and machine learning.'),
    ('Machine Learning', 'AI / ML', 'Supervised and unsupervised model development.'),
    ('Deep Learning', 'AI / ML', 'Neural networks and deep learning architectures.'),
    ('SQL', 'Database Querying', 'Relational data querying and analysis.'),
    ('Pandas', 'Data Processing', 'Tabular data processing and analysis in Python.'),
    ('Scikit-Learn', 'Machine Learning Library', 'Classical machine learning tools for Python.'),
    ('HTML', 'Web Core', 'Semantic markup for web documents.'),
    ('CSS', 'Web Styling', 'Responsive styling and visual presentation for web applications.'),
    ('JavaScript', 'Programming Language', 'Programming language for web browsers and servers.'),
    ('TypeScript', 'Programming Language', 'Statically typed JavaScript development.'),
    ('Tailwind CSS', 'CSS Framework', 'Utility-first CSS framework for responsive interfaces.'),
    ('Linux', 'Operating System', 'Linux command line, processes, files, and permissions.'),
    ('Kubernetes', 'Container Orchestration', 'Container orchestration, workloads, and services.'),
    ('AWS', 'Cloud Computing', 'Cloud architecture and foundational AWS services.'),
    ('CI/CD', 'DevOps', 'Continuous integration and delivery pipelines.'),
    ('Terraform', 'Infrastructure as Code', 'Declarative provisioning of cloud infrastructure.'),
    ('Statistics', 'Mathematics & Analytics', 'Descriptive statistics and inference for data analysis.'),
    ('Data Analysis', 'Analytics', 'Exploration, interpretation, and communication of data.'),
    ('PowerBI', 'Data Visualization', 'Business intelligence dashboards and data visualization.'),
    ('Network Security', 'Cybersecurity', 'Network defenses, threats, and security fundamentals.'),
    ('Ethical Hacking', 'Cybersecurity', 'Authorized security testing and vulnerability assessment.'),
    ('Cryptography', 'Cybersecurity', 'Cryptographic concepts and secure communication.'),
    ('SIEM', 'Security Monitoring', 'Security information and event monitoring fundamentals.');
INSERT IGNORE INTO careers (code, title, description, category, experience_level) VALUES
    ('java-developer', 'Java Developer', 'Build and maintain Java applications, services, and APIs.', 'Software Development', 'Entry'),
    ('full-stack-developer', 'Full Stack Developer', 'Build web applications across frontend, backend, and relational data layers.', 'Software Development', 'Entry'),
    ('data-analyst', 'Data Analyst', 'Query, analyze, and communicate insights from structured data.', 'Data & Analytics', 'Entry'),
    ('ai-ml-engineer', 'AI/ML Engineer', 'Prepare data, train machine-learning models, and integrate them into applications.', 'Artificial Intelligence', 'Entry');
-- Compatibility catalog consumed by CareerPath.java and its JPA repository.
INSERT IGNORE INTO career_paths (code, title, description, icon) VALUES
    ('java-developer', 'Java Developer', 'Build and maintain Java applications, services, and APIs.', NULL),
    ('fullstack-java', 'Full Stack Java Developer', 'Build enterprise web applications with Java, Spring Boot, React, and MySQL.', NULL),
    ('data-analyst', 'Data Analyst', 'Query, analyze, and communicate insights from structured data.', NULL),
    ('ai-ml-engineer', 'AI / Machine Learning Engineer', 'Develop and deploy predictive models and machine-learning systems.', NULL),
    ('frontend-developer', 'Frontend Web Developer', 'Design accessible, responsive, and high-performance user interfaces.', NULL),
    ('cloud-devops', 'Cloud & DevOps Engineer', 'Automate cloud infrastructure, containers, and delivery pipelines.', NULL),
    ('data-scientist', 'Data Scientist', 'Use statistics and machine learning to extract insights from data.', NULL),
    ('cybersecurity-analyst', 'Cybersecurity Analyst', 'Assess vulnerabilities and help protect digital systems.', NULL);
-- Career benchmark data. Levels use a 1-5 scale; larger weights matter more.
INSERT INTO career_skills
    (career_path_id, career_id, skill_id, required_level, importance_weight)
SELECT cp.id, c.id, s.id, v.required_level, v.importance_weight
FROM (
    SELECT 'java-developer' AS career_code, 'java-developer' AS path_code, 'Java' AS skill_name, 4 AS required_level, 1.50 AS importance_weight
    UNION ALL SELECT 'java-developer', 'java-developer', 'Spring Boot', 3, 1.30
    UNION ALL SELECT 'java-developer', 'java-developer', 'REST APIs', 3, 1.10
    UNION ALL SELECT 'java-developer', 'java-developer', 'SQL', 3, 1.00
    UNION ALL SELECT 'java-developer', 'java-developer', 'Git', 3, 0.80
    UNION ALL SELECT 'full-stack-developer', 'fullstack-java', 'Java', 3, 1.20
    UNION ALL SELECT 'full-stack-developer', 'fullstack-java', 'Spring Boot', 4, 1.40
    UNION ALL SELECT 'full-stack-developer', 'fullstack-java', 'React', 3, 1.20
    UNION ALL SELECT 'full-stack-developer', 'fullstack-java', 'MySQL', 3, 1.00
    UNION ALL SELECT 'full-stack-developer', 'fullstack-java', 'REST APIs', 4, 1.30
    UNION ALL SELECT 'full-stack-developer', 'fullstack-java', 'Git', 3, 0.80
    UNION ALL SELECT 'full-stack-developer', 'fullstack-java', 'Docker', 2, 0.70
    UNION ALL SELECT 'data-analyst', 'data-analyst', 'SQL', 4, 1.50
    UNION ALL SELECT 'data-analyst', 'data-analyst', 'Python', 3, 1.10
    UNION ALL SELECT 'data-analyst', 'data-analyst', 'Pandas', 3, 1.10
    UNION ALL SELECT 'data-analyst', 'data-analyst', 'Statistics', 3, 1.20
    UNION ALL SELECT 'data-analyst', 'data-analyst', 'Data Analysis', 4, 1.40
    UNION ALL SELECT 'data-analyst', 'data-analyst', 'PowerBI', 3, 1.00
    UNION ALL SELECT 'ai-ml-engineer', 'ai-ml-engineer', 'Python', 4, 1.30
    UNION ALL SELECT 'ai-ml-engineer', 'ai-ml-engineer', 'Machine Learning', 4, 1.50
    UNION ALL SELECT 'ai-ml-engineer', 'ai-ml-engineer', 'Deep Learning', 3, 1.20
    UNION ALL SELECT 'ai-ml-engineer', 'ai-ml-engineer', 'Pandas', 3, 0.90
    UNION ALL SELECT 'ai-ml-engineer', 'ai-ml-engineer', 'Scikit-Learn', 4, 1.30
    UNION ALL SELECT 'ai-ml-engineer', 'ai-ml-engineer', 'SQL', 2, 0.70
    UNION ALL SELECT 'ai-ml-engineer', 'ai-ml-engineer', 'Git', 2, 0.60
) AS v
JOIN careers c ON c.code = v.career_code
JOIN career_paths cp ON cp.code = v.path_code
JOIN skills s ON s.name = v.skill_name
WHERE NOT EXISTS (
    SELECT 1 FROM career_skills existing
    WHERE existing.career_id = c.id AND existing.skill_id = s.id
)
ON DUPLICATE KEY UPDATE
    career_id = COALESCE(career_skills.career_id, VALUES(career_id));

-- Keep the additional legacy paths populated for the existing UI/API.
INSERT INTO career_skills
    (career_path_id, skill_id, required_level, importance_weight)
SELECT cp.id, s.id, v.required_level, v.importance_weight
FROM (
    SELECT 'frontend-developer' AS path_code, 'HTML' AS skill_name, 3 AS required_level, 1.00 AS importance_weight
    UNION ALL SELECT 'frontend-developer', 'CSS', 3, 1.00
    UNION ALL SELECT 'frontend-developer', 'JavaScript', 4, 1.40
    UNION ALL SELECT 'frontend-developer', 'React', 4, 1.40
    UNION ALL SELECT 'frontend-developer', 'TypeScript', 3, 1.00
    UNION ALL SELECT 'frontend-developer', 'Tailwind CSS', 2, 0.70
    UNION ALL SELECT 'frontend-developer', 'Git', 2, 0.60
    UNION ALL SELECT 'cloud-devops', 'Linux', 3, 1.00
    UNION ALL SELECT 'cloud-devops', 'Docker', 4, 1.30
    UNION ALL SELECT 'cloud-devops', 'Kubernetes', 3, 1.20
    UNION ALL SELECT 'cloud-devops', 'AWS', 3, 1.20
    UNION ALL SELECT 'cloud-devops', 'CI/CD', 3, 1.00
    UNION ALL SELECT 'cloud-devops', 'Git', 2, 0.60
    UNION ALL SELECT 'cloud-devops', 'Terraform', 2, 0.80
    UNION ALL SELECT 'data-scientist', 'Python', 4, 1.30
    UNION ALL SELECT 'data-scientist', 'SQL', 3, 1.00
    UNION ALL SELECT 'data-scientist', 'Statistics', 4, 1.40
    UNION ALL SELECT 'data-scientist', 'Data Analysis', 3, 1.10
    UNION ALL SELECT 'data-scientist', 'Machine Learning', 4, 1.40
    UNION ALL SELECT 'data-scientist', 'PowerBI', 2, 0.70
    UNION ALL SELECT 'cybersecurity-analyst', 'Network Security', 4, 1.40
    UNION ALL SELECT 'cybersecurity-analyst', 'Linux', 3, 1.00
    UNION ALL SELECT 'cybersecurity-analyst', 'Ethical Hacking', 3, 1.20
    UNION ALL SELECT 'cybersecurity-analyst', 'Cryptography', 3, 1.00
    UNION ALL SELECT 'cybersecurity-analyst', 'SIEM', 3, 1.20
    UNION ALL SELECT 'cybersecurity-analyst', 'Python', 2, 0.60
) AS v
JOIN career_paths cp ON cp.code = v.path_code
JOIN skills s ON s.name = v.skill_name
WHERE NOT EXISTS (
    SELECT 1 FROM career_skills existing
    WHERE existing.career_path_id = cp.id AND existing.skill_id = s.id
);

INSERT IGNORE INTO courses
    (code, title, description, provider, url, difficulty_level, duration_hours, skill_id, status,
     skill, level, duration, rating, icon)
SELECT v.code, v.title, v.description, v.provider, v.url, v.difficulty_level, v.duration_hours, s.id, 'ACTIVE',
       s.name, v.difficulty_level, CONCAT(v.duration_hours, ' Hours'), v.rating, NULL
FROM (
    SELECT 'crs-spring-boot' AS code, 'Spring Boot 3 & Enterprise Architecture' AS title, 'Learn Spring Boot, dependency injection, data access, security, and production-ready APIs.' AS description, 'SkillSphere Academy' AS provider, 'https://docs.spring.io/spring-boot/reference/' AS url, 'Intermediate' AS difficulty_level, 18 AS duration_hours, 4.9 AS rating, 'Spring Boot' AS skill_name
    UNION ALL SELECT 'crs-react-modern', 'Modern React: The Complete Guide', 'Build single-page interfaces with React components, hooks, routing, and API integration.', 'SkillSphere Academy', 'https://react.dev/learn', 'Intermediate', 16, 4.8, 'React'
    UNION ALL SELECT 'crs-rest-apis', 'RESTful API Design & Best Practices', 'Design HTTP resources, status codes, pagination, and versioned APIs.', 'SkillSphere Academy', 'https://developer.mozilla.org/en-US/docs/Web/HTTP', 'Beginner', 8, 4.7, 'REST APIs'
    UNION ALL SELECT 'crs-docker-devs', 'Docker & Containerization for Developers', 'Build container images and run multi-service applications with Docker.', 'SkillSphere Academy', 'https://docs.docker.com/get-started/', 'Intermediate', 10, 4.8, 'Docker'
    UNION ALL SELECT 'crs-java-mastery', 'Core & Advanced Java Masterclass', 'Practice object-oriented Java, collections, streams, and concurrency.', 'SkillSphere Academy', 'https://dev.java/learn/', 'Beginner', 22, 4.9, 'Java'
    UNION ALL SELECT 'crs-mysql-db', 'Relational Database Design & MySQL', 'Study relational modeling, SQL, joins, indexes, and transactions.', 'SkillSphere Academy', 'https://dev.mysql.com/doc/', 'Beginner', 12, 4.7, 'MySQL'
    UNION ALL SELECT 'crs-git-github', 'Git & GitHub for Team Collaboration', 'Learn version control, branches, merge conflict resolution, and pull requests.', 'SkillSphere Academy', 'https://git-scm.com/book/en/v2', 'Beginner', 6, 4.8, 'Git'
    UNION ALL SELECT 'crs-python-ml', 'Python for Data Science & Machine Learning', 'Use Python and Pandas for data processing and introductory model training.', 'SkillSphere Academy', 'https://docs.python.org/3/tutorial/', 'Beginner', 20, 4.9, 'Python'
    UNION ALL SELECT 'crs-ml-algorithms', 'Machine Learning Algorithms & Predictive Modeling', 'Explore supervised learning, classification, regression, and model evaluation.', 'SkillSphere Academy', 'https://scikit-learn.org/stable/user_guide.html', 'Intermediate', 18, 4.8, 'Machine Learning'
    UNION ALL SELECT 'crs-deep-learning', 'Deep Learning & Neural Networks', 'Build neural networks and learn core deep-learning workflows.', 'SkillSphere Academy', 'https://pytorch.org/tutorials/', 'Advanced', 24, 4.8, 'Deep Learning'
    UNION ALL SELECT 'crs-aws-cloud', 'AWS Cloud Architecture Essentials', 'Explore compute, storage, identity, and managed database services.', 'SkillSphere Academy', 'https://docs.aws.amazon.com/whitepapers/latest/aws-overview/aws-overview.pdf', 'Intermediate', 14, 4.7, 'AWS'
    UNION ALL SELECT 'crs-kubernetes', 'Kubernetes Orchestration Fundamentals', 'Deploy workloads and configure services, ingress, and scaling.', 'SkillSphere Academy', 'https://kubernetes.io/docs/tutorials/', 'Advanced', 16, 4.8, 'Kubernetes'
    UNION ALL SELECT 'crs-linux-sys', 'Linux System Administration & Shell Scripting', 'Practice command-line navigation, permissions, processes, and shell scripts.', 'SkillSphere Academy', 'https://ubuntu.com/tutorials/command-line-for-beginners', 'Beginner', 10, 4.8, 'Linux'
    UNION ALL SELECT 'crs-cybersecurity', 'Network Security & Ethical Hacking Defense', 'Understand security fundamentals, threat modeling, and defensive practices.', 'SkillSphere Academy', 'https://owasp.org/www-project-top-ten/', 'Intermediate', 16, 4.7, 'Network Security'
) AS v
JOIN skills s ON s.name = v.skill_name;

INSERT INTO course_modules (course_id, module_name)
SELECT c.id, v.module_name
FROM (
    SELECT 'crs-spring-boot' AS course_code, 'Spring IoC and REST controllers' AS module_name
    UNION ALL SELECT 'crs-spring-boot', 'Spring Data JPA and Hibernate'
    UNION ALL SELECT 'crs-spring-boot', 'Spring Security fundamentals'
    UNION ALL SELECT 'crs-react-modern', 'Components, props, and state'
    UNION ALL SELECT 'crs-react-modern', 'Hooks and effects'
    UNION ALL SELECT 'crs-react-modern', 'Client-side routing and APIs'
    UNION ALL SELECT 'crs-java-mastery', 'Object-oriented programming'
    UNION ALL SELECT 'crs-java-mastery', 'Collections, generics, and streams'
    UNION ALL SELECT 'crs-java-mastery', 'Exceptions and concurrency'
    UNION ALL SELECT 'crs-mysql-db', 'Relational modeling and normalization'
    UNION ALL SELECT 'crs-mysql-db', 'SQL joins and aggregation'
    UNION ALL SELECT 'crs-mysql-db', 'Indexes and transactions'
    UNION ALL SELECT 'crs-python-ml', 'Python fundamentals and NumPy'
    UNION ALL SELECT 'crs-python-ml', 'Pandas data frames'
    UNION ALL SELECT 'crs-python-ml', 'Exploratory data analysis'
    UNION ALL SELECT 'crs-ml-algorithms', 'Supervised learning'
    UNION ALL SELECT 'crs-ml-algorithms', 'Model evaluation'
    UNION ALL SELECT 'crs-ml-algorithms', 'Feature preparation'
    UNION ALL SELECT 'crs-deep-learning', 'Tensors and neural networks'
    UNION ALL SELECT 'crs-deep-learning', 'Training and evaluation'
) AS v
JOIN courses c ON c.code = v.course_code
WHERE NOT EXISTS (
    SELECT 1 FROM course_modules existing
    WHERE existing.course_id = c.id AND existing.module_name = v.module_name
);

INSERT IGNORE INTO assessments
    (code, title, description, skill_id, difficulty_level, total_marks, status,
     skill, difficulty, time_limit_minutes, icon)
SELECT v.code, v.title, v.description, s.id, v.difficulty, v.total_marks, 'ACTIVE',
       s.name, v.difficulty, v.time_limit_minutes, NULL
FROM (
    SELECT 'asm-spring-boot' AS code, 'Spring Boot & REST Architecture Assessment' AS title, 'Check core Spring Boot, REST, and persistence concepts.' AS description, 'Spring Boot' AS skill_name, 'Intermediate' AS difficulty, 2 AS total_marks, 10 AS time_limit_minutes
    UNION ALL SELECT 'asm-react', 'Modern React & Frontend Engineering Assessment', 'Check React state, rendering, and component fundamentals.', 'React', 'Intermediate', 2, 10
    UNION ALL SELECT 'asm-docker', 'Docker Containerization & DevOps Assessment', 'Check container and image fundamentals.', 'Docker', 'Intermediate', 2, 10
    UNION ALL SELECT 'asm-sql', 'Relational Database Design & MySQL Assessment', 'Check SQL querying and relational design fundamentals.', 'MySQL', 'Beginner', 2, 10
) AS v
JOIN skills s ON s.name = v.skill_name;

CREATE TEMPORARY TABLE IF NOT EXISTS skillsphere_seed_questions (
    assessment_code VARCHAR(80) NOT NULL,
    sequence_number SMALLINT UNSIGNED NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    correct_index SMALLINT UNSIGNED NOT NULL,
    explanation VARCHAR(1000) NOT NULL,
    option_1 VARCHAR(500) NOT NULL,
    option_2 VARCHAR(500) NOT NULL,
    option_3 VARCHAR(500) NOT NULL,
    option_4 VARCHAR(500) NOT NULL,
    PRIMARY KEY (assessment_code, sequence_number)
) ENGINE=InnoDB;

DELETE FROM skillsphere_seed_questions;

INSERT INTO skillsphere_seed_questions VALUES
    ('asm-spring-boot', 1, 'Which Spring annotation maps a handler method to HTTP GET requests?', 1, '@GetMapping is the composed annotation for GET request handlers.', '@PostMapping', '@GetMapping', '@PutMapping', '@DeleteMapping'),
    ('asm-spring-boot', 2, 'Which interface provides common JPA CRUD and pagination operations in Spring Data?', 1, 'JpaRepository provides JPA-specific operations and extends common repository abstractions.', 'CrudRepository', 'JpaRepository', 'QuerydslRepository', 'EntityManagerFactory'),
    ('asm-react', 1, 'Which React hook declares local state in a function component?', 1, 'useState returns a state value and a setter function.', 'useEffect', 'useState', 'useContext', 'useRef'),
    ('asm-react', 2, 'Why should rendered list items have stable keys?', 2, 'Keys let React match list items across renders.', 'To sort the list', 'To apply CSS', 'To identify changed, added, or removed items', 'Keys are only needed for forms'),
    ('asm-docker', 1, 'Which command builds an image from the Dockerfile in the current directory?', 1, 'docker build uses the current directory as the build context.', 'docker run .', 'docker build -t app .', 'docker create .', 'docker init image'),
    ('asm-docker', 2, 'What is the difference between an image and a container?', 1, 'An image is a template; a container is a running or stopped instance.', 'They are the same thing', 'An image is a template; a container is an instance', 'A container builds images automatically', 'Images only run on Windows'),
    ('asm-sql', 1, 'Which clause filters grouped results after aggregate calculations?', 1, 'HAVING applies conditions to grouped rows after aggregation.', 'WHERE', 'HAVING', 'ORDER BY', 'LIMIT'),
    ('asm-sql', 2, 'What does normalization primarily help reduce in a relational schema?', 1, 'Normalization reduces redundant storage and update anomalies.', 'Foreign keys', 'Data redundancy and update anomalies', 'Primary keys', 'Indexes');

INSERT IGNORE INTO assessment_questions
    (assessment_id, question_text, question_type, marks, sequence_number, correct_index, explanation)
SELECT a.id, q.question_text, 'MULTIPLE_CHOICE', 1, q.sequence_number, q.correct_index, q.explanation
FROM skillsphere_seed_questions q
JOIN assessments a ON a.code = q.assessment_code;

INSERT IGNORE INTO assessment_options (question_id, option_text, sequence_number, is_correct)
SELECT q.id, o.option_text, o.option_number - 1, o.option_number - 1 = q.correct_index
FROM skillsphere_seed_questions seed
JOIN assessments a ON a.code = seed.assessment_code
JOIN assessment_questions q ON q.assessment_id = a.id AND q.sequence_number = seed.sequence_number
JOIN (
    SELECT assessment_code, sequence_number, option_1 AS option_text, 1 AS option_number FROM skillsphere_seed_questions
    UNION ALL SELECT assessment_code, sequence_number, option_2, 2 FROM skillsphere_seed_questions
    UNION ALL SELECT assessment_code, sequence_number, option_3, 3 FROM skillsphere_seed_questions
    UNION ALL SELECT assessment_code, sequence_number, option_4, 4 FROM skillsphere_seed_questions
) o ON o.assessment_code = seed.assessment_code
   AND o.sequence_number = seed.sequence_number;
-- Mirror sample assessment content into the tables used by the existing JPA API.
INSERT INTO quiz_questions (assessment_id, question_text, correct_index, explanation)
SELECT a.id, seed.question_text, seed.correct_index, seed.explanation
FROM skillsphere_seed_questions seed
JOIN assessments a ON a.code = seed.assessment_code
WHERE NOT EXISTS (
    SELECT 1 FROM quiz_questions existing
    WHERE existing.assessment_id = a.id AND existing.question_text = seed.question_text
)
ORDER BY seed.assessment_code, seed.sequence_number;

INSERT INTO quiz_question_options (question_id, option_text, sequence_number)
SELECT q.id, o.option_text, o.option_number - 1
FROM skillsphere_seed_questions seed
JOIN assessments a ON a.code = seed.assessment_code
JOIN quiz_questions q ON q.assessment_id = a.id AND q.question_text = seed.question_text
JOIN (
    SELECT assessment_code, sequence_number, option_1 AS option_text, 1 AS option_number FROM skillsphere_seed_questions
    UNION ALL SELECT assessment_code, sequence_number, option_2, 2 FROM skillsphere_seed_questions
    UNION ALL SELECT assessment_code, sequence_number, option_3, 3 FROM skillsphere_seed_questions
    UNION ALL SELECT assessment_code, sequence_number, option_4, 4 FROM skillsphere_seed_questions
) o ON o.assessment_code = seed.assessment_code AND o.sequence_number = seed.sequence_number
WHERE NOT EXISTS (
    SELECT 1 FROM quiz_question_options existing
    WHERE existing.question_id = q.id AND existing.option_text = o.option_text
)
ORDER BY seed.assessment_code, seed.sequence_number, o.option_number;

DROP TEMPORARY TABLE IF EXISTS skillsphere_seed_questions;
