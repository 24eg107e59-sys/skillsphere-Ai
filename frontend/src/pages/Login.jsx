import { useState, useEffect } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { setAuth, API_BASE } from "../api";
import "./Login.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get("expired") === "true") {
      setIsError(true);
      setMessage("Your session has expired. Please log in again.");
    }
  }, [location]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage("");
    setIsError(false);
    setLoading(true);

    try {
      const response = await fetch(`${API_BASE}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: email.trim(),
          password,
        }),
      });

      const data = await response.json().catch(() => ({}));

      if (response.ok && data.token) {
        setMessage(data.message || "Login successful!");
        setIsError(false);

        // Store JWT token and user info
        setAuth(data.token, {
          id: data.id,
          name: data.name,
          email: data.email,
          role: data.role || "STUDENT",
        });

        // Redirect based on role
        setTimeout(() => {
          if (data.role === "ADMIN") {
            navigate("/admin");
          } else {
            navigate("/dashboard");
          }
        }, 800);
      } else if (response.status === 401) {
        setIsError(true);
        setMessage(data.message || "Invalid email or password.");
      } else {
        setIsError(true);
        setMessage(data.message || "Login failed. Please try again.");
      }
    } catch (error) {
      setIsError(true);
      setMessage(
        "Cannot connect to the backend server. Make sure Spring Boot is running on port 8080."
      );
      console.error("Login request error:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1>
          SkillSphere <span>AI</span>
        </h1>

        <h2>Welcome Back</h2>
        <p>Login with your credentials to access your personalized roadmap</p>

        <form onSubmit={handleLogin}>
          <label htmlFor="login-email">Email</label>
          <input
            id="login-email"
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <label htmlFor="login-password">Password</label>
          <input
            id="login-password"
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
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

        <p className="register-text">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}

export default Login;