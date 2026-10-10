import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { authFetch, clearAuth, getUser } from "../api";
import "./Dashboard.css";

function Dashboard() {
  const [user, setUser] = useState(null);
  const [profile, setProfile] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const currentUser = getUser();
    if (!currentUser) {
      navigate("/login");
    } else {
      setUser(currentUser);
      authFetch(`/profile/${currentUser.id}`)
        .then((res) => (res.ok ? res.json() : null))
        .then((data) => {
          if (data) setProfile(data);
        })
        .catch((err) => console.log("Profile fetch err:", err));
    }
  }, [navigate]);

  const handleLogout = () => {
    clearAuth();
    navigate("/login");
  };

  if (!user) {
    return null;
  }

  const skillCount = profile?.currentSkills
    ? profile.currentSkills.split(",").filter((s) => s.trim().length > 0).length
    : 0;

  return (
    <div className="dashboard-container">
      {/* Top Navigation */}
      <header className="dashboard-header">
        <div className="logo">
          SkillSphere <span>AI</span>
        </div>
        <div className="header-right">
          <Link to="/profile" className="profile-link-btn">
            👤 My Profile
          </Link>
          <span className="user-badge">
            {user.name} ({user.role})
          </span>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className="dashboard-main">
        <div className="welcome-banner">
          <h2>Welcome back, {user.name}! 👋</h2>
          <p>
            Your AI-powered personalized learning and career roadmap platform.
          </p>
        </div>

        {/* Quick Status Cards */}
        <div className="stats-grid">
          <div className="stat-card">
            <h4>Target Career</h4>
            <p className="stat-value active">
              {profile?.targetCareer || "Select Career"}
            </p>
            <span className="stat-sub">
              {profile?.experienceLevel || "Beginner"} level
            </span>
          </div>
          <div className="stat-card">
            <h4>Acquired Skills</h4>
            <p className="stat-value">{skillCount} Skills</p>
            <span className="stat-sub">
              {skillCount > 0 ? "Ready for Gap Analysis" : "No skills added yet"}
            </span>
          </div>
          <div className="stat-card">
            <h4>Education</h4>
            <p className="stat-value">
              {profile?.graduationYear ? `Class of ${profile.graduationYear}` : "Pending"}
            </p>
            <span className="stat-sub">{profile?.degree || "Update in profile"}</span>
          </div>
        </div>

        {/* Modules Overview Grid */}
        <h3 className="section-title">SkillSphere AI Learning Modules</h3>
        <div className="modules-grid">
          <Link to="/profile" className="module-card-link">
            <div className="module-card interactive">
              <div className="module-icon">🎯</div>
              <h4>Profile & Career Selection</h4>
              <p>Set your target job roles, aspirations, and manage current skillset.</p>
              <span className="module-tag ready">Open Module ➔</span>
            </div>
          </Link>

          <Link to="/skill-gap" className="module-card-link">
            <div className="module-card interactive">
              <div className="module-icon">⚡</div>
              <h4>Skills & Gap Analysis</h4>
              <p>Assess your current skillset and detect missing key skills.</p>
              <span className="module-tag ready">Run Gap Analysis ➔</span>
            </div>
          </Link>

          <Link to="/roadmap" className="module-card-link">
            <div className="module-card interactive">
              <div className="module-icon">🗺️</div>
              <h4>AI Recommendations & Roadmap</h4>
              <p>Step-by-step personalized curriculum tailored for your target role.</p>
              <span className="module-tag ready">View Roadmap ➔</span>
            </div>
          </Link>

          <Link to="/courses" className="module-card-link">
            <div className="module-card interactive">
              <div className="module-icon">📚</div>
              <h4>Courses & Resources</h4>
              <p>Explore hand-picked course modules, syllabus, and study resources.</p>
              <span className="module-tag ready">Browse Courses ➔</span>
            </div>
          </Link>

          <Link to="/assessments" className="module-card-link">
            <div className="module-card interactive">
              <div className="module-icon">📝</div>
              <h4>Skill Assessments & Quizzes</h4>
              <p>Validate your knowledge with timed tests, earn badges, and verify skills.</p>
              <span className="module-tag ready">Take Assessment ➔</span>
            </div>
          </Link>

          <div className="module-card">
            <div className="module-icon">📈</div>
            <h4>Progress Tracking</h4>
            <p>Track completion rate, quiz scores, and career readiness index.</p>
            <span className="module-tag upcoming">Upcoming</span>
          </div>
        </div>
      </main>
    </div>
  );
}

export default Dashboard;
