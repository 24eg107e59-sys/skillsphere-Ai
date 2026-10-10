-- SkillSphere AI schema for MySQL 8.0+
-- Safe to re-run: existing tables and rows are not dropped.
CREATE DATABASE IF NOT EXISTS skillsphere
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE skillsphere;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NULL,
    full_name VARCHAR(255) GENERATED ALWAYS AS (`name`) VIRTUAL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) GENERATED ALWAYS AS (`password`) VIRTUAL,
    role VARCHAR(255) NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('STUDENT', 'ADMIN')),
    INDEX ix_users_role_enabled (role, enabled)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NULL,
    description VARCHAR(500) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_skills_name UNIQUE (name),
    INDEX ix_skills_category (category)
) ENGINE=InnoDB;

-- The legacy profile table is retained for Profile.java.
CREATE TABLE IF NOT EXISTS profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    full_name VARCHAR(255) NULL,
    email VARCHAR(255) NULL,
    phone VARCHAR(255) NULL,
    college VARCHAR(255) NULL,
    degree VARCHAR(255) NULL,
    graduation_year VARCHAR(255) NULL,
    experience_level VARCHAR(255) NULL,
    target_career VARCHAR(255) NULL,
    current_skills VARCHAR(2000) NULL,
    bio VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS student_profiles (
    user_id BIGINT NOT NULL,
    education VARCHAR(255) NULL,
    institution VARCHAR(255) NULL,
    interests JSON NULL,
    career_goal VARCHAR(255) NULL,
    bio VARCHAR(1000) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_student_profiles_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX ix_student_profiles_career_goal (career_goal)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS student_skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    proficiency_level TINYINT UNSIGNED NOT NULL DEFAULT 1,
    experience_months SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_student_skills_mapping UNIQUE (student_id, skill_id),
    CONSTRAINT chk_student_skills_level CHECK (proficiency_level BETWEEN 1 AND 5),
    CONSTRAINT fk_student_skills_student FOREIGN KEY (student_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_student_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX ix_student_skills_skill (skill_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS careers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NULL,
    category VARCHAR(80) NULL,
    experience_level VARCHAR(40) NOT NULL DEFAULT 'Entry',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_careers_code UNIQUE (code),
    CONSTRAINT uq_careers_title UNIQUE (title),
    INDEX ix_careers_category_level (category, experience_level)
) ENGINE=InnoDB;

-- career_paths is the current CareerPath.java entity table.
CREATE TABLE IF NOT EXISTS career_paths (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NULL,
    icon VARCHAR(255) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_career_paths_code UNIQUE (code)
) ENGINE=InnoDB;

-- career_path_id is the existing JPA join column; career_id supports the
-- normalized careers catalog. Either relationship can be populated safely.
CREATE TABLE IF NOT EXISTS career_skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    career_path_id BIGINT NULL,
    career_id BIGINT NULL,
    skill_id BIGINT NOT NULL,
    required_level TINYINT UNSIGNED NOT NULL DEFAULT 3,
    importance_weight DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    PRIMARY KEY (id),
    CONSTRAINT uq_career_skills_legacy UNIQUE (career_path_id, skill_id),
    CONSTRAINT uq_career_skills_normalized UNIQUE (career_id, skill_id),
    CONSTRAINT chk_career_skills_level CHECK (required_level BETWEEN 1 AND 5),
    CONSTRAINT chk_career_skills_weight CHECK (importance_weight > 0 AND importance_weight <= 100),
    CONSTRAINT fk_career_skills_path FOREIGN KEY (career_path_id) REFERENCES career_paths (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_career_skills_career FOREIGN KEY (career_id) REFERENCES careers (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_career_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX ix_career_skills_skill (skill_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NULL,
    provider VARCHAR(255) NULL,
    url VARCHAR(1000) NULL,
    difficulty_level VARCHAR(40) NULL,
    duration_hours DECIMAL(7,2) NULL,
    skill_id BIGINT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    -- Legacy fields used by CourseEntity.java.
    skill VARCHAR(255) NOT NULL,
    level VARCHAR(255) NULL,
    duration VARCHAR(255) NULL,
    rating DOUBLE NULL,
    icon VARCHAR(255) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_courses_code UNIQUE (code),
    CONSTRAINT fk_courses_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_courses_duration CHECK (duration_hours IS NULL OR duration_hours >= 0),
    CONSTRAINT chk_courses_rating CHECK (rating IS NULL OR (rating >= 0 AND rating <= 5)),
    INDEX ix_courses_skill_status (skill_id, status),
    INDEX ix_courses_difficulty (difficulty_level)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_modules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_course_modules_name UNIQUE (course_id, module_name),
    CONSTRAINT fk_course_modules_course FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assessments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NULL,
    skill_id BIGINT NULL,
    difficulty_level VARCHAR(40) NULL,
    total_marks SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    -- Legacy fields used by AssessmentEntity.java.
    skill VARCHAR(255) NOT NULL,
    difficulty VARCHAR(255) NULL,
    time_limit_minutes INT NOT NULL DEFAULT 0,
    icon VARCHAR(255) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_assessments_code UNIQUE (code),
    CONSTRAINT fk_assessments_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX ix_assessments_skill_status (skill_id, status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assessment_questions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    assessment_id BIGINT NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    question_type VARCHAR(30) NOT NULL DEFAULT 'MULTIPLE_CHOICE',
    marks SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    sequence_number SMALLINT UNSIGNED NOT NULL,
    correct_index INT NOT NULL DEFAULT 0,
    explanation VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_assessment_question_sequence UNIQUE (assessment_id, sequence_number),
    CONSTRAINT fk_assessment_questions_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX ix_assessment_questions_type (question_type)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assessment_options (
    id BIGINT NOT NULL AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    sequence_number SMALLINT UNSIGNED NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT uq_assessment_option_sequence UNIQUE (question_id, sequence_number),
    CONSTRAINT uq_assessment_option_question_id UNIQUE (question_id, id),
    CONSTRAINT fk_assessment_options_question FOREIGN KEY (question_id) REFERENCES assessment_questions (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Legacy assessment question tables are retained for QuizQuestionEntity.java.
CREATE TABLE IF NOT EXISTS quiz_questions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    assessment_id BIGINT NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    correct_index INT NOT NULL DEFAULT 0,
    explanation VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_quiz_questions_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX ix_quiz_questions_assessment (assessment_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quiz_question_options (
    id BIGINT NOT NULL AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    sequence_number INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_quiz_options_question FOREIGN KEY (question_id) REFERENCES quiz_questions (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX ix_quiz_options_question_order (question_id, sequence_number)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assessment_attempts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    assessment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    score DECIMAL(8,2) NULL,
    total_marks DECIMAL(8,2) NOT NULL DEFAULT 0,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_attempts_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_attempts_student FOREIGN KEY (student_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX ix_attempts_student_date (student_id, started_at),
    INDEX ix_attempts_assessment_date (assessment_id, started_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assessment_answers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option_id BIGINT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    marks_awarded DECIMAL(8,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uq_attempt_question UNIQUE (attempt_id, question_id),
    CONSTRAINT fk_answers_attempt FOREIGN KEY (attempt_id) REFERENCES assessment_attempts (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES assessment_questions (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_answers_selected_option FOREIGN KEY (question_id, selected_option_id)
        REFERENCES assessment_options (question_id, id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- AssessmentResult.java compatibility table used by the current REST API.
CREATE TABLE IF NOT EXISTS assessment_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NULL,
    assessment_id VARCHAR(255) NULL,
    assessment_title VARCHAR(255) NULL,
    skill VARCHAR(255) NULL,
    score INT NOT NULL DEFAULT 0,
    total_questions INT NOT NULL DEFAULT 0,
    percentage INT NOT NULL DEFAULT 0,
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_assessment_results_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX ix_assessment_results_user_date (user_id, completed_at),
    INDEX ix_assessment_results_assessment (assessment_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS learning_roadmaps (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    career_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (id),
    CONSTRAINT fk_roadmaps_student FOREIGN KEY (student_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_roadmaps_career FOREIGN KEY (career_id) REFERENCES careers (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX ix_roadmaps_student_status (student_id, status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS roadmap_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    roadmap_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    course_id BIGINT NULL,
    phase VARCHAR(80) NOT NULL,
    priority TINYINT UNSIGNED NOT NULL DEFAULT 3,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sequence_number SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_roadmap_item_sequence UNIQUE (roadmap_id, sequence_number),
    CONSTRAINT chk_roadmap_item_priority CHECK (priority BETWEEN 1 AND 5),
    CONSTRAINT fk_roadmap_items_roadmap FOREIGN KEY (roadmap_id) REFERENCES learning_roadmaps (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_roadmap_items_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_roadmap_items_course FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX ix_roadmap_items_skill (skill_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS student_progress (
    id BIGINT NOT NULL AUTO_INCREMENT,
    -- user_id and summary fields preserve StudentProgress.java compatibility.
    user_id BIGINT NULL,
    target_career VARCHAR(255) NULL,
    readiness_score INT NOT NULL DEFAULT 0,
    completed_phases INT NOT NULL DEFAULT 0,
    total_phases INT NOT NULL DEFAULT 3,
    last_updated DATETIME(6) NULL,
    -- Normalized per-skill/per-course progress fields.
    student_id BIGINT NULL,
    skill_id BIGINT NULL,
    course_id BIGINT NULL,
    completion_percentage TINYINT UNSIGNED NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_student_progress_legacy_user UNIQUE (user_id),
    CONSTRAINT uq_student_progress_mapping UNIQUE (student_id, skill_id, course_id),
    CONSTRAINT chk_student_progress_mapping_owner CHECK (
        (student_id IS NULL AND skill_id IS NULL AND course_id IS NULL)
        OR student_id IS NOT NULL
    ),
    CONSTRAINT chk_student_progress_users_match CHECK (
        user_id IS NULL OR student_id IS NULL OR user_id = student_id
    ),
    CONSTRAINT chk_student_progress_completion CHECK (completion_percentage BETWEEN 0 AND 100),
    CONSTRAINT fk_student_progress_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_student_progress_student FOREIGN KEY (student_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_student_progress_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_student_progress_course FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX ix_student_progress_student_status (student_id, status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    career_id BIGINT NULL,
    skill_id BIGINT NULL,
    course_id BIGINT NULL,
    reason VARCHAR(1000) NOT NULL,
    priority TINYINT UNSIGNED NOT NULL DEFAULT 3,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_recommendations_priority CHECK (priority BETWEEN 1 AND 5),
    CONSTRAINT fk_recommendations_student FOREIGN KEY (student_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_recommendations_career FOREIGN KEY (career_id) REFERENCES careers (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_recommendations_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_recommendations_course FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX ix_recommendations_student_priority (student_id, priority, created_at)
) ENGINE=InnoDB;

-- Additive upgrades for tables created by older Hibernate versions. The
-- procedure checks information_schema first, so these calls are repeatable.
DROP PROCEDURE IF EXISTS skillsphere_add_column_if_missing;
DELIMITER //
CREATE PROCEDURE skillsphere_add_column_if_missing(
    IN p_table VARCHAR(64),
    IN p_column VARCHAR(64),
    IN p_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = p_table
          AND column_name = p_column
    ) THEN
        SET @ddl = CONCAT(
            'ALTER TABLE `', REPLACE(p_table, '`', '``'),
            '` ADD COLUMN `', REPLACE(p_column, '`', '``'), '` ', p_definition
        );
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

CALL skillsphere_add_column_if_missing('users', 'created_at', 'TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP');
CALL skillsphere_add_column_if_missing('users', 'updated_at', 'TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL skillsphere_add_column_if_missing('users', 'enabled', 'BOOLEAN NOT NULL DEFAULT TRUE');
CALL skillsphere_add_column_if_missing('users', 'full_name', 'VARCHAR(255) GENERATED ALWAYS AS (`name`) VIRTUAL');
CALL skillsphere_add_column_if_missing('users', 'password_hash', 'VARCHAR(255) GENERATED ALWAYS AS (`password`) VIRTUAL');
CALL skillsphere_add_column_if_missing('courses', 'skill_id', 'BIGINT NULL');
CALL skillsphere_add_column_if_missing('courses', 'url', 'VARCHAR(1000) NULL');
CALL skillsphere_add_column_if_missing('courses', 'difficulty_level', 'VARCHAR(40) NULL');
CALL skillsphere_add_column_if_missing('courses', 'duration_hours', 'DECIMAL(7,2) NULL');
CALL skillsphere_add_column_if_missing('courses', 'status', 'VARCHAR(20) NOT NULL DEFAULT ''ACTIVE''');
CALL skillsphere_add_column_if_missing('assessments', 'description', 'VARCHAR(1000) NULL');
CALL skillsphere_add_column_if_missing('assessments', 'skill_id', 'BIGINT NULL');
CALL skillsphere_add_column_if_missing('assessments', 'difficulty_level', 'VARCHAR(40) NULL');
CALL skillsphere_add_column_if_missing('assessments', 'total_marks', 'SMALLINT UNSIGNED NOT NULL DEFAULT 0');
CALL skillsphere_add_column_if_missing('assessments', 'status', 'VARCHAR(20) NOT NULL DEFAULT ''ACTIVE''');
CALL skillsphere_add_column_if_missing('quiz_question_options', 'sequence_number', 'INT NULL');
CALL skillsphere_add_column_if_missing('career_skills', 'id', 'BIGINT NOT NULL AUTO_INCREMENT UNIQUE');
CALL skillsphere_add_column_if_missing('career_skills', 'career_id', 'BIGINT NULL');
CALL skillsphere_add_column_if_missing('career_skills', 'required_level', 'TINYINT UNSIGNED NOT NULL DEFAULT 3');
CALL skillsphere_add_column_if_missing('career_skills', 'importance_weight', 'DECIMAL(5,2) NOT NULL DEFAULT 1.00');
CALL skillsphere_add_column_if_missing('student_progress', 'student_id', 'BIGINT NULL');
CALL skillsphere_add_column_if_missing('student_progress', 'skill_id', 'BIGINT NULL');
CALL skillsphere_add_column_if_missing('student_progress', 'course_id', 'BIGINT NULL');
CALL skillsphere_add_column_if_missing('student_progress', 'completion_percentage', 'TINYINT UNSIGNED NOT NULL DEFAULT 0');
CALL skillsphere_add_column_if_missing('student_progress', 'status', 'VARCHAR(20) NOT NULL DEFAULT ''NOT_STARTED''');
CALL skillsphere_add_column_if_missing('student_progress', 'updated_at', 'TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
DROP PROCEDURE skillsphere_add_column_if_missing;

-- Normalized progress rows have student_id; user_id remains available for the
-- legacy one-summary-per-user JPA view and therefore must allow NULL.
DROP PROCEDURE IF EXISTS skillsphere_allow_null_student_progress_user;
DELIMITER //
CREATE PROCEDURE skillsphere_allow_null_student_progress_user()
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'student_progress'
          AND column_name = 'user_id'
          AND is_nullable = 'NO'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE constraint_schema = DATABASE()
              AND table_name = 'student_progress'
              AND constraint_name = 'fk_student_progress_user'
              AND constraint_type = 'FOREIGN KEY'
        ) THEN
            ALTER TABLE student_progress DROP FOREIGN KEY fk_student_progress_user;
        END IF;
        ALTER TABLE student_progress MODIFY COLUMN user_id BIGINT NULL;
    END IF;
END//
DELIMITER ;
CALL skillsphere_allow_null_student_progress_user();
DROP PROCEDURE skillsphere_allow_null_student_progress_user;

-- Add missing relationships to older Hibernate-created tables. These ALTERs
-- validate existing rows; if orphaned rows exist, MySQL reports the problem
-- and leaves the data untouched so it can be reviewed before retrying.
DROP PROCEDURE IF EXISTS skillsphere_add_fk_if_missing;
DELIMITER //
CREATE PROCEDURE skillsphere_add_fk_if_missing(
    IN p_table VARCHAR(64),
    IN p_constraint VARCHAR(64),
    IN p_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_schema = DATABASE()
          AND table_name = p_table
          AND constraint_name = p_constraint
          AND constraint_type = 'FOREIGN KEY'
    ) THEN
        SET @ddl = CONCAT(
            'ALTER TABLE `', REPLACE(p_table, '`', '``'),
            '` ADD CONSTRAINT `', REPLACE(p_constraint, '`', '``'), '` ', p_definition
        );
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

CALL skillsphere_add_fk_if_missing('profiles', 'fk_profiles_user', 'FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('career_skills', 'fk_career_skills_path', 'FOREIGN KEY (career_path_id) REFERENCES career_paths (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('career_skills', 'fk_career_skills_career', 'FOREIGN KEY (career_id) REFERENCES careers (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('career_skills', 'fk_career_skills_skill', 'FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE RESTRICT ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('courses', 'fk_courses_skill', 'FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE SET NULL ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('course_modules', 'fk_course_modules_course', 'FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('assessments', 'fk_assessments_skill', 'FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE SET NULL ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('quiz_questions', 'fk_quiz_questions_assessment', 'FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('quiz_question_options', 'fk_quiz_options_question', 'FOREIGN KEY (question_id) REFERENCES quiz_questions (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('assessment_results', 'fk_assessment_results_user', 'FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('student_progress', 'fk_student_progress_user', 'FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('student_progress', 'fk_student_progress_student', 'FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('student_progress', 'fk_student_progress_skill', 'FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE RESTRICT ON UPDATE CASCADE');
CALL skillsphere_add_fk_if_missing('student_progress', 'fk_student_progress_course', 'FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE SET NULL ON UPDATE CASCADE');
DROP PROCEDURE skillsphere_add_fk_if_missing;

DROP PROCEDURE IF EXISTS skillsphere_add_unique_index_if_missing;
DELIMITER //
CREATE PROCEDURE skillsphere_add_unique_index_if_missing(
    IN p_table VARCHAR(64),
    IN p_index VARCHAR(64),
    IN p_columns TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = p_table
          AND index_name = p_index
    ) THEN
        SET @ddl = CONCAT(
            'ALTER TABLE `', REPLACE(p_table, '`', '``'),
            '` ADD UNIQUE INDEX `', REPLACE(p_index, '`', '``'), '` (', p_columns, ')'
        );
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

CALL skillsphere_add_unique_index_if_missing('career_skills', 'uq_career_skills_normalized', '`career_id`, `skill_id`');
CALL skillsphere_add_unique_index_if_missing('student_progress', 'uq_student_progress_mapping', '`student_id`, `skill_id`, `course_id`');
DROP PROCEDURE skillsphere_add_unique_index_if_missing;
