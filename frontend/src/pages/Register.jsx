import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { setAuth, API_BASE } from "../api";
import "./Register.css";

function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("STUDENT");
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setMessage("");
    setIsError(false);
    setLoading(true);

    try {
      const response = await fetch(`${API_BASE}/auth/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: name.trim(),
          email: email.trim(),
          password,
          role,
        }),
      });

      const data = await response.json().catch(() => ({}));

      if (response.ok) {
        setIsError(false);
        setMessage("Account created successfully! Logging you in...");

        // Auto-login with returned token
        if (data.token) {
          setAuth(data.token, {
            id: data.id,
            name: data.name,
            email: data.email,
            role: data.role || role,
          });

          setTimeout(() => {
            if (data.role === "ADMIN") {
              navigate("/admin");
            } else {
              navigate("/profile");
            }
          }, 1000);
        } else {
          setTimeout(() => {
            navigate("/login");
          }, 1200);
        }
      } else if (response.status === 409) {
        setIsError(true);
        setMessage("This email is already registered. Please login instead.");
      } else {
        setIsError(true);
        setMessage(data.message || "Registration failed. Please try again.");
      }
    } catch (error) {
      setIsError(true);
      setMessage(
        "Cannot connect to the backend server. Make sure Spring Boot is running on port 8080."
      );
      console.error("Registration request failed:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <div className="register-card">
        <h1>
          SkillSphere <span>AI</span>
        </h1>

        <h2>Create Account</h2>
        <p>Start your AI-powered skill gap analysis & learning journey</p>

        <form onSubmit={handleRegister}>
          <label htmlFor="register-name">Full Name</label>
          <input
            id="register-name"
            type="text"
            placeholder="Enter your full name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />

          <label htmlFor="register-email">Email</label>
          <input
            id="register-email"
            type="email"
            placeholder="Enter your college or personal email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <label htmlFor="register-password">Password</label>
          <input
            id="register-password"
            type="password"
            placeholder="Create a strong password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <label htmlFor="register-role">I am a</label>
          <select
            id="register-role"
            value={role}
            onChange={(e) => setRole(e.target.value)}
            style={{
              width: "100%",
              padding: "0.75rem",
              borderRadius: "8px",
              border: "1px solid #cbd5e1",
              marginBottom: "1rem",
              fontSize: "0.95rem",
              backgroundColor: "#f8fafc",
            }}
          >
            <option value="STUDENT">Student / Learner</option>
            <option value="ADMIN">Platform Administrator</option>
          </select>

          <button type="submit" disabled={loading}>
            {loading ? "Creating Account..." : "Create Account"}
          </button>
        </form>

        {message && (
          <p
            className={`auth-message ${isError ? "error" : "success"}`}
            role="status"
          >
            {message}
          </p>
        )}

        <p className="login-text">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;