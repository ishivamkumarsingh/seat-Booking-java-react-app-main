import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { adminApi } from '../api/api';

export default function AdminPanel() {
  const [floors, setFloors] = useState([]);
  const [showFloorForm, setShowFloorForm] = useState(false);
  const [showRoomForm, setShowRoomForm] = useState(null);
  const [floorForm, setFloorForm] = useState({ floorNumber: "", name: "" });
  const [roomForm, setRoomForm] = useState({ name: "", roomNumber: "", width: 800, height: 600 });
  const [expandedFloor, setExpandedFloor] = useState(null);
  const [rooms, setRooms] = useState({});
  const [error, setError] = useState('');

  const loadFloors = async () => { 
    try {
      const res = await adminApi.getFloors(); 
      setFloors(res.data); 
    } catch (err) { 
      setError('Failed to load floors');
    }
  };

  useEffect(() => { loadFloors(); }, []);

  const loadRooms = async (floorId) => {
    try { 
      const res = await adminApi.getRoomsByFloor(floorId); 
      setRooms(prev => ({ ...prev, [floorId]: res.data })); 
    } catch (err) { 
      setError('Failed to load rooms');
    }
  };

  const toggleFloor = (floorId) => { 
    if (expandedFloor === floorId) { 
      setExpandedFloor(null); 
    } else { 
      setExpandedFloor(floorId); 
      if (!rooms[floorId]) loadRooms(floorId);
    }
  };

  const handleCreateFloor = async (e) => { 
    e.preventDefault(); 
    setError('');
    try { 
      await adminApi.createFloor({ name: floorForm.name, floorNumber: parseInt(floorForm.floorNumber) });
      setFloorForm({ floorNumber: "", name: "" }); 
      setShowFloorForm(false); 
      loadFloors(); 
    } catch (err) { 
      setError(err.response?.data?.message || 'Failed to create floor');
    }
  };

  const handleCreateRoom = async (e, floorId) => { 
    e.preventDefault(); 
    setError(''); 
    try { 
      await adminApi.createRoom({ 
        floorId, 
        name: roomForm.name, 
        roomNumber: parseInt(roomForm.roomNumber), 
        width: parseInt(roomForm.width), 
        height: parseInt(roomForm.height) 
      });
      setRoomForm({ name: "", roomNumber: "", width: 800, height: 600 }); 
      setShowRoomForm(null); 
      loadRooms(floorId); 
      loadFloors(); 
    } catch (err) { 
      setError(err.response?.data?.message || 'Failed to create room');
    }
  };

  const handleDeleteFloor = async (id) => {
    if (!window.confirm('Delete this floor and all its rooms?')) return;
    try {
      await adminApi.deleteFloor(id);
      loadFloors();
    } catch (err) {
      setError('Failed to delete floor');
    }
  };

  const handleDeleteRoom = async (roomId, floorId) => {
    if (!window.confirm('Delete this room and all its desks?')) return;
    try {
      await adminApi.deleteRoom(roomId);
      loadRooms(floorId);
      loadFloors();
    } catch (err) {
      setError('Failed to delete room');
    }
  };

  return (
    <div>
      <div className="card-header" style={{ marginBottom: '24px' }}>
        <h1 className="page-title" style={{ marginBottom: 0 }}>
          Admin Panel
          <div className="page-subtitle">Manage your office layout</div>
        </h1>
        <button className="btn btn-primary" onClick={() => setShowFloorForm(!showFloorForm)}>
          + Add Floor
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {showFloorForm && (
        <div className="card" style={{ marginBottom: '16px' }}>
          <h3 style={{ marginBottom: '16px' }}>New Floor</h3>
          <form onSubmit={handleCreateFloor}>
            <div className="form-row">
              <div className="form-group">
                <label>Floor Number</label>
                <input 
                  type="number" 
                  value={floorForm.floorNumber}
                  onChange={e => setFloorForm({ ...floorForm, floorNumber: e.target.value })} 
                  required 
                />
              </div>
              <div className="form-group">
                <label>Floor Name</label>
                <input 
                  type="text" 
                  value={floorForm.name}
                  onChange={e => setFloorForm({ ...floorForm, name: e.target.value })} 
                  required 
                />
              </div>
            </div>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button type="submit" className="btn btn-primary">Create Floor</button>
              <button type="button" className="btn btn-outline" onClick={() => setShowFloorForm(false)}>Cancel</button>
            </div>
          </form>
        </div>
      )}

      {floors.length === 0 ? (
        <div className="empty-state">
          <h3>No floors configured</h3>
          <p>Add your first floor to get started</p>
        </div>
      ) : (
        floors.map(floor => (
          <div key={floor.id} className="card">
            <div className="card-header" style={{ marginBottom: expandedFloor === floor.id ? '16px' : 0 }}>
              <div style={{ cursor: 'pointer', flex: 1 }} onClick={() => toggleFloor(floor.id)}>
                <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span>{expandedFloor === floor.id ? '▼' : '▶'}</span>
                  Floor {floor.floorNumber} {floor.name}
                  <span className="badge badge-user">{floor.roomCount} rooms</span>
                </h3>
              </div>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button 
                  className="btn btn-outline btn-sm"
                  onClick={() => { setShowRoomForm(showRoomForm === floor.id ? null : floor.id); }}
                >
                  + Room
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => handleDeleteFloor(floor.id)}>
                  Delete
                </button>
              </div>
            </div>

            {expandedFloor === floor.id && (
              <div>
                {showRoomForm === floor.id && (
                  <div style={{ background: 'var(--gray-50)', borderRadius: 'var(--radius)', padding: '16px', marginBottom: '16px' }}>
                    <h4 style={{ marginBottom: '12px' }}>New Room on Floor {floor.floorNumber}</h4>
                    <form onSubmit={(e) => handleCreateRoom(e, floor.id)}>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Room Name</label>
                          <input 
                            type="text" 
                            value={roomForm.name}
                            onChange={e => setRoomForm({ ...roomForm, name: e.target.value })} 
                            required 
                          />
                        </div>
                        <div className="form-group">
                          <label>Room Number</label>
                          <input 
                            type="number" 
                            value={roomForm.roomNumber}
                            onChange={e => setRoomForm({ ...roomForm, roomNumber: e.target.value })} 
                            required 
                          />
                        </div>
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Canvas Width (px)</label>
                          <input 
                            type="number" 
                            value={roomForm.width}
                            onChange={e => setRoomForm({ ...roomForm, width: e.target.value })} 
                          />
                        </div>
                        <div className="form-group">
                          <label>Canvas Height (px)</label>
                          <input 
                            type="number" 
                            value={roomForm.height}
                            onChange={e => setRoomForm({ ...roomForm, height: e.target.value })} 
                          />
                        </div>
                      </div>
                      <div style={{ display: 'flex', gap: '8px' }}>
                        <button type="submit" className="btn btn-primary btn-sm">Create</button>
                        <button type="button" className="btn btn-outline btn-sm" onClick={() => setShowRoomForm(null)}>Cancel</button>
                      </div>
                    </form>
                  </div>
                )}

                {!rooms[floor.id] || rooms[floor.id].length === 0 ? (
                  <p style={{ color: 'var(--gray-400)', fontSize: '14px' }}>No rooms yet. Add one above.</p>
                ) : (
                  rooms[floor.id] && rooms[floor.id].map(room => (
                    <div key={room.id} style={{
                      display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                      padding: '12px 16px', borderRadius: 'var(--radius)', background: 'var(--gray-50)',
                      marginBottom: '8px'
                    }}>
                      <div>
                        <strong>Room {room.roomNumber}: {room.name}</strong>
                        <span style={{ color: 'var(--gray-500)', fontSize: '13px', marginLeft: '12px' }}>
                          {room.desks?.length || 0} desks {room.width}x{room.height}px
                        </span>
                      </div>
                      <div style={{ display: 'flex', gap: '8px' }}>
                        <Link to={`/admin/room/${room.id}`} className="btn btn-primary btn-sm">
                          Configure Layout
                        </Link>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDeleteRoom(room.id, floor.id)}>Delete</button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            )}
          </div>
        ))
      )}
    </div>
  );
}