import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { authFetch, clearAuth, getUser } from "../api";
import "./Roadmap.css";

function Roadmap() {
  const [user, setUser] = useState(null);
  const [roadmap, setRoadmap] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [activeTab, setActiveTab] = useState("timeline"); // "timeline" or "courses"
  const [enrolledCourses, setEnrolledCourses] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    const currentUser = getUser();
    if (!currentUser) {
      navigate("/login");
      return;
    }

    setUser(currentUser);
    fetchRoadmap(currentUser.id);
  }, [navigate]);

  const fetchRoadmap = async (userId) => {
    setLoading(true);
    setError("");
    try {
      const res = await authFetch(`/roadmap/${userId}`);
      if (res.ok) {
        const data = await res.json();
        setRoadmap(data);
      } else {
        const err = await res.json().catch(() => ({}));
        setError(err.message || "Failed to generate learning roadmap.");
      }
    } catch (err) {
      console.error("Roadmap fetch error:", err);
      setError("Cannot connect to backend server on port 8080.");
    } finally {
      setLoading(false);
    }
  };

  const handleEnrollCourse = (courseId) => {
    setEnrolledCourses((prev) => ({
      ...prev,
      [courseId]: !prev[courseId],
    }));
  };

  const handleLogout = () => {
    clearAuth();
    navigate("/login");
  };

  if (!user || loading) {
    return (
      <div className="roadmap-loading">
        <div className="spinner"></div>
        <p>Generating your AI personalized learning roadmap & courses...</p>
      </div>
    );
  }

  return (
    <div className="roadmap-page">
      {/* Top Navbar */}
      <header className="roadmap-header">
        <div className="header-brand">
          <Link to="/dashboard" className="brand-logo">
            SkillSphere <span>AI</span>
          </Link>
          <span className="brand-divider">/</span>
          <span className="brand-crumb">Personalized Roadmap</span>
        </div>

        <div className="header-actions">
          <Link to="/courses" className="nav-link">
            📚 All Courses
          </Link>
          <Link to="/skill-gap" className="nav-link">
            ⚡ Skill Gap
          </Link>
          <Link to="/dashboard" className="nav-link">
            ⬅️ Dashboard
          </Link>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="roadmap-content">
        {/* Title Bar */}
        <div className="roadmap-title-bar">
          <div>
            <h1>Personalized Career Roadmap 🗺️</h1>
            <p>
              AI-generated curriculum tailored to bridge your exact skill gaps
              for <strong>{roadmap?.targetCareer}</strong>.
            </p>
          </div>
          <div className="roadmap-meta-pills">
            <span className="meta-pill">
              ⏱️ ~{roadmap?.estimatedWeeks} Weeks Total
            </span>
            <span className="meta-pill phases">
              🎯 {roadmap?.totalPhases} Learning Phases
            </span>
          </div>
        </div>

        {error && <div className="roadmap-alert error">{error}</div>}

        {/* AI Roadmap Advice Banner */}
        {roadmap?.aiRoadmapAdvice && (
          <div className="roadmap-advice-card">
            <div className="advice-icon">🤖</div>
            <div className="advice-text">
              <h4>SkillSphere AI Roadmap Strategy</h4>
              <p>{roadmap.aiRoadmapAdvice}</p>
            </div>
          </div>
        )}

        {/* Tab Navigation */}
        <div className="roadmap-tabs">
          <button
            className={`tab-btn ${activeTab === "timeline" ? "active" : ""}`}
            onClick={() => setActiveTab("timeline")}
          >
            🗓️ Phase-by-Phase Timeline ({roadmap?.phases?.length || 0})
          </button>
          <button
            className={`tab-btn ${activeTab === "courses" ? "active" : ""}`}
            onClick={() => setActiveTab("courses")}
          >
            🎓 Recommended Courses ({roadmap?.recommendedCourses?.length || 0})
          </button>
        </div>

        {/* TAB 1: TIMELINE */}
        {activeTab === "timeline" && (
          <div className="timeline-container">
            {roadmap?.phases?.map((phase) => (
              <div key={phase.phase} className="timeline-card">
                <div className="timeline-badge-col">
                  <div className="phase-circle">P{phase.phase}</div>
                  <div className="timeline-line"></div>
                </div>

                <div className="timeline-body">
                  <div className="phase-header">
                    <div>
                      <span className="phase-num">Phase {phase.phase}</span>
                      <h3>{phase.title}</h3>
                    </div>
                    <div className="phase-badges">
                      <span className="duration-badge">⏱️ {phase.duration}</span>
                      <span
                        className={`status-badge ${
                          phase.status === "IN_PROGRESS"
                            ? "in-progress"
                            : "not-started"
                        }`}
                      >
                        {phase.status === "IN_PROGRESS"
                          ? "● Current Focus"
                          : "○ Upcoming"}
                      </span>
                    </div>
                  </div>

                  <p className="phase-description">{phase.description}</p>

                  <div className="skill-focus-pill">
                    <strong>Primary Skill Focus:</strong> {phase.skillFocus}
                  </div>

                  {/* Core Topics Checklist */}
                  <div className="topics-section">
                    <h4>Key Syllabus Modules to Cover:</h4>
                    <div className="topics-grid">
                      {phase.topics?.map((topic, idx) => (
                        <div key={idx} className="topic-item">
                          <span className="check-icon">✓</span>
                          <span>{topic}</span>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Portfolio Milestone Project */}
                  {phase.portfolioProject && (
                    <div className="project-highlight-box">
                      <div className="project-icon">💼</div>
                      <div className="project-content">
                        <strong>Portfolio Milestone Project:</strong>
                        <p>{phase.portfolioProject}</p>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* TAB 2: RECOMMENDED COURSES */}
        {activeTab === "courses" && (
          <div className="courses-tab-view">
            <div className="section-intro">
              <h3>Targeted Courses to Bridge Your Missing Skills</h3>
              <p>
                Each recommended course corresponds directly to a skill
                highlighted in your Skill Gap Analysis.
              </p>
            </div>

            <div className="courses-grid">
              {roadmap?.recommendedCourses?.map((course) => {
                const isEnrolled = enrolledCourses[course.id];
                return (
                  <div key={course.id} className="course-card">
                    <div className="course-card-header">
                      <span className="course-icon">{course.icon}</span>
                      <span className="course-level-tag">{course.level}</span>
                    </div>

                    <span className="course-skill-pill">
                      Skill: {course.skill}
                    </span>
                    <h4>{course.title}</h4>
                    <p className="course-desc">{course.description}</p>

                    <div className="course-meta-row">
                      <span>⏱️ {course.duration}</span>
                      <span>⭐ {course.rating} / 5.0</span>
                      <span>🏫 {course.provider}</span>
                    </div>

                    <div className="course-modules-list">
                      <small>Syllabus Modules:</small>
                      <ul>
                        {course.modules?.map((m, idx) => (
                          <li key={idx}>{m}</li>
                        ))}
                      </ul>
                    </div>

                    <button
                      className={`enroll-btn ${isEnrolled ? "enrolled" : ""}`}
                      onClick={() => handleEnrollCourse(course.id)}
                    >
                      {isEnrolled ? "✓ Enrolled (In Progress)" : "🚀 Start Course"}
                    </button>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Action Footer */}
        <div className="roadmap-footer">
          <Link to="/courses" className="browse-all-btn">
            Browse All Available Courses ➔
          </Link>
          <Link to="/skill-gap" className="back-gap-btn">
            ⬅️ Review Skill Gap
          </Link>
        </div>
      </main>
    </div>
  );
}

export default Roadmap;
