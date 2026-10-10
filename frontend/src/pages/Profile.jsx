import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { authFetch, clearAuth, getUser } from "../api";
import "./Profile.css";

function Profile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  // Floating Toast notification state
  const [toast, setToast] = useState({ show: false, message: "", type: "success" });

  // Inline feedback states
  const [skillFeedback, setSkillFeedback] = useState({ text: "", type: "" });
  const [saveStatus, setSaveStatus] = useState({ text: "", type: "" });

  // Profile form state
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [college, setCollege] = useState("");
  const [degree, setDegree] = useState("");
  const [graduationYear, setGraduationYear] = useState("");
  const [experienceLevel, setExperienceLevel] = useState("Beginner");
  const [targetCareer, setTargetCareer] = useState("Full Stack Java Developer");
  const [currentSkills, setCurrentSkills] = useState([]);
  const [newSkillInput, setNewSkillInput] = useState("");
  const [bio, setBio] = useState("");

  // Available career options loaded from backend
  const [careers, setCareers] = useState([]);

  const navigate = useNavigate();

  const triggerToast = (msg, type = "success") => {
    setToast({ show: true, message: msg, type });
    setTimeout(() => {
      setToast((prev) => ({ ...prev, show: false }));
    }, 4000);
  };

  useEffect(() => {
    let currentUser = getUser();
    if (!currentUser) {
      navigate("/login");
      return;
    }

    // Verify session & id
    if (!currentUser.id) {
      authFetch("/auth/me")
        .then((res) => (res.ok ? res.json() : null))
        .then((fresh) => {
          if (fresh && fresh.id) {
            currentUser = { ...currentUser, ...fresh };
            localStorage.setItem("user", JSON.stringify(currentUser));
            setUser(currentUser);
            loadCareerOptions();
            loadProfileData(currentUser.id);
          } else {
            navigate("/login");
          }
        })
        .catch(() => navigate("/login"));
      return;
    }

    setUser(currentUser);
    loadCareerOptions();
    loadProfileData(currentUser.id);
  }, [navigate]);

  const loadCareerOptions = async () => {
    try {
      const res = await authFetch("/careers");
      if (res.ok) {
        const data = await res.json();
        setCareers(data);
      }
    } catch (err) {
      console.error("Failed to load careers:", err);
    }
  };

  const loadProfileData = async (userId) => {
    setLoading(true);
    try {
      const res = await authFetch(`/profile/${userId}`);
      if (res.ok) {
        const data = await res.json();
        setFullName(data.fullName || "");
        setEmail(data.email || "");
        setPhone(data.phone || "");
        setCollege(data.college || "");
        setDegree(data.degree || "");
        setGraduationYear(data.graduationYear || "");
        setExperienceLevel(data.experienceLevel || "Beginner");
        setTargetCareer(data.targetCareer || "Full Stack Java Developer");
        setBio(data.bio || "");

        // Parse skills from comma-separated string
        if (data.currentSkills && data.currentSkills.trim()) {
          const list = data.currentSkills
            .split(",")
            .map((s) => s.trim())
            .filter((s) => s.length > 0);
          setCurrentSkills(list);
        } else {
          setCurrentSkills([]);
        }
      }
    } catch (err) {
      console.error("Failed to fetch profile:", err);
      setIsError(true);
      setMessage("Failed to load profile details from server.");
    } finally {
      setLoading(false);
    }
  };

  const handleAddSkill = async (skillToAdd) => {
    const raw = typeof skillToAdd === "string" ? skillToAdd : newSkillInput;
    const trimmed = raw.trim();

    if (!trimmed) {
      triggerToast("Please type a skill name first! (e.g. Python, Docker)", "warning");
      setSkillFeedback({ text: "⚠️ Please enter a skill name before clicking Add.", type: "warning" });
      return;
    }

    // Check duplicate case-insensitively
    if (currentSkills.some((s) => s.toLowerCase() === trimmed.toLowerCase())) {
      triggerToast(`"${trimmed}" is already in your skills list!`, "info");
      setSkillFeedback({ text: `ℹ️ "${trimmed}" is already added to your profile.`, type: "info" });
      setNewSkillInput("");
      return;
    }

    const updatedList = [...currentSkills, trimmed];
    setCurrentSkills(updatedList);
    setNewSkillInput("");

    // Persist immediately to MySQL backend
    if (user?.id) {
      try {
        const res = await authFetch(`/profile/${user.id}/skills`, {
          method: "POST",
          body: JSON.stringify({ skill: trimmed }),
        });

        if (res.ok) {
          triggerToast(`✅ "${trimmed}" added and saved to your profile! 🎉`, "success");
          setSkillFeedback({ text: `🎉 "${trimmed}" successfully added and saved to MySQL!`, type: "success" });
        } else {
          // Fallback to update profile
          await authFetch(`/profile/${user.id}`, {
            method: "PUT",
            body: JSON.stringify({
              fullName: fullName.trim(),
              phone: phone.trim(),
              college: college.trim(),
              degree: degree.trim(),
              graduationYear: graduationYear.trim(),
              experienceLevel,
              targetCareer,
              currentSkills: updatedList.join(", "),
              bio: bio.trim(),
            }),
          });
          triggerToast(`✅ "${trimmed}" added to your skills! 🎉`, "success");
          setSkillFeedback({ text: `✅ "${trimmed}" added to profile!`, type: "success" });
        }
      } catch (err) {
        console.error("Auto-save skill error:", err);
        triggerToast(`Added "${trimmed}". Click "Save Profile & Career Goal" below to sync with server.`, "info");
        setSkillFeedback({ text: `ℹ️ Added "${trimmed}". Click "Save Profile" below to persist.`, type: "info" });
      }
    } else {
      triggerToast(`Added "${trimmed}" to your skills list!`, "success");
      setSkillFeedback({ text: `✅ Added "${trimmed}"! Click Save Profile below to save.`, type: "success" });
    }
  };

  const handleRemoveSkill = async (skillToRemove) => {
    const updatedList = currentSkills.filter((s) => s !== skillToRemove);
    setCurrentSkills(updatedList);

    if (user?.id) {
      try {
        await authFetch(`/profile/${user.id}/skills/${encodeURIComponent(skillToRemove)}`, {
          method: "DELETE",
        });
        triggerToast(`🗑️ Removed "${skillToRemove}" from profile.`, "info");
        setSkillFeedback({ text: `🗑️ Removed "${skillToRemove}".`, type: "info" });
      } catch (err) {
        console.error("Remove skill error:", err);
        triggerToast(`Removed "${skillToRemove}" locally. Click Save Profile to apply changes.`, "info");
      }
    }
  };

  const handleSaveProfile = async (e) => {
    if (e && e.preventDefault) e.preventDefault();
    if (!user) return;

    setSaving(true);
    setMessage("");
    setIsError(false);
    setSaveStatus({ text: "Saving your profile to database...", type: "loading" });

    const payload = {
      fullName: fullName.trim(),
      phone: phone.trim(),
      college: college.trim(),
      degree: degree.trim(),
      graduationYear: graduationYear.trim(),
      experienceLevel,
      targetCareer,
      currentSkills: currentSkills.join(", "),
      bio: bio.trim(),
    };

    try {
      const res = await authFetch(`/profile/${user.id}`, {
        method: "PUT",
        body: JSON.stringify(payload),
      });

      const data = await res.json().catch(() => ({}));

      if (res.ok) {
        setIsError(false);
        setMessage("Profile and Career Goal updated successfully! 🎉");
        triggerToast("🎉 Profile & Career Goal saved successfully to MySQL database!", "success");
        setSaveStatus({ text: "✅ Profile and Career Goal saved successfully!", type: "success" });
        setSkillFeedback({ text: "✅ All skills and profile details are up to date in MySQL!", type: "success" });
      } else {
        setIsError(true);
        const errMsg = data.message || "Failed to update profile.";
        setMessage(errMsg);
        triggerToast(`⚠️ ${errMsg}`, "error");
        setSaveStatus({ text: `❌ ${errMsg}`, type: "error" });
      }
    } catch (err) {
      setIsError(true);
      setMessage("Server connection error while saving profile.");
      triggerToast("❌ Server connection error while saving profile.", "error");
      setSaveStatus({ text: "❌ Server connection error while saving profile.", type: "error" });
      console.error("Error saving profile:", err);
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = () => {
    clearAuth();
    navigate("/login");
  };

  if (!user || loading) {
    return (
      <div className="profile-loading">
        <div className="spinner"></div>
        <p>Loading your SkillSphere profile...</p>
      </div>
    );
  }

  // Find currently selected career object
  const selectedCareerObj = careers.find(
    (c) => c.title.toLowerCase() === targetCareer.toLowerCase()
  );

  return (
    <div className="profile-page">
      {/* Floating Toast Notification (Sticky Viewport Notification) */}
      {toast.show && (
        <div className={`floating-toast ${toast.type}`} role="status">
          <div className="toast-icon">
            {toast.type === "success" && "🎉"}
            {toast.type === "error" && "⚠️"}
            {toast.type === "warning" && "⚡"}
            {toast.type === "info" && "ℹ️"}
          </div>
          <div className="toast-content">{toast.message}</div>
          <button
            type="button"
            className="toast-close-btn"
            onClick={() => setToast((prev) => ({ ...prev, show: false }))}
            title="Dismiss notification"
          >
            ×
          </button>
        </div>
      )}

      {/* Top Navbar */}
      <header className="profile-header">
        <div className="header-brand">
          <Link to="/dashboard" className="brand-logo">
            SkillSphere <span>AI</span>
          </Link>
          <span className="brand-divider">/</span>
          <span className="brand-crumb">Profile & Career Goal</span>
        </div>

        <div className="header-actions">
          <Link to="/skill-gap" className="nav-link">
            ⚡ Skill Gap Analysis
          </Link>
          <Link to="/dashboard" className="nav-link">
            ⬅️ Back to Dashboard
          </Link>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="profile-content">
        <div className="profile-title-bar">
          <div>
            <h1>Student Profile & Career Selection 🎯</h1>
            <p>
              Keep your profile up to date to receive hyper-personalized skill
              gap analysis, AI roadmaps, and course recommendations.
            </p>
          </div>
        </div>

        {message && (
          <div
            className={`profile-alert ${isError ? "error" : "success"}`}
            role="status"
          >
            {message}
          </div>
        )}

        <form onSubmit={handleSaveProfile}>
          {/* Section 1: Academic & Personal Info */}
          <section className="profile-card">
            <h3>👤 Academic & Personal Information</h3>
            <div className="form-grid">
              <div className="form-group">
                <label>Full Name</label>
                <input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="e.g. John Doe"
                  required
                />
              </div>

              <div className="form-group">
                <label>Email (Account ID)</label>
                <input type="email" value={email} disabled />
              </div>

              <div className="form-group">
                <label>Phone Number</label>
                <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  placeholder="e.g. +91 9876543210"
                />
              </div>

              <div className="form-group">
                <label>College / University</label>
                <input
                  type="text"
                  value={college}
                  onChange={(e) => setCollege(e.target.value)}
                  placeholder="e.g. Hyderabad Institute of Technology"
                />
              </div>

              <div className="form-group">
                <label>Degree / Branch</label>
                <input
                  type="text"
                  value={degree}
                  onChange={(e) => setDegree(e.target.value)}
                  placeholder="e.g. B.Tech Computer Science"
                />
              </div>

              <div className="form-group">
                <label>Graduation Year</label>
                <input
                  type="text"
                  value={graduationYear}
                  onChange={(e) => setGraduationYear(e.target.value)}
                  placeholder="e.g. 2026"
                />
              </div>

              <div className="form-group full-width">
                <label>Current Experience Level</label>
                <select
                  value={experienceLevel}
                  onChange={(e) => setExperienceLevel(e.target.value)}
                >
                  <option value="Beginner">Beginner (1st / 2nd Year)</option>
                  <option value="Intermediate">Intermediate (Hands-on experience)</option>
                  <option value="Advanced">Advanced (Projects & Internships)</option>
                </select>
              </div>

              <div className="form-group full-width">
                <label>Short Bio</label>
                <textarea
                  rows="3"
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                  placeholder="Tell us about your learning passions and future goals..."
                ></textarea>
              </div>
            </div>
          </section>

          {/* Section 2: Career Selection */}
          <section className="profile-card">
            <div className="section-header">
              <h3>🚀 Target Career Role</h3>
              <span className="selected-career-badge">
                Selected: <strong>{targetCareer}</strong>
              </span>
            </div>
            <p className="section-desc">
              Choose your dream role. SkillSphere AI will benchmark your
              skills against this career path.
            </p>

            <div className="careers-grid">
              {careers.map((c) => {
                const isSelected =
                  targetCareer.toLowerCase() === c.title.toLowerCase();
                return (
                  <div
                    key={c.id}
                    className={`career-card ${isSelected ? "active" : ""}`}
                    onClick={() => {
                      setTargetCareer(c.title);
                      triggerToast(`🎯 Selected "${c.title}" as your target career!`, "info");
                    }}
                  >
                    <div className="career-header">
                      <span className="career-icon">{c.icon}</span>
                      {isSelected && <span className="check-badge">✓ Selected</span>}
                    </div>
                    <h4>{c.title}</h4>
                    <p>{c.description}</p>
                    <div className="required-skills-preview">
                      <small>Core Skills:</small>
                      <div className="mini-chips">
                        {c.requiredSkills.slice(0, 4).map((s, idx) => (
                          <span key={idx} className="mini-chip">
                            {s}
                          </span>
                        ))}
                        {c.requiredSkills.length > 4 && (
                          <span className="mini-chip more">
                            +{c.requiredSkills.length - 4} more
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </section>

          {/* Section 3: Current Skills */}
          <section className="profile-card">
            <div className="skills-card-header-row">
              <div>
                <h3>⚡ My Current Skills</h3>
                <p className="section-desc">
                  Add skills you already know. You can type them manually or click
                  recommended skills from your chosen career path.
                </p>
              </div>
              <button
                type="button"
                className="skills-quick-save-btn"
                onClick={handleSaveProfile}
                disabled={saving}
                title="Quickly save your current skills"
              >
                {saving ? "⏳ Saving..." : "💾 Quick Save Skills"}
              </button>
            </div>

            {/* Real-time Inline Feedback inside Skills Box */}
            {skillFeedback.text && (
              <div className={`skill-inline-feedback ${skillFeedback.type}`} role="status">
                {skillFeedback.text}
              </div>
            )}

            {/* Quick-add suggestions from target career */}
            {selectedCareerObj && (
              <div className="skill-suggestions">
                <span className="suggestions-label">
                  Suggested for {selectedCareerObj.title}:
                </span>
                <div className="suggestions-chips">
                  {selectedCareerObj.requiredSkills.map((sk) => {
                    const alreadyAdded = currentSkills.some(
                      (s) => s.toLowerCase() === sk.toLowerCase()
                    );
                    return (
                      <button
                        type="button"
                        key={sk}
                        className={`suggest-chip ${alreadyAdded ? "added" : ""}`}
                        disabled={alreadyAdded}
                        onClick={() => handleAddSkill(sk)}
                        title={alreadyAdded ? "Already in your skills" : `Click to add ${sk}`}
                      >
                        {alreadyAdded ? `✓ ${sk}` : `+ ${sk}`}
                      </button>
                    );
                  })}
                </div>
              </div>
            )}

            {/* Add custom skill input */}
            <div className="add-skill-box">
              <input
                type="text"
                placeholder="Type a skill (e.g. Python, Docker, Spring Boot) and press Enter or click Add"
                value={newSkillInput}
                onChange={(e) => {
                  setNewSkillInput(e.target.value);
                  if (skillFeedback.text) setSkillFeedback({ text: "", type: "" });
                }}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    e.preventDefault();
                    handleAddSkill();
                  }
                }}
              />
              <button
                type="button"
                className="add-btn"
                onClick={() => handleAddSkill()}
              >
                + Add Skill
              </button>
            </div>

            {/* Active Skills Header */}
            <div className="skills-summary-row">
              <span className="skills-count-badge">
                Active Skills ({currentSkills.length})
              </span>
              {currentSkills.length > 0 && (
                <span className="skills-hint">
                  💡 Click <strong>×</strong> on any skill chip to remove it.
                </span>
              )}
            </div>

            {/* Active Skills List */}
            <div className="active-skills">
              {currentSkills.length === 0 ? (
                <p className="no-skills-text">
                  No skills added yet. Click on the suggestions above or type your
                  skills and click <strong>+ Add Skill</strong>.
                </p>
              ) : (
                currentSkills.map((sk) => (
                  <span key={sk} className="skill-badge animate-badge">
                    {sk}
                    <button
                      type="button"
                      className="remove-chip-btn"
                      onClick={() => handleRemoveSkill(sk)}
                      title={`Remove ${sk}`}
                    >
                      ×
                    </button>
                  </span>
                ))
              )}
            </div>
          </section>

          {/* Bottom Save Action */}
          <div className="profile-footer-actions">
            {saveStatus.text && (
              <span className={`save-status-indicator ${saveStatus.type}`} role="status">
                {saveStatus.text}
              </span>
            )}
            <button
              type="submit"
              className="save-profile-btn"
              disabled={saving}
            >
              {saving ? "⏳ Saving Changes..." : "💾 Save Profile & Career Goal"}
            </button>
          </div>
        </form>
      </main>
    </div>
  );
}

export default Profile;
