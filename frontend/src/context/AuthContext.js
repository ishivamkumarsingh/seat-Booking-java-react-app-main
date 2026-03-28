import React, { createContext, useContext, useState, useCallback } from 'react';
import { authApi } from '../api/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => { 
    const stored = localStorage.getItem('auth'); 
    return stored ? JSON.parse(stored) : null; 
  });
  
  const login = useCallback(async (username, password) => {
    const res = await authApi.login({username, password}); 
    const userData = res.data; 
    localStorage.setItem('auth', JSON.stringify(userData)); 
    setUser(userData);
    return userData;
  }, []);
  
  const register = useCallback(async (data) => { 
    const res = await authApi.register(data);
    const userData = res.data;
    localStorage.setItem('auth', JSON.stringify(userData)); 
    setUser(userData);
    return userData;
  }, []);
  
  const logout = useCallback(() => {
    localStorage.removeItem('auth');
    setUser(null);
  }, []);
  
  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
}