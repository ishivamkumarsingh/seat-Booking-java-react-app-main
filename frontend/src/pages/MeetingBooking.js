import React, { useState } from 'react';
import { meetingApi } from '../api/api';

export default function MeetingBooking() {
  const [meetingDate, setMeetingDate] = useState('');
  const [startTime, setStartTime] = useState('09:00');
  const [endTime, setEndTime] = useState('10:00');
  const [title, setTitle] = useState('');
  const [availableDesks, setAvailableDesks] = useState([]);
  const [searched, setSearched] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');

  const handleSearch = async () => {
    if (!meetingDate || !startTime || !endTime) {
      setError('Please select date and time');
      return;
    }
    setError('');
    setSuccess('');
    try {
      const res = await meetingApi.getAvailableDesks(meetingDate, startTime, endTime);
      setAvailableDesks(res.data);
      setSearched(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to search');
    }
  };

  const handleBookMeeting = async (deskId) => {
    if (!title.trim()) {
      setError('Please enter a meeting title');
      return;
    }
    setSuccess('');
    setError('');
    setLoading(true);
    try {
      await meetingApi.bookMeeting({
        deskId,
        title,
        meetingDate,
        startTime,
        endTime
      });
      setSuccess('Meeting booked successfully!');
      handleSearch(); // Refresh availability
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to book meeting');
    } finally {
      setLoading(false);
    }
  };

  const today = new Date().toISOString().split('T')[0];

  return (
    <div>
      <h1 className="page-title">
        Book a Meeting
        <div className="page-subtitle">Find available circular desks across all floors and book the entire circle</div>
      </h1>

      <div className="card">
        <h3 style={{ marginBottom: '16px' }}>Meeting Details</h3>
        <div className="form-group">
          <label>Meeting Title</label>
          <input 
            type="text" 
            value={title} 
            onChange={e => setTitle(e.target.value)}
            placeholder="e.g., Sprint Planning, Team Standup" 
          />
        </div>
        <div className="form-row" style={{ gridTemplateColumns: '1fr 1fr 1fr' }}>
          <div className="form-group">
            <label>Date</label>
            <input 
              type="date" 
              min={today} 
              value={meetingDate}
              onChange={e => setMeetingDate(e.target.value)} 
            />
          </div>
          <div className="form-group">
            <label>Start Time</label>
            <input 
              type="time" 
              value={startTime} 
              onChange={e => setStartTime(e.target.value)} 
            />
          </div>
          <div className="form-group">
            <label>End Time (max 6 hrs)</label>
            <input 
              type="time" 
              value={endTime} 
              onChange={e => setEndTime(e.target.value)} 
            />
          </div>
        </div>
        <button className="btn btn-primary" onClick={handleSearch}>
          Find Available Circular Desks
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      {searched && (
        <div className="card">
          <h3 style={{ marginBottom: '16px' }}>
            Available Circular Desks
            <span style={{ fontSize: '14px', fontWeight: 400, color: 'var(--gray-500)', marginLeft: '8px' }}>
              ({availableDesks.length} found)
            </span>
          </h3>
          {availableDesks.length === 0 ? (
            <div className="empty-state">
              <h3>No circular desks available</h3>
              <p>Try a different date or time slot</p>
            </div>
          ) : (
            <div className="grid-3">
              {availableDesks.map(desk => (
                <div key={desk.deskId} style={{
                  padding: '20px',
                  border: '2px solid var(--gray-200)',
                  borderRadius: 'var(--radius)',
                  transition: 'all 0.2s',
                  textAlign: 'center'
                }}>
                  <div style={{
                    width: 60, height: 60, borderRadius: '50%', background: desk.color, margin: '0 auto 12px',
                    display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white',
                    fontWeight: 700, fontSize: '18px'
                  }}>
                    {desk.seatCount}
                  </div>
                  <h4>{desk.roomName}</h4>
                  <p style={{ fontSize: '13px', color: 'var(--gray-500)', marginBottom: '4px' }}>
                    Floor {desk.floorNumber} {desk.floorName}
                  </p>
                  <p style={{ fontSize: '13px', color: 'var(--gray-500)', marginBottom: '12px' }}>
                    {desk.seatCount} seats {desk.shape} desk
                  </p>
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={() => handleBookMeeting(desk.deskId)}
                    disabled={loading}
                    style={{ width: '100%', justifyContent: 'center' }}
                  >
                    {loading ? 'Booking...' : 'Book This Circle'}
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}