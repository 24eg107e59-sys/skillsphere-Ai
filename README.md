# 🚀 SkillSphere AI - Personalized Skill Gap Analysis & Career Roadmap Platform

SkillSphere AI is an intelligent full-stack career readiness platform designed to benchmark student skillsets against industry standards, identify real-time skill gaps, generate dynamic step-by-step learning roadmaps, and provide targeted course recommendations and quizzes.

---

## 🛠️ Tech Stack

### Frontend
- **Framework:** React 19 (Vite)
- **Routing:** React Router v7
- **Styling:** Custom CSS with modern UI & responsive layout
- **API Client:** Fetch API with JWT Bearer Authentication

### Backend
- **Framework:** Spring Boot 3.3.4 (Java 21)
- **Security:** Spring Security with Stateless JWT Authentication & BCrypt
- **ORM / Persistence:** Spring Data JPA / Hibernate
- **Database:** MySQL 8.0

---

## 🌟 Key Features

1. **Role-Based Authentication & Security:**
   - JWT-based authentication for Students and Platform Administrators.
   - Password encryption with BCrypt.
2. **Student Profile & Skill Persistence:**
   - Real-time skill management with instant persistence to MySQL.
   - Target career selection across 6+ industry domains.
   - Floating toast notification system for instant feedback.
3. **AI Skill Gap Analysis:**
   - Dynamic role-readiness calculation benchmarking possessed skills against industry targets.
   - Categorization into Matched Core Skills, Missing Skills (The Gap), and Bonus Skills.
4. **Interactive Learning Roadmaps:**
   - Phase-by-phase learning paths with progress tracking and course links.
5. **Interactive Quizzes & Assessments:**
   - Domain-specific assessments with instant scoring and performance tracking.
6. **Administrator Dashboard:**
   - Student management, skill catalog management, and course management.

---

## 🚀 Getting Started

### Prerequisites
- **Java:** JDK 21+
- **Node.js:** v18+ or v20+
- **Build Tool:** Apache Maven 3.9+
- **Database:** MySQL Server 8.0+

### Database Setup
1. Create a database in MySQL:
   ```sql
   CREATE DATABASE skillsphere;
   ```
2. Configure database credentials in `backend/backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/skillsphere?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=root
   ```

---

## 🏃‍♂️ Running the Application

### 1. Start Backend (Spring Boot)
```bash
cd backend/backend
mvn spring-boot:run
```
*Backend runs on: `http://localhost:8080`*

### 2. Start Frontend (React Vite)
```bash
cd frontend
npm install
npm run dev
```
*Frontend runs on: `http://localhost:5173`*

---

## 📁 Project Structure

```
skillsphere-ai/
├── backend/
│   └── backend/
│       ├── pom.xml
│       └── src/
│           ├── main/java/com/skillsphere/
│           │   ├── config/          # Security & Data Initializer
│           │   ├── controller/      # REST API Controllers
│           │   ├── dto/             # Data Transfer Objects
│           │   ├── entity/          # JPA Entities
│           │   ├── repository/      # Spring Data Repositories
│           │   ├── security/        # JWT Authentication Filter & Utils
│           │   └── service/         # Business Logic
│           └── main/resources/      # application.properties
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api.js                   # Centralized JWT Fetch Client
│       ├── pages/                   # React Views & Pages
│       └── App.jsx                  # Main Router
├── run-backend.bat                  # One-click backend startup script
├── run-frontend.bat                 # One-click frontend startup script
└── README.md
```
