import { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });
  const [loading, setLoading] = useState(false);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const { data } = await authApi.login({ email, password });
      const auth = data.data;
      localStorage.setItem('token', auth.token);
      const userData = { id: auth.id, name: auth.name, email: auth.email, role: auth.role };
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      return auth;
    } finally {
      setLoading(false);
    }
  };

  const register = async (name, email, password) => {
    setLoading(true);
    try {
      const { data } = await authApi.register({ name, email, password });
      const auth = data.data;
      localStorage.setItem('token', auth.token);
      const userData = { id: auth.id, name: auth.name, email: auth.email, role: auth.role };
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      return auth;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const isAdmin = user?.role === 'ADMIN';

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, isAdmin, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};
