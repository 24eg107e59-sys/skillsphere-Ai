# SkillSphere AI database

This folder contains the MySQL 8.0+ schema and repeatable catalog data for the project:

- `schema.sql` creates the `skillsphere` database and the requested tables, keys, indexes, and constraints. It uses `CREATE TABLE IF NOT EXISTS`, and its upgrade section adds missing columns and foreign keys without dropping tables or rows.
- `seed.sql` inserts skills, career benchmarks, course resources, and assessment questions. It does not create users or passwords. Re-running it adds only missing catalog rows and preserves existing catalog edits.

## How the schema fits the existing Java entities

The backend currently has a legacy JPA model as well as the normalized tables requested for the project. The schema includes compatibility fields/tables so existing repositories can continue to work:

| Normalized concept | Existing JPA mapping |
| --- | --- |
| User full name and password hash | The current entity writes `users.name` and `users.password`; generated `full_name` and `password_hash` columns expose the normalized names. `UserService` stores passwords using BCrypt. |
| Student profile | `student_profiles` is normalized; `profiles` remains for `Profile.java`. |
| Career | `careers` is normalized; `career_paths` remains for `CareerPath.java`. `career_skills` supports both `career_id` and the existing `career_path_id`. |
| Course | `courses.skill_id` is the normalized skill FK. The legacy `courses.skill` text column remains because `CourseEntity` still reads/writes it. |
| Assessment and questions | `assessment_questions` / `assessment_options` are normalized. `assessments` also retains `code`, `skill`, `difficulty`, and `time_limit_minutes`; `quiz_questions` / `quiz_question_options` remain for the current API. |
| Progress | `student_progress` contains both the existing per-user summary fields and the normalized per-skill/per-course fields. |
| Assessment results | `assessment_results` remains for the current API; detailed answer-level records use `assessment_attempts` and `assessment_answers`. |

The database schema is backward-compatible rather than a Java-model migration: some legacy text columns are retained until the application is updated to read and write the normalized relationships. Do not remove those compatibility fields while the current JPA entities still use them.

## Before running

1. Back up any existing `skillsphere` database. These scripts do not drop data, but DDL changes are not rolled back as a single transaction by MySQL.
2. Use MySQL 8.0 or newer with permission to create/alter a database, tables, indexes, foreign keys, and stored procedures. The schema uses short-lived helper procedures to make additive upgrades repeatable, then removes those helper procedures.
3. The schema upgrade checks and adds foreign keys to older Hibernate-created tables. If an old table contains orphaned references, MySQL will stop at that constraint instead of deleting or silently ignoring those rows. Review and repair the orphan rows, then rerun `schema.sql`.

## Run on Windows

Open **Command Prompt** (not the MySQL prompt) and run:

```bat
cd /d "C:\Users\anshikareddy\OneDrive\Documents\skillsphere AI"
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < database\schema.sql
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < database\seed.sql
```

Enter your MySQL password at each prompt. If `mysql.exe` is already on `PATH`, you can use `mysql` in place of its full path. The scripts select or create the `skillsphere` database themselves.

The Spring Boot backend is configured to use MySQL. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in the environment before starting it if the defaults (`localhost:3306`, user `root`, empty password) do not match your MySQL installation. Do not commit credentials.

After importing the scripts and setting the database environment variables, start the backend from PowerShell with:

```powershell
Set-Location "C:\Users\anshikareddy\OneDrive\Documents\skillsphere AI\backend\backend"
.\mvnw.cmd spring-boot:run
```

An administrator is **not** inserted by either SQL script. The backend only provisions one if `SKILLSPHERE_ADMIN_PASSWORD` is configured; it hashes that value with BCrypt. Optionally set `SKILLSPHERE_ADMIN_EMAIL` (default `admin@skillsphere.com`) before first startup. Use a unique strong password and provide it through a private environment/IDE secret setting, not source control.

Automated backend tests use an isolated in-memory H2 database; the normal application configuration uses MySQL.

