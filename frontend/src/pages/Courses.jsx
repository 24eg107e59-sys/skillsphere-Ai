import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { authFetch, clearAuth, getUser } from "../api";
import "./Courses.css";

function Courses() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedLevel, setSelectedLevel] = useState("ALL");
  const [enrolledMap, setEnrolledMap] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    const currentUser = getUser();
    if (!currentUser) {
      navigate("/login");
      return;
    }
    fetchCourses();
  }, [navigate]);

  const fetchCourses = async () => {
    setLoading(true);
    try {
      const res = await authFetch("/courses");
      if (res.ok) {
        const data = await res.json();
        setCourses(data);
      }
    } catch (err) {
      console.error("Failed to load courses:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleEnrollToggle = (id) => {
    setEnrolledMap((prev) => ({
      ...prev,
      [id]: !prev[id],
    }));
  };

  const filteredCourses = courses.filter((c) => {
    const matchesSearch =
      c.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.skill.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.description.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesLevel =
      selectedLevel === "ALL" ||
      c.level.toUpperCase() === selectedLevel.toUpperCase();

    return matchesSearch && matchesLevel;
  });

  return (
    <div className="courses-page">
      <header className="courses-header">
        <div className="header-brand">
          <Link to="/dashboard" className="brand-logo">
            SkillSphere <span>AI</span>
          </Link>
          <span className="brand-divider">/</span>
          <span className="brand-crumb">Course Catalog</span>
        </div>

        <div className="header-actions">
          <Link to="/roadmap" className="nav-link">
            🗺️ My Roadmap
          </Link>
          <Link to="/dashboard" className="nav-link">
            ⬅️ Dashboard
          </Link>
        </div>
      </header>

      <main className="courses-content">
        <div className="courses-title-bar">
          <div>
            <h1>Curated Industry Courses 📚</h1>
            <p>
              Hand-picked, high-impact modules engineered to accelerate your
              skills from fundamentals to production-grade deployment.
            </p>
          </div>
          <Link to="/roadmap" className="recommended-btn">
            View My AI Recommended Courses ➔
          </Link>
        </div>

        {/* Filter & Search Bar */}
        <div className="filter-bar">
          <div className="search-input-box">
            <span>🔍</span>
            <input
              type="text"
              placeholder="Search by skill (e.g. Spring Boot, React, Docker, Python)..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div className="level-filter-chips">
            {["ALL", "BEGINNER", "INTERMEDIATE", "ADVANCED"].map((lvl) => (
              <button
                key={lvl}
                className={`level-chip ${selectedLevel === lvl ? "active" : ""}`}
                onClick={() => setSelectedLevel(lvl)}
              >
                {lvl}
              </button>
            ))}
          </div>
        </div>

        {loading ? (
          <div className="courses-loading">
            <div className="spinner"></div>
            <p>Loading course catalog...</p>
          </div>
        ) : (
          <div className="courses-grid">
            {filteredCourses.length === 0 ? (
              <div className="no-courses-found">
                <p>No courses match your search criteria.</p>
              </div>
            ) : (
              filteredCourses.map((course) => {
                const isEnrolled = enrolledMap[course.id];
                return (
                  <div key={course.id} className="catalog-course-card">
                    <div className="card-top">
                      <span className="card-icon">{course.icon}</span>
                      <span className="level-badge">{course.level}</span>
                    </div>

                    <span className="skill-tag">{course.skill}</span>
                    <h3>{course.title}</h3>
                    <p className="card-desc">{course.description}</p>

                    <div className="card-meta">
                      <span>⏱️ {course.duration}</span>
                      <span>⭐ {course.rating} / 5.0</span>
                      <span>🏫 {course.provider}</span>
                    </div>

                    <div className="modules-box">
                      <strong>Course Modules:</strong>
                      <ul>
                        {course.modules?.map((m, idx) => (
                          <li key={idx}>{m}</li>
                        ))}
                      </ul>
                    </div>

                    <button
                      className={`course-action-btn ${isEnrolled ? "enrolled" : ""}`}
                      onClick={() => handleEnrollToggle(course.id)}
                    >
                      {isEnrolled ? "✓ Enrolled" : "Enroll Now"}
                    </button>
                  </div>
                );
              })
            )}
          </div>
        )}
      </main>
    </div>
  );
}

export default Courses;
