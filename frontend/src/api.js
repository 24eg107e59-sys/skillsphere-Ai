// Centralized API client and JWT authentication manager for SkillSphere AI

export const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const getToken = () => {
  return localStorage.getItem("token");
};

export const getUser = () => {
  const userStr = localStorage.getItem("user");
  if (!userStr) return null;
  try {
    return JSON.parse(userStr);
  } catch {
    return null;
  }
};

export const setAuth = (token, user) => {
  if (token) localStorage.setItem("token", token);
  if (user) localStorage.setItem("user", JSON.stringify(user));
};

export const clearAuth = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
};

export const isAuthenticated = () => {
  return Boolean(getToken());
};

/**
 * Standard fetch wrapper that automatically adds:
 * - Content-Type: application/json
 * - Authorization: Bearer <token>
 * - Automatic redirect to /login if token is expired (401)
 */
export const authFetch = async (endpoint, options = {}) => {
  const token = getToken();
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const url = endpoint.startsWith("http") ? endpoint : `${API_BASE}${endpoint}`;

  const response = await fetch(url, {
    ...options,
    headers,
  });

  if (response.status === 401 && !endpoint.includes("/auth/login")) {
    clearAuth();
    if (!window.location.pathname.includes("/login")) {
      window.location.href = "/login?expired=true";
    }
  }

  return response;
};
