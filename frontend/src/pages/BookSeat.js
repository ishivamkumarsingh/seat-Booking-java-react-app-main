import React, { useEffect, useState } from 'react';
import { bookingApi } from '../api/api';
import RoomLayout from '../components/RoomLayout';
import './BookSeat.css';

export default function BookSeat() {
  const [floors, setFloors] = useState([]);
  const [selectedFloor, setSelectedFloor] = useState(null);
  const [floorData, setFloorData] = useState(null);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [roomData, setRoomData] = useState(null);
  const [seats, setSeats] = useState([]);
  const [selectedSeat, setSelectedSeat] = useState(null);
  const [bookingDate, setBookingDate] = useState('');
  const [startTime, setStartTime] = useState('09:00');
  const [endTime, setEndTime] = useState('12:00');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    bookingApi.getFloors().then(r => setFloors(r.data)).catch(() => {});
  }, []);

  const handleFloorSelect = async (floorId) => {
    setSelectedFloor(floorId);
    setSelectedRoom(null);
    setRoomData(null);
    setSeats([]);
    setSelectedSeat(null);
    try {
      const res = await bookingApi.getFloor(floorId);
      setFloorData(res.data);
    } catch {
      setError('Failed to load floor data');
    }
  };

  const handleRoomSelect = async (roomId) => {
    setSelectedRoom(roomId);
    setSelectedSeat(null);
    setSeats([]);
    try {
      const res = await bookingApi.getRoom(roomId);
      setRoomData(res.data);
      if (bookingDate && startTime && endTime) {
        loadSeats(roomId);
      }
    } catch {
      setError('Failed to load room data');
    }
  };

  const loadSeats = async (roomId) => {
    if (!bookingDate || !startTime || !endTime) return;
    try {
      const res = await bookingApi.getAvailableSeats(roomId || selectedRoom, bookingDate, startTime, endTime);
      setSeats(res.data);
    } catch {
      setError('Failed to load seat availability');
    }
  };

  useEffect(() => {
    if (selectedRoom && bookingDate && startTime && endTime) {
      loadSeats(selectedRoom);
    }
  }, [bookingDate, startTime, endTime, selectedRoom]); // eslint-disable-line

  const handleSeatClick = (seat) => {
    setSelectedSeat(seat.id === selectedSeat ? null : seat.id);
    setError('');
    setSuccess('');
  };

  const handleBook = async () => {
    if (!selectedSeat || !bookingDate || !startTime || !endTime) {
      setError('Please select a date, time, and seat');
      return;
    }
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      await bookingApi.createBooking({
        seatId: selectedSeat,
        bookingDate,
        startTime,
        endTime
      });
      setSuccess('Seat booked successfully!');
      setSelectedSeat(null);
      loadSeats(selectedRoom);
    } catch (err) {
      const msg = err.response?.data?.message || 'Booking failed';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const today = new Date().toISOString().split('T')[0];

  return (
    <div>
      <div className="card">
        <h1 className="page-title">
          Book a Seat
          <div className="page-subtitle">Select date, time, floor, room, and your preferred seat</div>
        </h1>

        {/* Step 1: Date & Time */}
        <h3 style={{ marginBottom: '16px' }}>1. Select Date & Time</h3>
        <div className="form-row" style={{ gridTemplateColumns: '1fr 1fr 1fr' }}>
          <div className="form-group">
            <label>Date</label>
            <input 
              type="date" 
              min={today} 
              value={bookingDate}
              onChange={e => setBookingDate(e.target.value)} 
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
      </div>

      {/* Step 2: Select Floor */}
      <div className="card">
        <h3 style={{ marginBottom: '16px' }}>2. Select Floor</h3>
        {floors.length === 0 ? (
          <p style={{ color: 'var(--gray-400)' }}>No floors available. Admin needs to configure the office.</p>
        ) : (
          <div className="floor-chips">
            {floors.map(f => (
              <button
                key={f.id}
                className={`floor-chip ${selectedFloor === f.id ? 'active' : ''}`}
                onClick={() => handleFloorSelect(f.id)}
              >
                Floor {f.floorNumber}: {f.name}
                <span className="chip-count">{f.roomCount} rooms</span>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Step 3: Select Room */}
      {floorData && (
        <div className="card">
          <h3 style={{ marginBottom: '16px' }}>3. Select Room</h3>
          {floorData.rooms?.length === 0 ? (
            <p style={{ color: 'var(--gray-400)' }}>No rooms on this floor</p>
          ) : (
            <div className="floor-chips">
              {floorData.rooms?.map(r => (
                <button
                  key={r.id}
                  className={`floor-chip ${selectedRoom === r.id ? 'active' : ''}`}
                  onClick={() => handleRoomSelect(r.id)}
                >
                  Room {r.roomNumber}: {r.name}
                  <span className="chip-count">{r.desks?.length || 0} desks</span>
                </button>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Step 4: Select Seat */}
      {roomData && (
        <div className="card">
          <h3 style={{ marginBottom: '16px' }}>4. Select a Seat</h3>
          {!bookingDate ? (
            <div className="alert alert-info">Please select a date and time first to see seat availability</div>
          ) : (
            <>
              {error && <div className="alert alert-error">{error}</div>}
              {success && <div className="alert alert-success">{success}</div>}
              <RoomLayout
                room={roomData}
                seats={seats}
                onSeatClick={handleSeatClick}
                selectedSeatId={selectedSeat}
                interactive={true}
              />
              {selectedSeat && (
                <div className="booking-confirm">
                  <div className="confirm-details">
                    <strong>Selected:</strong> Seat {seats.find(s => s.id === selectedSeat)?.label} - {bookingDate} {startTime} - {endTime}
                  </div>
                  <button
                    className="btn btn-primary"
                    onClick={handleBook}
                    disabled={loading}
                  >
                    {loading ? 'Booking...' : 'Confirm Booking'}
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
}