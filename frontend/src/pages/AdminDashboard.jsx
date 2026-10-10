import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authFetch, clearAuth, getUser } from "../api";
import "./Dashboard.css";

function AdminDashboard() {
  const [user, setUser] = useState(null);
  const [stats, setStats] = useState(null);
  const [students, setStudents] = useState([]);
  const [skills, setSkills] = useState([]);
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("users"); // "users", "skills", "courses"

  // Skill Form
  const [newSkillName, setNewSkillName] = useState("");
  const [newSkillCategory, setNewSkillCategory] = useState("Framework");
  const [newSkillDesc, setNewSkillDesc] = useState("");

  // Course Form
  const [newCourseTitle, setNewCourseTitle] = useState("");
  const [newCourseSkill, setNewCourseSkill] = useState("");
  const [newCourseLevel, setNewCourseLevel] = useState("Beginner");
  const [newCourseDuration, setNewCourseDuration] = useState("10 Hours");
  const [newCourseDesc, setNewCourseDesc] = useState("");

  const [actionMessage, setActionMessage] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const currentUser = getUser();
    if (!currentUser) {
      navigate("/login");
      return;
    }

    if (currentUser.role !== "ADMIN") {
      navigate("/dashboard");
      return;
    }

    setUser(currentUser);
    loadAllAdminData();
  }, [navigate]);

  const loadAllAdminData = async () => {
    setLoading(true);
    try {
      const [statsRes, usersRes, skillsRes, coursesRes] = await Promise.all([
        authFetch("/admin/stats"),
        authFetch("/admin/users"),
        authFetch("/admin/skills"),
        authFetch("/admin/courses"),
      ]);

      if (statsRes.ok) setStats(await statsRes.json());
      if (usersRes.ok) setStudents(await usersRes.json());
      if (skillsRes.ok) setSkills(await skillsRes.json());
      if (coursesRes.ok) setCourses(await coursesRes.json());
    } catch (err) {
      console.error("Failed to load admin data:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId, userEmail) => {
    if (!window.confirm(`Are you sure you want to remove user "${userEmail}"?`)) {
      return;
    }

    try {
      const res = await authFetch(`/admin/users/${userId}`, { method: "DELETE" });
      if (res.ok) {
        setActionMessage(`User "${userEmail}" removed successfully.`);
        loadAllAdminData();
      } else {
        const err = await res.json().catch(() => ({}));
        alert(err.message || "Failed to delete user.");
      }
    } catch (err) {
      alert("Error deleting user: " + err.message);
    }
  };

  const handleAddSkill = async (e) => {
    e.preventDefault();
    if (!newSkillName.trim()) return;

    try {
      const res = await authFetch("/admin/skills", {
        method: "POST",
        body: JSON.stringify({
          name: newSkillName.trim(),
          category: newSkillCategory,
          description: newSkillDesc.trim(),
        }),
      });

      if (res.ok) {
        setActionMessage(`Skill "${newSkillName}" created in MySQL! 🎉`);
        setNewSkillName("");
        setNewSkillDesc("");
        loadAllAdminData();
      } else {
        const err = await res.json().catch(() => ({}));
        alert(err.message || "Failed to create skill.");
      }
    } catch (err) {
      alert("Error creating skill: " + err.message);
    }
  };

  const handleAddCourse = async (e) => {
    e.preventDefault();
    if (!newCourseTitle.trim() || !newCourseSkill.trim()) return;

    try {
      const res = await authFetch("/admin/courses", {
        method: "POST",
        body: JSON.stringify({
          title: newCourseTitle.trim(),
          skill: newCourseSkill.trim(),
          level: newCourseLevel,
          duration: newCourseDuration,
          description: newCourseDesc.trim(),
          provider: "SkillSphere Academy",
          icon: "🎓",
          modules: ["Introduction", "Core Concepts", "Hands-on Project"],
        }),
      });

      if (res.ok) {
        setActionMessage(`Course "${newCourseTitle}" added to catalog! 🚀`);
        setNewCourseTitle("");
        setNewCourseSkill("");
        setNewCourseDesc("");
        loadAllAdminData();
      } else {
        const err = await res.json().catch(() => ({}));
        alert(err.message || "Failed to create course.");
      }
    } catch (err) {
      alert("Error adding course: " + err.message);
    }
  };

  const handleDeleteCourse = async (courseId, courseTitle) => {
    if (!window.confirm(`Delete course "${courseTitle}" from catalog?`)) return;

    try {
      const res = await authFetch(`/admin/courses/${courseId}`, { method: "DELETE" });
      if (res.ok) {
        setActionMessage(`Course "${courseTitle}" removed.`);
        loadAllAdminData();
      }
    } catch (err) {
      alert("Error deleting course: " + err.message);
    }
  };

  const handleLogout = () => {
    clearAuth();
    navigate("/login");
  };

  if (!user) return null;

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="logo">
          SkillSphere <span>AI (Admin Panel)</span>
        </div>
        <div className="header-right">
          <span
            className="user-badge"
            style={{ background: "#fef3c7", color: "#92400e" }}
          >
            🛡️ {user.name} ({user.role})
          </span>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="dashboard-main">
        <div
          className="welcome-banner"
          style={{
            background: "linear-gradient(135deg, #1e293b 0%, #0f172a 100%)",
          }}
        >
          <h2>Platform Management & Analytics 🛡️</h2>
          <p>
            Manage technical skill taxonomy, course catalog, registered students,
            and view real-time system metrics from MySQL.
          </p>
        </div>

        {/* Live Admin Statistics */}
        <div className="stats-grid">
          <div className="stat-card">
            <h4>Registered Accounts</h4>
            <p className="stat-value active">{stats?.totalUsers ?? "--"}</p>
            <span className="stat-sub">{stats?.totalProfiles ?? "--"} student profiles</span>
          </div>
          <div className="stat-card">
            <h4>Skill Taxonomy</h4>
            <p className="stat-value">{stats?.totalSkills ?? "--"} Skills</p>
            <span className="stat-sub">{stats?.totalCareers ?? "--"} Career benchmarks</span>
          </div>
          <div className="stat-card">
            <h4>Catalog & Assessments</h4>
            <p className="stat-value">{stats?.totalCourses ?? "--"} Courses</p>
            <span className="stat-sub">{stats?.totalAssessmentsCompleted ?? "--"} Tests taken</span>
          </div>
        </div>

        {actionMessage && (
          <div
            style={{
              padding: "0.75rem 1rem",
              background: "#dcfce7",
              color: "#166534",
              borderRadius: "8px",
              marginBottom: "1.5rem",
              fontWeight: "500",
            }}
          >
            ✓ {actionMessage}
          </div>
        )}

        {/* Admin Navigation Tabs */}
        <div
          style={{
            display: "flex",
            gap: "0.5rem",
            marginBottom: "1.5rem",
            borderBottom: "2px solid #e2e8f0",
            paddingBottom: "0.5rem",
          }}
        >
          <button
            onClick={() => setActiveTab("users")}
            style={{
              padding: "0.5rem 1rem",
              borderRadius: "6px",
              border: "none",
              cursor: "pointer",
              fontWeight: "600",
              background: activeTab === "users" ? "#2563eb" : "#f1f5f9",
              color: activeTab === "users" ? "#ffffff" : "#475569",
            }}
          >
            👥 Registered Users ({students.length})
          </button>
          <button
            onClick={() => setActiveTab("skills")}
            style={{
              padding: "0.5rem 1rem",
              borderRadius: "6px",
              border: "none",
              cursor: "pointer",
              fontWeight: "600",
              background: activeTab === "skills" ? "#2563eb" : "#f1f5f9",
              color: activeTab === "skills" ? "#ffffff" : "#475569",
            }}
          >
            🏷️ Skills Taxonomy ({skills.length})
          </button>
          <button
            onClick={() => setActiveTab("courses")}
            style={{
              padding: "0.5rem 1rem",
              borderRadius: "6px",
              border: "none",
              cursor: "pointer",
              fontWeight: "600",
              background: activeTab === "courses" ? "#2563eb" : "#f1f5f9",
              color: activeTab === "courses" ? "#ffffff" : "#475569",
            }}
          >
            📚 Courses ({courses.length})
          </button>
        </div>

        {/* Tab 1: Users */}
        {activeTab === "users" && (
          <div
            style={{
              background: "#ffffff",
              borderRadius: "12px",
              padding: "1.5rem",
              boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
              overflowX: "auto",
            }}
          >
            <h3 style={{ marginBottom: "1rem" }}>Platform Accounts in MySQL</h3>
            <table
              style={{
                width: "100%",
                borderCollapse: "collapse",
                textAlign: "left",
              }}
            >
              <thead>
                <tr style={{ borderBottom: "2px solid #e2e8f0", color: "#475569" }}>
                  <th style={{ padding: "0.75rem" }}>ID</th>
                  <th style={{ padding: "0.75rem" }}>Name</th>
                  <th style={{ padding: "0.75rem" }}>Email</th>
                  <th style={{ padding: "0.75rem" }}>Role</th>
                  <th style={{ padding: "0.75rem" }}>Action</th>
                </tr>
              </thead>
              <tbody>
                {students.map((s) => (
                  <tr key={s.id} style={{ borderBottom: "1px solid #f1f5f9" }}>
                    <td style={{ padding: "0.75rem", color: "#64748b" }}>#{s.id}</td>
                    <td style={{ padding: "0.75rem", fontWeight: "600", color: "#1e293b" }}>
                      {s.name}
                    </td>
                    <td style={{ padding: "0.75rem", color: "#475569" }}>{s.email}</td>
                    <td style={{ padding: "0.75rem" }}>
                      <span
                        style={{
                          padding: "0.25rem 0.5rem",
                          borderRadius: "6px",
                          fontSize: "0.8rem",
                          fontWeight: "600",
                          background: s.role === "ADMIN" ? "#fef3c7" : "#e0e7ff",
                          color: s.role === "ADMIN" ? "#92400e" : "#3730a3",
                        }}
                      >
                        {s.role}
                      </span>
                    </td>
                    <td style={{ padding: "0.75rem" }}>
                      {s.email !== "admin@skillsphere.com" && s.id !== user.id && (
                        <button
                          onClick={() => handleDeleteUser(s.id, s.email)}
                          style={{
                            padding: "0.3rem 0.6rem",
                            background: "#fee2e2",
                            color: "#dc2626",
                            border: "none",
                            borderRadius: "4px",
                            cursor: "pointer",
                            fontWeight: "500",
                          }}
                        >
                          Remove
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Tab 2: Skills */}
        {activeTab === "skills" && (
          <div>
            <div
              style={{
                background: "#ffffff",
                borderRadius: "12px",
                padding: "1.5rem",
                boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
                marginBottom: "1.5rem",
              }}
            >
              <h3>➕ Add New Skill to Taxonomy</h3>
              <form
                onSubmit={handleAddSkill}
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr 1.5fr auto",
                  gap: "1rem",
                  marginTop: "1rem",
                }}
              >
                <input
                  type="text"
                  placeholder="Skill Name (e.g. Flutter)"
                  value={newSkillName}
                  onChange={(e) => setNewSkillName(e.target.value)}
                  required
                  style={{ padding: "0.6rem", borderRadius: "6px", border: "1px solid #cbd5e1" }}
                />
                <select
                  value={newSkillCategory}
                  onChange={(e) => setNewSkillCategory(e.target.value)}
                  style={{ padding: "0.6rem", borderRadius: "6px", border: "1px solid #cbd5e1" }}
                >
                  <option value="Programming Language">Programming Language</option>
                  <option value="Framework">Framework</option>
                  <option value="Database">Database</option>
                  <option value="DevOps">DevOps</option>
                  <option value="AI / ML">AI / ML</option>
                  <option value="Cybersecurity">Cybersecurity</option>
                </select>
                <input
                  type="text"
                  placeholder="Description"
                  value={newSkillDesc}
                  onChange={(e) => setNewSkillDesc(e.target.value)}
                  style={{ padding: "0.6rem", borderRadius: "6px", border: "1px solid #cbd5e1" }}
                />
                <button
                  type="submit"
                  style={{
                    padding: "0.6rem 1.2rem",
                    background: "#16a34a",
                    color: "white",
                    border: "none",
                    borderRadius: "6px",
                    fontWeight: "600",
                    cursor: "pointer",
                  }}
                >
                  Add Skill
                </button>
              </form>
            </div>

            <div
              style={{
                background: "#ffffff",
                borderRadius: "12px",
                padding: "1.5rem",
                boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))",
                gap: "1rem",
              }}
            >
              {skills.map((s) => (
                <div
                  key={s.id}
                  style={{
                    padding: "0.75rem",
                    borderRadius: "8px",
                    border: "1px solid #e2e8f0",
                    background: "#f8fafc",
                  }}
                >
                  <h4 style={{ margin: "0 0 0.25rem 0", color: "#1e293b" }}>{s.name}</h4>
                  <span
                    style={{
                      fontSize: "0.75rem",
                      background: "#e0e7ff",
                      color: "#3730a3",
                      padding: "0.2rem 0.5rem",
                      borderRadius: "4px",
                      fontWeight: "600",
                    }}
                  >
                    {s.category}
                  </span>
                  {s.description && (
                    <p style={{ margin: "0.5rem 0 0 0", fontSize: "0.8rem", color: "#64748b" }}>
                      {s.description}
                    </p>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Tab 3: Courses */}
        {activeTab === "courses" && (
          <div>
            <div
              style={{
                background: "#ffffff",
                borderRadius: "12px",
                padding: "1.5rem",
                boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
                marginBottom: "1.5rem",
              }}
            >
              <h3>➕ Add Course to Catalog</h3>
              <form
                onSubmit={handleAddCourse}
                style={{
                  display: "grid",
                  gridTemplateColumns: "1.5fr 1fr 1fr 1fr auto",
                  gap: "0.75rem",
                  marginTop: "1rem",
                }}
              >
                <input
                  type="text"
                  placeholder="Course Title"
                  value={newCourseTitle}
                  onChange={(e) => setNewCourseTitle(e.target.value)}
                  required
                  style={{ padding: "0.6rem", borderRadius: "6px", border: "1px solid #cbd5e1" }}
                />
                <input
                  type="text"
                  placeholder="Primary Skill (e.g. React)"
                  value={newCourseSkill}
                  onChange={(e) => setNewCourseSkill(e.target.value)}
                  required
                  style={{ padding: "0.6rem", borderRadius: "6px", border: "1px solid #cbd5e1" }}
                />
                <select
                  value={newCourseLevel}
                  onChange={(e) => setNewCourseLevel(e.target.value)}
                  style={{ padding: "0.6rem", borderRadius: "6px", border: "1px solid #cbd5e1" }}
                >
                  <option value="Beginner">Beginner</option>
                  <option value="Intermediate">Intermediate</option>
                  <option value="Advanced">Advanced</option>
                </select>
                <input
                  type="text"
                  placeholder="Duration (e.g. 12 Hours)"
                  value={newCourseDuration}
                  onChange={(e) => setNewCourseDuration(e.target.value)}
                  style={{ padding: "0.6rem", borderRadius: "6px", border: "1px solid #cbd5e1" }}
                />
                <button
                  type="submit"
                  style={{
                    padding: "0.6rem 1.2rem",
                    background: "#16a34a",
                    color: "white",
                    border: "none",
                    borderRadius: "6px",
                    fontWeight: "600",
                    cursor: "pointer",
                  }}
                >
                  Add Course
                </button>
              </form>
            </div>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))",
                gap: "1rem",
              }}
            >
              {courses.map((c) => (
                <div
                  key={c.id}
                  style={{
                    background: "#ffffff",
                    borderRadius: "10px",
                    padding: "1rem",
                    boxShadow: "0 2px 4px rgba(0,0,0,0.06)",
                    border: "1px solid #e2e8f0",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "space-between",
                  }}
                >
                  <div>
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                      <span style={{ fontSize: "1.5rem" }}>{c.icon || "📘"}</span>
                      <span
                        style={{
                          fontSize: "0.75rem",
                          padding: "0.2rem 0.5rem",
                          background: "#e2e8f0",
                          borderRadius: "4px",
                          fontWeight: "600",
                        }}
                      >
                        {c.level}
                      </span>
                    </div>
                    <h4 style={{ margin: "0.5rem 0 0.25rem 0", color: "#1e293b" }}>{c.title}</h4>
                    <p style={{ margin: "0", fontSize: "0.85rem", color: "#64748b" }}>
                      Skill: <strong>{c.skill}</strong> • {c.duration}
                    </p>
                  </div>
                  <button
                    onClick={() => handleDeleteCourse(c.id, c.title)}
                    style={{
                      marginTop: "1rem",
                      padding: "0.4rem",
                      background: "#fee2e2",
                      color: "#dc2626",
                      border: "none",
                      borderRadius: "6px",
                      cursor: "pointer",
                      fontWeight: "500",
                      width: "100%",
                    }}
                  >
                    Delete Course
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default AdminDashboard;
