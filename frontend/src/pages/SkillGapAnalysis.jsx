import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { authFetch, clearAuth, getUser } from "../api";
import "./SkillGapAnalysis.css";

function SkillGapAnalysis() {
  const [user, setUser] = useState(null);
  const [gapData, setGapData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const currentUser = getUser();
    if (!currentUser) {
      navigate("/login");
      return;
    }

    setUser(currentUser);
    fetchGapAnalysis(currentUser.id);
  }, [navigate]);

  const fetchGapAnalysis = async (userId) => {
    setLoading(true);
    setError("");
    try {
      const res = await authFetch(`/skill-gap/${userId}`);
      if (res.ok) {
        const data = await res.json();
        setGapData(data);
      } else {
        const err = await res.json().catch(() => ({}));
        setError(err.message || "Failed to analyze skill gap.");
      }
    } catch (err) {
      console.error("Skill gap error:", err);
      setError("Cannot connect to backend server on port 8080.");
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    clearAuth();
    navigate("/login");
  };

  if (!user || loading) {
    return (
      <div className="gap-loading">
        <div className="spinner"></div>
        <p>Analyzing your skillset against industry benchmarks...</p>
      </div>
    );
  }

  const score = gapData?.readinessScore || 0;
  const isHigh = score >= 80;
  const isModerate = score >= 45 && score < 80;

  return (
    <div className="gap-page">
      {/* Top Navbar */}
      <header className="gap-header">
        <div className="header-brand">
          <Link to="/dashboard" className="brand-logo">
            SkillSphere <span>AI</span>
          </Link>
          <span className="brand-divider">/</span>
          <span className="brand-crumb">Skill Gap Analysis</span>
        </div>

        <div className="header-actions">
          <Link to="/profile" className="nav-link">
            ✏️ Edit Skills & Career
          </Link>
          <Link to="/dashboard" className="nav-link">
            ⬅️ Dashboard
          </Link>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="gap-content">
        {/* Title Bar */}
        <div className="gap-title-bar">
          <div>
            <h1>AI Skill Gap Analysis ⚡</h1>
            <p>
              Real-time benchmark comparing your current skills with industry
              standards for <strong>{gapData?.targetCareer}</strong>.
            </p>
          </div>
          <Link to="/profile" className="change-career-btn">
            Change Target Career
          </Link>
        </div>

        {error && <div className="gap-alert error">{error}</div>}

        {/* Readiness Score Card */}
        <div className="score-hero-card">
          <div className="score-visual-col">
            <div
              className={`score-circle ${
                isHigh ? "high" : isModerate ? "moderate" : "low"
              }`}
            >
              <div className="score-number">{score}%</div>
              <div className="score-label">Role Readiness</div>
            </div>
          </div>

          <div className="score-details-col">
            <div className="score-badge-row">
              <span
                className={`level-badge ${
                  isHigh ? "high" : isModerate ? "moderate" : "low"
                }`}
              >
                {gapData?.readinessLevel}
              </span>
              <span className="career-target-pill">
                Target: {gapData?.targetCareer}
              </span>
            </div>

            <h3>
              {isHigh
                ? "Excellent Match! You are nearly job-ready."
                : isModerate
                ? "Good Foundation! Bridge a few missing skills to qualify."
                : "Skill Gap Detected! Focus on your recommended roadmap."}
            </h3>

            <div className="progress-bar-container">
              <div
                className={`progress-fill ${
                  isHigh ? "high" : isModerate ? "moderate" : "low"
                }`}
                style={{ width: `${score}%` }}
              ></div>
            </div>

            <div className="metrics-row">
              <div className="metric">
                <span className="metric-val matched">
                  {gapData?.matchedCount} / {gapData?.totalRequiredCount}
                </span>
                <span className="metric-lbl">Core Skills Matched</span>
              </div>
              <div className="metric">
                <span className="metric-val missing">
                  {gapData?.missingCount}
                </span>
                <span className="metric-lbl">Skills to Bridge</span>
              </div>
              <div className="metric">
                <span className="metric-val additional">
                  {gapData?.additionalSkills?.length || 0}
                </span>
                <span className="metric-lbl">Bonus Skills</span>
              </div>
            </div>
          </div>
        </div>

        {/* AI Insight Advice */}
        {gapData?.aiSummary && (
          <div className="ai-insight-box">
            <div className="ai-icon">🤖</div>
            <div className="ai-text">
              <h4>SkillSphere AI Recommendation Insight</h4>
              <p>{gapData.aiSummary}</p>
            </div>
          </div>
        )}

        {/* Detailed Skills Breakdown Grid */}
        <div className="breakdown-grid">
          {/* Missing Skills (The Gap) */}
          <div className="breakdown-card missing-card">
            <div className="card-header">
              <h3>🚨 Missing Skills (Your Gap)</h3>
              <span className="count-tag missing">
                {gapData?.missingCount} skills
              </span>
            </div>
            <p className="card-desc">
              These industry competencies are required for your dream role but
              have not been added yet.
            </p>

            <div className="skills-pill-list">
              {gapData?.missingSkills?.length === 0 ? (
                <div className="all-matched-banner">
                  🎉 No missing skills! You've matched all core requirements.
                </div>
              ) : (
                gapData?.missingSkills?.map((skill, idx) => (
                  <div key={idx} className="skill-item missing">
                    <div className="skill-name-col">
                      <span className="dot missing-dot"></span>
                      <strong>{skill}</strong>
                    </div>
                    <span className="priority-tag">High Priority</span>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Matched Skills */}
          <div className="breakdown-card matched-card">
            <div className="card-header">
              <h3>✅ Matched Core Skills</h3>
              <span className="count-tag matched">
                {gapData?.matchedCount} skills
              </span>
            </div>
            <p className="card-desc">
              Skills you currently possess that directly satisfy benchmark
              requirements for this career.
            </p>

            <div className="skills-pill-list">
              {gapData?.matchedSkills?.length === 0 ? (
                <div className="no-matched-text">
                  None of your current skills matched this role's core list.
                </div>
              ) : (
                gapData?.matchedSkills?.map((skill, idx) => (
                  <div key={idx} className="skill-item matched">
                    <div className="skill-name-col">
                      <span className="dot matched-dot"></span>
                      <strong>{skill}</strong>
                    </div>
                    <span className="verified-tag">Mastered</span>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Additional Skills */}
          <div className="breakdown-card additional-card">
            <div className="card-header">
              <h3>💡 Additional Skills</h3>
              <span className="count-tag additional">
                {gapData?.additionalSkills?.length || 0} skills
              </span>
            </div>
            <p className="card-desc">
              Complementary technical skills you possess that broaden your
              overall developer versatility.
            </p>

            <div className="skills-pill-list">
              {gapData?.additionalSkills?.length === 0 ? (
                <div className="no-matched-text">No additional skills listed.</div>
              ) : (
                gapData?.additionalSkills?.map((skill, idx) => (
                  <div key={idx} className="skill-item additional">
                    <div className="skill-name-col">
                      <span className="dot additional-dot"></span>
                      <strong>{skill}</strong>
                    </div>
                    <span className="bonus-tag">Bonus Skill</span>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>

        {/* Call to Action Footer */}
        <div className="gap-action-footer">
          <Link to="/profile" className="secondary-action-btn">
            ✏️ Update Skills in Profile
          </Link>
          <Link to="/roadmap" className="primary-action-btn">
            View AI Learning Roadmap & Courses ➔
          </Link>
        </div>
      </main>
    </div>
  );
}

export default SkillGapAnalysis;