## Beginner-friendly schema notes

- A **primary key** (`id`, or a natural key such as a profile's `user_id`) uniquely identifies each row.
- A **foreign key** prevents a row from referring to a student, skill, course, or assessment that does not exist.
- **Unique constraints** prevent duplicate emails, skill names, student/skill mappings, career benchmarks, and ordered course/assessment items.
- `ON DELETE CASCADE` removes child-only data such as a student's profile when its parent is intentionally deleted. `RESTRICT` protects reference/catalog data and assessment history. `SET NULL` keeps the dependent record when its optional course or skill is removed.
- `created_at` and `updated_at` record when a row was created or changed. `full_name` and `password_hash` are virtual aliases for the legacy JPA column names. Password data is not included in the sample seed; normal registration and optional admin provisioning use BCrypt hashes. If upgrading an older installation, confirm existing `users.password` values are BCrypt hashes before treating them as password hashes.
- Profile interests are stored as a MySQL `JSON` value so multiple interests are not packed into a comma-separated string.

## Verify the schema and sample data

Run these queries in MySQL Workbench or the MySQL command prompt:

```sql
USE skillsphere;

-- Confirm the tables exist.
SHOW TABLES;

-- Inspect the key user and relationship tables.
SHOW CREATE TABLE users;
SHOW CREATE TABLE student_skills;
SHOW CREATE TABLE career_skills;
SHOW CREATE TABLE assessment_answers;

-- List foreign-key relationships in the database.
SELECT
    kcu.table_name,
    kcu.column_name,
    kcu.referenced_table_name,
    kcu.referenced_column_name,
    rc.delete_rule,
    rc.update_rule
FROM information_schema.key_column_usage AS kcu
JOIN information_schema.referential_constraints AS rc
  ON rc.constraint_schema = kcu.constraint_schema
 AND rc.constraint_name = kcu.constraint_name
WHERE kcu.constraint_schema = DATABASE()
  AND kcu.referenced_table_name IS NOT NULL
ORDER BY kcu.table_name, kcu.column_name;

-- Find any existing child rows that do not have a parent.
SELECT 'student_skills without a user' AS check_name, COUNT(*) AS problem_rows
FROM student_skills ss LEFT JOIN users u ON u.id = ss.student_id
WHERE u.id IS NULL
UNION ALL
SELECT 'student_skills without a skill', COUNT(*)
FROM student_skills ss LEFT JOIN skills s ON s.id = ss.skill_id
WHERE s.id IS NULL;

-- Inspect the course catalog and its associated skill.
SELECT c.code, c.title, c.provider, c.url, c.difficulty_level,
       c.duration_hours, s.name AS skill, c.status
FROM courses c
LEFT JOIN skills s ON s.id = c.skill_id
ORDER BY c.title;

-- Inspect the four requested careers.
SELECT code, title, category, experience_level
FROM careers
ORDER BY title;

-- Inspect the required skill benchmarks for each career.
SELECT c.title AS career, s.name AS skill, cs.required_level, cs.importance_weight
FROM career_skills cs
JOIN careers c ON c.id = cs.career_id
JOIN skills s ON s.id = cs.skill_id
ORDER BY c.title, cs.importance_weight DESC, s.name;

-- Confirm the sample assessment questions and options.
SELECT a.code, a.title, q.sequence_number, q.question_text,
       o.sequence_number AS option_number, o.option_text, o.is_correct
FROM assessments a
JOIN assessment_questions q ON q.assessment_id = a.id
JOIN assessment_options o ON o.question_id = q.id
ORDER BY a.code, q.sequence_number, o.sequence_number;

-- The SQL seed deliberately creates no accounts.
SELECT COUNT(*) AS user_rows FROM users;
```

## Execution status

The files define the operations to run. Applying them to a MySQL instance requires a MySQL account with the privileges listed above; do not treat file creation as database execution. After running both scripts, use the verification queries and confirm they complete without foreign-key errors.
