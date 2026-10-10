import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { authFetch, clearAuth, getToken, getUser, setAuth } from "../api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => getUser());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = getToken();
    if (!token) {
      setUser(null);
      setLoading(false);
      return;
    }

    let mounted = true;

    authFetch("/auth/me")
      .then(async (response) => {
        if (!mounted) return;

        if (response.ok) {
          const profile = await response.json();
          const nextUser = {
            id: profile.id,
            name: profile.name,
            email: profile.email,
            role: profile.role || "STUDENT",
          };
          setAuth(token, nextUser);
          setUser(nextUser);
        } else {
          clearAuth();
          setUser(null);
        }
      })
      .catch(() => {
        if (mounted) {
          clearAuth();
          setUser(null);
        }
      })
      .finally(() => {
        if (mounted) setLoading(false);
      });

    return () => {
      mounted = false;
    };
  }, []);

  const login = async (credentials) => {
    const response = await authFetch("/auth/login", {
      method: "POST",
      body: JSON.stringify(credentials),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      return { ok: false, message: data.message || "Login failed." };
    }

    if (!data.token) {
      return { ok: false, message: "Authentication token was not returned by the backend." };
    }

    const nextUser = {
      id: data.id,
      name: data.name,
      email: data.email,
      role: data.role || "STUDENT",
    };

    setAuth(data.token, nextUser);
    setUser(nextUser);
    return { ok: true, user: nextUser, message: data.message || "Login successful." };
  };

  const register = async (payload) => {
    const response = await authFetch("/auth/register", {
      method: "POST",
      body: JSON.stringify(payload),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      return { ok: false, message: data.message || "Registration failed." };
    }

    if (data.token) {
      const nextUser = {
        id: data.id,
        name: data.name,
        email: data.email,
        role: data.role || "STUDENT",
      };
      setAuth(data.token, nextUser);
      setUser(nextUser);
    }

    return { ok: true, user: data, message: data.message || "Registration successful." };
  };

  const logout = () => {
    clearAuth();
    setUser(null);
  };

  const value = useMemo(
    () => ({ user, loading, login, register, logout }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}

export function RequireAuth({ children, requiredRole = null }) {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div className="page-loading">Checking session...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (requiredRole && user.role !== requiredRole) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

export function RequireGuest({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="page-loading">Loading...</div>;
  }

  if (user) {
    return <Navigate to={user.role === "ADMIN" ? "/admin" : "/dashboard"} replace />;
  }

  return children;
}
