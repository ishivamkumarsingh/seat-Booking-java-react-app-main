import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useParams, Link } from 'react-router-dom';
import { adminApi } from '../api/api';
import './RoomConfigurator.css';

const TOOL_MOVE = 'move';
const TOOL_WALL = 'wall';
const TOOL_DOOR = 'door';

export default function RoomConfigurator() {
  const { roomId } = useParams();
  const [room, setRoom] = useState(null);
  const [error, setError] = useState('');
  const [showDeskForm, setShowDeskForm] = useState(false);
  const [deskForm, setDeskForm] = useState({
    shape: 'RECTANGLE',
    seatArrangement: 'RECTANGLE',
    color: '#884513',
    positionX: 100,
    positionY: 100,
    width: 160,
    height: 80,
    numberOfSeats: 4,
  });
  const [activeTool, setActiveTool] = useState(TOOL_MOVE);
  const [walls, setWalls] = useState([]);
  const [drawingSegment, setDrawingSegment] = useState(null);
  const [cursorPos, setCursorPos] = useState({ x: 0, y: 0 });
  const [selectedDeskId, setSelectedDeskId] = useState(null);
  const [selectedWallIndex, setSelectedWallIndex] = useState(null);
  const [dragState, setDragState] = useState(null);
  const [savingWalls, setSavingWalls] = useState(false);
  const svgRef = useRef(null);

  const loadRoom = useCallback(async () => {
    try {
      const res = await adminApi.getRoom(roomId);
      const data = res.data;
      setRoom(data);
      if (data.walls) {
        try {
          setWalls(JSON.parse(data.walls));
        } catch {
          setWalls([]);
        }
      }
    } catch {
      setError('Failed to load room');
    }
  }, [roomId]);

  useEffect(() => {
    loadRoom();
  }, [loadRoom]);

  // Mirror of backend generateSeats logic
  const computeSeats = (desk, existingSeats) => {
    const numSeats = existingSeats.length;
    const { positionX: px, positionY: py, width: w, height: h, seatArrangement } = desk;
    const centerX = px + w / 2;
    const centerY = py + h / 2;
    const offset = 30;
    let result = existingSeats.map((s, idx) => ({ ...s }));

    if (seatArrangement === 'CIRCLE') {
      const radius = Math.max(w, h) / 2;
      for (let i = 0; i < numSeats; i++) {
        const angle = (2 * Math.PI * i) / numSeats;
        result[i] = {
          ...result[i],
          positionX: centerX + radius * Math.cos(angle),
          positionY: centerY + radius * Math.sin(angle)
        };
      }
    } else {
      const seatsPerSide = Math.max(1, Math.floor(numSeats / 4));
      let remaining = numSeats;
      let idx = 0;
      
      const topSeats = Math.min(seatsPerSide, remaining);
      for (let i = 0; i < topSeats; i++, idx++) {
        result[idx] = {
          ...result[idx],
          positionX: px + (w / (topSeats + 1)) * (i + 1),
          positionY: py - offset
        };
      }
      remaining -= topSeats;
      
      const rightSeats = Math.min(seatsPerSide, remaining);
      for (let i = 0; i < rightSeats; i++, idx++) {
        result[idx] = {
          ...result[idx],
          positionX: px + w + offset,
          positionY: py + (h / (rightSeats + 1)) * (i + 1)
        };
      }
      remaining -= rightSeats;
      
      const bottomSeats = Math.min(seatsPerSide, remaining);
      for (let i = 0; i < bottomSeats; i++, idx++) {
        result[idx] = {
          ...result[idx],
          positionX: px + (w / (bottomSeats + 1)) * (i + 1),
          positionY: py + h + offset
        };
      }
      remaining -= bottomSeats;
      
      const leftSeats = remaining;
      for (let i = 0; i < leftSeats; i++, idx++) {
        result[idx] = {
          ...result[idx],
          positionX: px - offset,
          positionY: py + (h / (leftSeats + 1)) * (i + 1)
        };
      }
    }
    
    return result;
  };

  useEffect(() => {
    const onkey = (e) => {
      if (e.key === 'Delete' || e.key === 'Backspace') {
        if (document.activeElement && document.activeElement.tagName !== 'BODY') return;
        if (selectedDeskId != null) {
          handleDeleteDesk(selectedDeskId);
        } else if (selectedWallIndex != null) {
          setWalls(prev => prev.filter((_, i) => i !== selectedWallIndex));
          setSelectedWallIndex(null);
        }
      }
    };
    window.addEventListener('keydown', onkey);
    return () => window.removeEventListener('keydown', onkey);
  }, [selectedDeskId, selectedWallIndex]);

  const getSVGCoords = useCallback((e) => {
    const svg = svgRef.current;
    if (!svg) return { x: 0, y: 0 };
    const pt = svg.createSVGPoint();
    pt.x = e.clientX;
    pt.y = e.clientY;
    return pt.matrixTransform(svg.getScreenCTM().inverse());
  }, []);

  const handleSVGMouseDown = (e) => {
    if (activeTool === TOOL_MOVE) {
      setSelectedDeskId(null);
      setSelectedWallIndex(null);
      return;
    }
    
    const p = getSVGCoords(e);
    if (!drawingSegment) {
      setDrawingSegment({ type: activeTool, x1: p.x, y1: p.y });
    } else {
      setWalls(prev => [...prev, { ...drawingSegment, x2: p.x, y2: p.y }]);
      setDrawingSegment(null);
    }
  };

  const handleSVGMouseMove = (e) => {
    const p = getSVGCoords(e);
    setCursorPos(p);
    if (!dragState) return;
    
    const dx = p.x - dragState.startX;
    const dy = p.y - dragState.startY;
    
    if (dragState.mode === 'drag') {
      const newX = Math.max(0, dragState.origX + dx);
      const newY = Math.max(0, dragState.origY + dy);
      const actualDx = newX - dragState.origX;
      const actualDy = newY - dragState.origY;
      
      setRoom(prev => ({
        ...prev,
        desks: prev.desks.map(d =>
          d.id === dragState.deskId
            ? {
                ...d,
                positionX: newX,
                positionY: newY,
                seats: d.seats?.map((seat, i) => ({
                  ...seat,
                  positionX: (dragState.origSeats[i]?.positionX ?? seat.positionX) + actualDx,
                  positionY: (dragState.origSeats[i]?.positionY ?? seat.positionY) + actualDy,
                }))
              }
            : d
        ),
      }));
    } else if (dragState.mode === 'resize') {
      const newW = Math.max(40, dragState.origW + dx);
      const newH = Math.max(30, dragState.origH + dy);
      setRoom(prev => ({
        ...prev,
        desks: prev.desks.map(d => {
          if (d.id !== dragState.deskId) return d;
          const updated = { ...d, width: newW, height: newH };
          return { ...updated, seats: computeSeats(updated, d.seats || []) };
        }),
      }));
    }
  };

  const handleSVGMouseUp = async () => {
    if (!dragState) return;
    const desk = room?.desks?.find(d => d.id === dragState.deskId);
    if (desk) {
      try {
        await adminApi.updateDesk(dragState.deskId, {
          positionX: desk.positionX,
          positionY: desk.positionY,
          width: desk.width,
          height: desk.height,
        });
        if (dragState.mode === 'resize') {
          await loadRoom();
        }
      } catch {
        setError('Failed to save desk position');
      }
    }
    setDragState(null);
  };

  const handleDeskMouseDown = (e, desk) => {
    if (activeTool !== TOOL_MOVE) return;
    e.preventDefault();
    e.stopPropagation();
    const p = getSVGCoords(e);
    
    setSelectedDeskId(desk.id);
    setDragState({
      deskId: desk.id,
      startX: p.x,
      startY: p.y,
      origX: desk.positionX,
      origY: desk.positionY,
      origW: desk.width,
      origH: desk.height,
      origSeats: (desk.seats || []).map(s => ({ positionX: s.positionX, positionY: s.positionY })),
      mode: 'drag',
    });
  };

  const handleResizeMouseDown = (e, desk) => {
    e.preventDefault();
    e.stopPropagation();
    const p = getSVGCoords(e);
    setDragState({
      deskId: desk.id,
      startX: p.x,
      startY: p.y,
      origX: desk.positionX,
      origY: desk.positionY,
      origW: desk.width,
      origH: desk.height,
      mode: 'resize',
    });
  };

  const handleCreateDesk = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await adminApi.createDesk({
        roomId: parseInt(roomId),
        shape: deskForm.shape,
        seatArrangement: deskForm.seatArrangement,
        color: deskForm.color,
        positionX: parseFloat(deskForm.positionX),
        positionY: parseFloat(deskForm.positionY),
        width: parseFloat(deskForm.width),
        height: parseFloat(deskForm.height),
        numberOfSeats: parseInt(deskForm.numberOfSeats),
      });
      setShowDeskForm(false);
      loadRoom();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create desk');
    }
  };

  const handleRotateDesk = async (deskId, angleDelta) => {
    const desk = room?.desks?.find(d => d.id === deskId);
    if (!desk) return;
    const newRotation = ((desk.rotation || 0) + angleDelta + 360) % 360;
    
    setRoom(prev => ({
      ...prev,
      desks: prev.desks.map(d =>
        d.id === deskId ? { ...d, rotation: newRotation } : d
      ),
    }));
    
    try {
      await adminApi.updateDesk(deskId, {
        positionX: desk.positionX,
        positionY: desk.positionY,
        width: desk.width,
        height: desk.height,
        rotation: newRotation,
      });
    } catch {
      setError('Failed to save rotation');
    }
  };

  const handleDeleteSelectedWall = () => {
    if (selectedWallIndex == null) return;
    setWalls(prev => prev.filter((_, i) => i !== selectedWallIndex));
    setSelectedWallIndex(null);
  };

  const handleDeleteDesk = async (deskId) => {
    if (!window.confirm('Delete this desk and all its seats?')) return;
    try {
      await adminApi.deleteDesk(deskId);
      loadRoom();
    } catch {
      setError('Failed to delete desk');
    }
  };

  const handleSaveWalls = async () => {
    setSavingWalls(true);
    try {
      await adminApi.updateRoomWalls(roomId, { walls: JSON.stringify(walls) });
      setError('');
    } catch {
      setError('Failed to save walls');
    } finally {
      setSavingWalls(false);
    }
  };

  const renderDeskShape = (desk) => {
    const { shape, color, positionX: px, positionY: py, width: dw, height: dh, id } = desk;
    const isSelected = selectedDeskId === id;
    const props = {
      fill: color,
      stroke: isSelected ? '#4f46e5' : 'rgba(0,0,0,0.2)',
      strokeWidth: isSelected ? 3 : 2,
    };
    
    switch (shape) {
      case 'CIRCLE':
        return <ellipse cx={px + dw / 2} cy={py + dh / 2} rx={dw / 2} ry={dh / 2} {...props} />;
      case 'SEMICIRCLE': {
        const rx = dw / 2;
        const ry = dh;
        return <path d={`M ${px} ${py + dh} A ${rx} ${ry} 0 0 1 ${px + dw} ${py + dh} Z`} {...props} />;
      }
      default:
        return <rect x={px} y={py} width={dw} height={dh} rx="6" ry="6" {...props} />;
    }
  };

  const renderDesk = (desk) => {
    const { positionX: px, positionY: py, width: dw, height: dh, shape, id, rotation } = desk;
    const isSelected = selectedDeskId === id;
    const cx = px + dw / 2;
    const cy = py + dh / 2;
    const rot = rotation || 0;
    
    return (
      <g key={id}>
        <g transform={`rotate(${rot}, ${cx}, ${cy})`}>
          <g
            style={{ cursor: activeTool === TOOL_MOVE ? 'grab' : 'default' }}
            onMouseDown={(e) => handleDeskMouseDown(e, desk)}
          >
            {renderDeskShape(desk)}
            <text
              x={cx}
              y={cy + 1}
              textAnchor="middle"
              dominantBaseline="middle"
              fontSize="11"
              fontWeight="500"
              fill="rgba(255,255,255,0.8)"
              pointerEvents="none"
            >
              {shape === 'CIRCLE' ? '○' : shape === 'SEMICIRCLE' ? '⌒' : '□'}
            </text>
          </g>
          {desk.seats?.map(seat => (
            <g key={seat.id} pointerEvents="none">
              <circle cx={seat.positionX} cy={seat.positionY} r={12}
                fill="#4ade80" stroke="rgba(0,0,0,0.15)" strokeWidth="1.5" />
              <text x={seat.positionX} y={seat.positionY + 1}
                textAnchor="middle" dominantBaseline="middle" fontSize="9" fontWeight="600" fill="white">
                {seat.label}
              </text>
            </g>
          ))}
        </g>
        {isSelected && activeTool === TOOL_MOVE && (
          <rect
            x={px + dw - 6}
            y={py + dh - 6}
            width={12}
            height={12}
            rx="2"
            fill="#4f46e5"
            stroke="white"
            strokeWidth="1.5"
            style={{ cursor: 'se-resize' }}
            onMouseDown={(e) => handleResizeMouseDown(e, desk)}
          />
        )}
      </g>
    );
  };

  const renderWall = (seg, i) => {
    const isSelected = selectedWallIndex === i;
    const handleWallMouseDown = (e) => {
      if (activeTool !== TOOL_MOVE) return;
      e.stopPropagation();
      setSelectedWallIndex(isSelected ? null : i);
      setSelectedDeskId(null);
    };
    
    if (seg.type === 'wall') {
      return (
        <g key={i} onMouseDown={handleWallMouseDown} style={{ cursor: activeTool === TOOL_MOVE ? 'pointer' : 'default' }}>
          <line x1={seg.x1} y1={seg.y1} x2={seg.x2} y2={seg.y2} stroke="transparent" strokeWidth="18" />
          <line x1={seg.x1} y1={seg.y1} x2={seg.x2} y2={seg.y2}
            stroke={isSelected ? '#4f46e5' : '#374151'}
            strokeWidth={isSelected ? 7 : 6} strokeLinecap="round" />
          {isSelected && (
            <>
              <circle cx={seg.x1} cy={seg.y1} r="6" fill="#4f46e5" stroke="white" strokeWidth="1.5" />
              <circle cx={seg.x2} cy={seg.y2} r="6" fill="#4f46e5" stroke="white" strokeWidth="1.5" />
            </>
          )}
        </g>
      );
    } else {
      return (
        <g key={i} onMouseDown={handleWallMouseDown} style={{ cursor: activeTool === TOOL_MOVE ? 'pointer' : 'default' }}>
          <line x1={seg.x1} y1={seg.y1} x2={seg.x2} y2={seg.y2} stroke="transparent" strokeWidth="18" />
          <line x1={seg.x1} y1={seg.y1} x2={seg.x2} y2={seg.y2}
            stroke={isSelected ? '#4f46e5' : '#f59e0b'}
            strokeWidth={isSelected ? 5 : 4} strokeLinecap="round" strokeDasharray="10 5" />
          <text x={(seg.x1 + seg.x2) / 2} y={(seg.y1 + seg.y2) / 2 - 8}
            textAnchor="middle" fontSize="14" fill={isSelected ? '#4f46e5' : '#f59e0b'} pointerEvents="none">
            🚪
          </text>
          {isSelected && (
            <>
              <circle cx={seg.x1} cy={seg.y1} r="6" fill="#4f46e5" stroke="white" strokeWidth="1.5" />
              <circle cx={seg.x2} cy={seg.y2} r="6" fill="#4f46e5" stroke="white" strokeWidth="1.5" />
            </>
          )}
        </g>
      );
    }
  };

  if (!room) return <div className="card">Loading room...</div>;

  const W = room.width || 800;
  const H = room.height || 600;
  const wallCount = walls.filter(w => w.type === 'wall').length;
  const doorCount = walls.filter(w => w.type === 'door').length;

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '24px' }}>
        <Link to="/admin" className="btn btn-outline btn-sm">← Back</Link>
        <h1 className="page-title" style={{ marginBottom: 0 }}>
          Room {room.roomNumber}: {room.name}
          <div className="page-subtitle">Floor {room.floorNumber} {W}x{H}px canvas</div>
        </h1>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="configurator-layout">
        <div className="configurator-sidebar">
          {/* Tools */}
          <div className="card" style={{ marginBottom: '16px' }}>
            <div className="card-header"><h3>Tools</h3></div>
            <div className="tool-buttons">
              <button
                className={`btn btn-sm tool-btn ${activeTool === TOOL_MOVE ? 'btn-primary' : 'btn-outline'}`}
                onClick={() => { setActiveTool(TOOL_MOVE); setDrawingSegment(null); }}
              >Move / Resize</button>
              <button
                className={`btn btn-sm tool-btn ${activeTool === TOOL_WALL ? 'btn-primary' : 'btn-outline'}`}
                onClick={() => { setActiveTool(TOOL_WALL); setSelectedDeskId(null); }}
              >Draw Wall</button>
              <button
                className={`btn btn-sm tool-btn ${activeTool === TOOL_DOOR ? 'btn-primary' : 'btn-outline'}`}
                onClick={() => { setActiveTool(TOOL_DOOR); setSelectedDeskId(null); }}
              >Draw Door</button>
            </div>
            
            {(activeTool === TOOL_WALL || activeTool === TOOL_DOOR) && (
              <div className="tool-hint">
                {drawingSegment
                  ? <><span>Click to place endpoint</span>
                    <button className="btn btn-outline btn-sm" onClick={() => setDrawingSegment(null)}>Cancel</button></>
                  : <span>Click on canvas to start</span>
                }
              </div>
            )}
            
            {activeTool === TOOL_MOVE && (
              <div className="tool-hint">
                {(selectedDeskId != null || selectedWallIndex != null)
                  ? <>
                      <span>
                        {selectedDeskId != null ? 'Desk selected' : `${walls[selectedWallIndex]?.type === 'door' ? 'Door' : 'Wall'} selected`}
                      </span>
                      <div style={{ display: 'flex', gap: '4px' }}>
                        {selectedDeskId != null && (
                          <>
                            <button className="btn btn-outline btn-sm" title="Rotate counter-clockwise"
                              onClick={() => handleRotateDesk(selectedDeskId, -15)}>↺</button>
                            <button className="btn btn-outline btn-sm" title="Rotate clockwise"
                              onClick={() => handleRotateDesk(selectedDeskId, 15)}>↻</button>
                          </>
                        )}
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => {
                            if (selectedDeskId != null) handleDeleteDesk(selectedDeskId);
                            else handleDeleteSelectedWall();
                          }}
                        >Delete</button>
                      </div>
                    </>
                  : <span>Click to select, Drag to move, Corner to resize</span>
                }
              </div>
            )}
          </div>

          {/* Desks */}
          <div className="card" style={{ marginBottom: '16px' }}>
            <div className="card-header">
              <h3>Desks</h3>
              <button className="btn btn-primary btn-sm" onClick={() => setShowDeskForm(!showDeskForm)}>
                + Add Desk
              </button>
            </div>
            
            {showDeskForm && (
              <form onSubmit={handleCreateDesk} className="desk-form">
                <div className="form-group">
                  <label>Desk Shape</label>
                  <select value={deskForm.shape}
                    onChange={e => setDeskForm({ ...deskForm, shape: e.target.value })}>
                    <option value="RECTANGLE">Rectangle</option>
                    <option value="CIRCLE">Circle</option>
                    <option value="SEMICIRCLE">Semi-Circle</option>
                  </select>
                </div>
                
                <div className="form-group">
                  <label>Seat Arrangement</label>
                  <select value={deskForm.seatArrangement}
                    onChange={e => setDeskForm({ ...deskForm, seatArrangement: e.target.value })}>
                    <option value="RECTANGLE">Rectangle (around sides)</option>
                    <option value="CIRCLE">Circle (around desk)</option>
                  </select>
                </div>
                
                <div className="form-group">
                  <label>Desk Color</label>
                  <div className="color-picker-wrapper">
                    <input type="color" value={deskForm.color}
                      onChange={e => setDeskForm({ ...deskForm, color: e.target.value })} />
                    <span style={{ fontSize: '13px', color: 'var(--gray-500)' }}>{deskForm.color}</span>
                  </div>
                </div>
                
                <div className="form-row">
                  <div className="form-group">
                    <label>X Position</label>
                    <input type="number" value={deskForm.positionX}
                      onChange={e => setDeskForm({ ...deskForm, positionX: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label>Y Position</label>
                    <input type="number" value={deskForm.positionY}
                      onChange={e => setDeskForm({ ...deskForm, positionY: e.target.value })} />
                  </div>
                </div>
                
                <div className="form-row">
                  <div className="form-group">
                    <label>Width</label>
                    <input type="number" value={deskForm.width}
                      onChange={e => setDeskForm({ ...deskForm, width: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label>Height</label>
                    <input type="number" value={deskForm.height}
                      onChange={e => setDeskForm({ ...deskForm, height: e.target.value })} />
                  </div>
                </div>
                
                <div className="form-group">
                  <label>Number of Seats</label>
                  <input type="number" min="1" max="20" value={deskForm.numberOfSeats}
                    onChange={e => setDeskForm({ ...deskForm, numberOfSeats: e.target.value })} />
                </div>
                
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button type="submit" className="btn btn-primary btn-sm">Create Desk</button>
                  <button type="button" className="btn btn-outline btn-sm" onClick={() => setShowDeskForm(false)}>Cancel</button>
                </div>
              </form>
            )}
            
            <div className="desk-list">
              {room.desks?.length === 0 && (
                <p style={{ color: 'var(--gray-400)', fontSize: '13px', padding: '12px 0' }}>No desks yet</p>
              )}
              {room.desks?.map(desk => (
                <div
                  key={desk.id}
                  className={`desk-item ${selectedDeskId === desk.id ? 'desk-item-selected' : ''}`}
                  onClick={() => { if (activeTool === TOOL_MOVE) setSelectedDeskId(desk.id); }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span className="desk-color-dot" style={{ background: desk.color }} />
                    <strong>{desk.shape}</strong>
                    <span className="badge badge-user">{desk.seats?.length || 0} seats</span>
                  </div>
                  <div style={{ fontSize: '12px', color: 'var(--gray-500)', marginTop: '2px' }}>
                    {desk.seatArrangement} ({Math.round(desk.positionX)}, {Math.round(desk.positionY)})
                    {' '}{Math.round(desk.width)}x{Math.round(desk.height)}
                  </div>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={(e) => { e.stopPropagation(); handleDeleteDesk(desk.id); }}
                  >✗</button>
                </div>
              ))}
            </div>
          </div>

          {/* Walls & Doors */}
          <div className="card">
            <div className="card-header">
              <h3>Walls & Doors</h3>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button className="btn btn-outline btn-sm"
                  onClick={() => setWalls(prev => prev.slice(0, -1))}
                  disabled={walls.length === 0}>Undo</button>
                <button className="btn btn-primary btn-sm" onClick={handleSaveWalls} disabled={savingWalls}>
                  {savingWalls ? 'Saving...' : 'Save Layout'}
                </button>
              </div>
            </div>
            <p style={{ fontSize: '13px', color: 'var(--gray-500)', margin: '8px 0 0' }}>
              {walls.length === 0
                ? 'No walls or doors drawn yet'
                : `${wallCount} wall${wallCount !== 1 ? 's' : ''}, ${doorCount} door${doorCount !== 1 ? 's' : ''}`}
            </p>
          </div>
        </div>

        {/* Canvas */}
        <div className="configurator-canvas">
          <div className="card" style={{ padding: '16px' }}>
            <svg
              ref={svgRef}
              viewBox={`0 0 ${W} ${H}`}
              className="room-layout-svg configurator-svg"
              style={{ maxWidth: W, userSelect: 'none', cursor: activeTool === TOOL_MOVE ? 'default' : 'crosshair' }}
              onMouseDown={handleSVGMouseDown}
              onMouseMove={handleSVGMouseMove}
              onMouseUp={handleSVGMouseUp}
              onMouseLeave={() => setDragState(null)}
            >
              <defs>
                <pattern id="cgrid" width="40" height="40" patternUnits="userSpaceOnUse">
                  <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#e5e7eb" strokeWidth="0.5" />
                </pattern>
              </defs>
              <rect width={W} height={H} fill="url(#cgrid)" rx="8" />
              <rect width={W} height={H} fill="none" stroke="#d1d5db" strokeWidth="2" rx="8" />
              
              {walls.map(renderWall)}
              
              {drawingSegment && (
                <line
                  x1={drawingSegment.x1}
                  y1={drawingSegment.y1}
                  x2={cursorPos.x}
                  y2={cursorPos.y}
                  stroke={drawingSegment.type === 'wall' ? '#374151' : '#f59e0b'}
                  strokeWidth={drawingSegment.type === 'wall' ? 6 : 4}
                  strokeDasharray="8 4"
                  strokeLinecap="round"
                  pointerEvents="none"
                />
              )}
              
              {room.desks?.map(renderDesk)}
            </svg>
          </div>
        </div>
      </div>
    </div>
  );
}