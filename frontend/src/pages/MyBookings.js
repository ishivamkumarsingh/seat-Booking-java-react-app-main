import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { bookingApi, meetingApi } from '../api/api';

export default function MyBookings() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [meetings, setMeetings] = useState([]);
  const [tab, setTab] = useState('bookings');
  const [error, setError] = useState('');

  const loadData = async () => {
    try {
      const bRes = await bookingApi.getMyBookings();
      setBookings(bRes.data);
    } catch {/* ignore */ }
    
    if (user.role === 'MANAGER' || user.role === 'ADMIN') {
      try {
        const mRes = await meetingApi.getMyMeetings();
        setMeetings(mRes.data);
      } catch {/* ignore */ }
    }
  };

  useEffect(() => { loadData(); }, []); // eslint-disable-line

  const handleCancelBooking = async (id) => {
    if (!window.confirm('Cancel this booking?')) return;
    setError('');
    try {
      await bookingApi.cancelBooking(id);
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to cancel');
    }
  };

  const handleCancelMeeting = async (id) => {
    if (!window.confirm('Cancel this meeting?')) return;
    setError('');
    try {
      await meetingApi.cancelMeeting(id);
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to cancel');
    }
  };

  return (
    <div>
      <h1 className="page-title">
        My Bookings
        <div className="page-subtitle">View and manage your seat and meeting reservations</div>
      </h1>

      {error && <div className="alert alert-error">{error}</div>}

      <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
        <button
          className={`btn ${tab === 'bookings' ? 'btn-primary' : 'btn-outline'} btn-sm`}
          onClick={() => setTab('bookings')}
        >
          Seat Bookings ({bookings.length})
        </button>
        
        {(user.role === 'MANAGER' || user.role === 'ADMIN') && (
          <button
            className={`btn ${tab === 'meetings' ? 'btn-primary' : 'btn-outline'} btn-sm`}
            onClick={() => setTab('meetings')}
          >
            Meetings ({meetings.length})
          </button>
        )}
      </div>

      {tab === 'bookings' && (
        <div className="card">
          {bookings.length === 0 ? (
            <div className="empty-state">
              <h3>No seat bookings yet</h3>
              <p>Book a seat to see it here</p>
            </div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Seat</th>
                    <th>Room</th>
                    <th>Floor</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {bookings.sort((a, b) => b.bookingDate.localeCompare(a.bookingDate)).map(b => (
                    <tr key={b.id}>
                      <td>{b.bookingDate}</td>
                      <td>{b.startTime} - {b.endTime}</td>
                      <td><strong>{b.seatLabel}</strong></td>
                      <td>{b.roomName}</td>
                      <td>Floor {b.floorNumber}</td>
                      <td>
                        <span className={`badge badge-${b.status.toLowerCase()}`}>
                          {b.status}
                        </span>
                      </td>
                      <td>
                        {b.status === 'CONFIRMED' && (
                          <button 
                            className="btn btn-danger btn-sm"
                            onClick={() => handleCancelBooking(b.id)}
                          >
                            Cancel
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {tab === 'meetings' && (
        <div className="card">
          {meetings.length === 0 ? (
            <div className="empty-state">
              <h3>No meetings yet</h3>
              <p>Book a meeting room to see it here</p>
            </div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Title</th>
                    <th>Room</th>
                    <th>Floor</th>
                    <th>Seats</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {meetings.sort((a, b) => b.meetingDate.localeCompare(a.meetingDate)).map(m => (
                    <tr key={m.id}>
                      <td>{m.meetingDate}</td>
                      <td>{m.startTime} - {m.endTime}</td>
                      <td><strong>{m.title}</strong></td>
                      <td>{m.roomName}</td>
                      <td>Floor {m.floorNumber}</td>
                      <td>{m.seatCount}</td>
                      <td>
                        <span className={`badge badge-${m.status.toLowerCase()}`}>
                          {m.status}
                        </span>
                      </td>
                      <td>
                        {m.status === 'CONFIRMED' && (
                          <button 
                            className="btn btn-danger btn-sm"
                            onClick={() => handleCancelMeeting(m.id)}
                          >
                            Cancel
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}