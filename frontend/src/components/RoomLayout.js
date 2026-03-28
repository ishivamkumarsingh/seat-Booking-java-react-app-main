import React from 'react';
import './RoomLayout.css';

export default function RoomLayout({ room, seats, onSeatClick, selectedSeatId, interactive = false }) {
  if (!room) return null;
  
  const width = room.width || 800;
  const height = room.height || 600;
  
  // Parse walls/doors JSON stored on the room
  let wallSegments = [];
  if (room.walls) {
    try { wallSegments = JSON.parse(room.walls); } catch { wallSegments = []; }
  }
  
  // Merge availability data with room desk/seat data
  const seatAvailability = {};
  if (seats) {
    seats.forEach(s => { seatAvailability[s.id] = s; });
  }
  
  const renderDesk = (desk) => {
    const { shape, color, positionX, positionY, width: dw, height: dh } = desk;
    switch (shape) {
      case 'CIRCLE':
        return (
          <ellipse
            cx={positionX + dw/2}
            cy={positionY + dh/2}
            rx={dw/2}
            ry={dh/2}
            fill={color}
            stroke="rgba(0,0,0,0.2)"
            strokeWidth="2"
          />
        );
      case 'SEMICIRCLE':
        const cx = positionX + dw/2;
        const cy = positionY + dh;
        const rx = dw/2;
        const ry = dh;
        return (
          <path
            d={`M ${positionX} ${cy} A ${rx} ${ry} 0 0 1 ${positionX + dw} ${cy} Z`}
            fill={color}
            stroke="rgba(0,0,0,0.2)"
            strokeWidth="2"
          />
        );
      default: // RECTANGLE
        return (
          <rect
            x={positionX}
            y={positionY}
            width={dw}
            height={dh}
            rx="6"
            ry="6"
            fill={color}
            stroke="rgba(0,0,0,0.2)"
            strokeWidth="2"
          />
        );
    }
  };
  
  const renderSeat = (seat, desk) => {
    const seatData = seatAvailability[seat.id];
    const isBooked = seatData ? seatData.booked : false;
    const isSelected = selectedSeatId === seat.id;
    const canClick = interactive && !isBooked;
    
    let fillColor = "#4ade80"; // available green
    if (isBooked) fillColor = "#f87171"; // booked red
    if (isSelected) fillColor = "#818cf8"; // selected - purple
    
    return (
      <g
        key={seat.id}
        className={`seat-group ${canClick ? 'clickable' : ''} ${isBooked ? 'booked' : ''}`}
        onClick={() => canClick && onSeatClick && onSeatClick(seat)}
      >
        <circle
          cx={seat.positionX}
          cy={seat.positionY}
          r="14"
          fill={fillColor}
          stroke={isSelected ? '#4f46e5' : 'rgba(0,0,0,0.15)'}
          strokeWidth={isSelected ? 3 : 1.5}
        />
        <text
          x={seat.positionX}
          y={seat.positionY + 1}
          textAnchor="middle"
          dominantBaseline="middle"
          fontSize="9"
          fontWeight="600"
          fill="white"
          pointerEvents="none"
        >
          {seat.label}
        </text>
      </g>
    );
  };
  
  return (
    <div className="room-layout-container">
      <svg
        viewBox={`0 0 ${width} ${height}`}
        className="room-layout-svg"
        style={{ maxWidth: width, maxHeight: height }}
      >
        {/* Grid background */}
        <defs>
          <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
            <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#e5e7eb" strokeWidth="0.5" />
          </pattern>
        </defs>
        <rect width={width} height={height} fill="url(#grid)" />
        <rect width={width} height={height} fill="none" stroke="#d1d5db" strokeWidth="2" />
        
        {/* Walls and doors */}
        {wallSegments.map((seg, i) => (
          seg.type === 'wall' ? (
            <line
              key={i}
              x1={seg.x1} y1={seg.y1}
              x2={seg.x2} y2={seg.y2}
              stroke="#374151"
              strokeWidth="4"
              strokeLinecap="round"
            />
          ) : (
            <g key={i}>
              <line
                x1={seg.x1} y1={seg.y1}
                x2={seg.x2} y2={seg.y2}
                stroke="#6b7280"
                strokeWidth="4"
                strokeLinecap="round"
                strokeDasharray="10 5"
              />
              <text
                x={(seg.x1 + seg.x2)/2}
                y={(seg.y1 + seg.y2)/2 - 8}
                textAnchor="middle"
                fontSize="14"
                fill="#6b7280"
                pointerEvents="none"
              >
                🚪
              </text>
            </g>
          )
        ))}
        
        {/* Desks and seats */}
        {room.desks?.map(desk => (
          <g
            key={desk.id}
            transform={`rotate(${desk.rotation || 0}, ${desk.positionX + desk.width/2}, ${desk.positionY + desk.height/2})`}
          >
            {renderDesk(desk)}
            
            {/* Desk label */}
            <text
              x={desk.positionX + desk.width / 2}
              y={desk.positionY + desk.height/2 + 1}
              textAnchor="middle"
              dominantBaseline="middle"
              fontSize="11"
              fontWeight="500"
              fill="rgba(255,255,255,0.8)"
              pointerEvents="none"
            >
              {desk.shape === 'CIRCLE' || desk.shape === 'SEMICIRCLE' ? '○' : '▭'}
            </text>
            
            {/* Seats inside rotate group so they follow the desk rotation */}
            {desk.seats?.map(seat => renderSeat(seat, desk))}
          </g>
        ))}
      </svg>
      
      {interactive && (
        <div className="seat-legend">
          <span><span className="legend-dot available" /> Available</span>
          <span><span className="legend-dot booked" /> Booked</span>
          <span><span className="legend-dot selected" /> Selected</span>
        </div>
      )}
    </div>
  );
}