import { createContext, useEffect, useMemo, useState } from "react";
import { loginUser, registerUser } from "../api/authApi";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedUser = localStorage.getItem("currentUser");
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    const response = await loginUser(credentials);
    localStorage.setItem("accessToken", response.accessToken);
    localStorage.setItem("currentUser", JSON.stringify(response.user));
    setUser(response.user);
    return response.user;
  };

  const register = async (payload) => registerUser(payload);

  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("currentUser");
    setUser(null);
  };

  const value = useMemo(
    () => ({
      user,
      loading,
      isAuthenticated: Boolean(user),
      login,
      register,
      logout,
    }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
