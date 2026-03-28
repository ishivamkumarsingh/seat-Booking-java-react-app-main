import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export default function Navbar() {
  const { user, logout } = useAuth();
  const location = useLocation();

  const isActive = (path) => location.pathname.startsWith(path);

  return (
    <nav className="navbar">
      <div className="navbar-inner">

        <Link to="/dashboard" className="navbar-brand">
          <span className="brand-icon">💺</span>
          SeatBook
        </Link>

        <div className="navbar-links">
          <Link
            to="/dashboard"
            className={`nav-link ${isActive('/dashboard') ? 'active' : ''}`}
          >
            Dashboard
          </Link>

          <Link
            to="/book"
            className={`nav-link ${isActive('/book') ? 'active' : ''}`}
          >
            Book Seat
          </Link>

          {(user?.role === 'MANAGER' || user?.role === 'ADMIN') && (
            <Link
              to="/meetings"
              className={`nav-link ${isActive('/meetings') ? 'active' : ''}`}
            >
              Meetings
            </Link>
          )}

          <Link
            to="/my-bookings"
            className={`nav-link ${isActive('/my-bookings') ? 'active' : ''}`}
          >
            My Bookings
          </Link>

          {user?.role === 'ADMIN' && (
            <Link
              to="/admin"
              className={`nav-link ${isActive('/admin') ? 'active' : ''}`}
            >
              Admin
            </Link>
          )}
        </div>

        <div className="navbar-user">
          <div className="user-info">
            <span className="user-name">{user?.fullName}</span>
            <span className={`badge badge-${user?.role?.toLowerCase()}`}>
              {user?.role}
            </span>
          </div>

          <button
            onClick={logout}
            className="btn btn-outline btn-sm"
          >
            Logout
          </button>
        </div>

      </div>
    </nav>
  );
}