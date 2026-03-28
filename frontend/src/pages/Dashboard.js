import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { bookingApi, meetingApi } from '../api/api';

export default function Dashboard() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [meetings, setMeetings] = useState([]);

  useEffect(() => {
    bookingApi.getMyBookings().then(r => setBookings(r.data)).catch(() => {});
    if (user.role === 'MANAGER' || user.role === 'ADMIN') {
      meetingApi.getMyMeetings().then(r => setMeetings(r.data)).catch(() => {});
    }
  }, [user.role]);

  const upcomingBookings = bookings
    .filter(b => b.status === 'CONFIRMED' && b.bookingDate > new Date().toISOString().split('T')[0])
    .slice(0, 5);

  const upcomingMeetings = meetings
    .filter(m => m.status === 'CONFIRMED' && m.meetingDate > new Date().toISOString().split('T')[0])
    .slice(0, 5);

  return (
    <div>
      <h1 className="page-title">
        Welcome back, {user.fullName || user.fullname}!
        <div className="page-subtitle">Here's your booking overview</div>
      </h1>

      <div className="grid-3">
        <Link to="/book" className="card dashboard-action">
          <div className="action-icon">📅</div>
          <h3>Book a Seat</h3>
          <p>Reserve your workspace for the day</p>
        </Link>

        {(user.role === 'MANAGER' || user.role === 'ADMIN') && (
          <Link to="/meetings" className="card dashboard-action">
            <div className="action-icon">👥</div>
            <h3>Book Meeting</h3>
            <p>Reserve a circular desk for your team</p>
          </Link>
        )}

        {user.role === 'ADMIN' && (
          <Link to="/admin" className="card dashboard-action">
            <div className="action-icon">⚙️</div>
            <h3>Admin Panel</h3>
            <p>Manage floors, rooms, and desks</p>
          </Link>
        )}

        <Link to="/my-bookings" className="card dashboard-action">
          <div className="action-icon">📋</div>
          <h3>My Bookings</h3>
          <p>View and manage your reservations</p>
        </Link>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginTop: '24px' }}>
        <div className="card">
          <h3 style={{ marginBottom: '16px' }}>Upcoming Seat Bookings</h3>
          {upcomingBookings.length === 0 ? (
            <p style={{ color: 'var(--gray-400)', fontSize: '14px' }}>No upcoming bookings</p>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Seat</th>
                    <th>Floor</th>
                  </tr>
                </thead>
                <tbody>
                  {upcomingBookings.map(b => (
                    <tr key={b.id}>
                      <td>{b.bookingDate}</td>
                      <td>{b.startTime} - {b.endTime}</td>
                      <td>{b.seatLabel}</td>
                      <td>Floor {b.floorNumber} {b.roomName}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        <div className="card">
          <h3 style={{ marginBottom: '16px' }}>Upcoming Meetings</h3>
          {upcomingMeetings.length === 0 ? (
            <p style={{ color: 'var(--gray-400)', fontSize: '14px' }}>
              {(user.role === 'MANAGER' || user.role === 'ADMIN') ? 'No upcoming meetings' : 'Only managers can book meetings'}
            </p>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Title</th>
                    <th>Seats</th>
                  </tr>
                </thead>
                <tbody>
                  {upcomingMeetings.map(m => (
                    <tr key={m.id}>
                      <td>{m.meetingDate}</td>
                      <td>{m.startTime} - {m.endTime}</td>
                      <td>{m.title}</td>
                      <td>{m.seatCount} seats</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      <style>{`
        .dashboard-action {
          text-decoration: none;
          color: inherit;
          text-align: center;
          transition: transform 0.2s, box-shadow 0.2s;
          cursor: pointer;
        }
        .dashboard-action:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-lg);
        }
        .action-icon {
          font-size: 36px;
          margin-bottom: 12px;
        }
        .dashboard-action h3 {
          font-size: 16px;
          margin-bottom: 4px;
        }
        .dashboard-action p {
          font-size: 13px;
          color: var(--gray-500);
        }
      `}</style>
    </div>
  );
}