package com.seatbooking.service;

import com.seatbooking.dto.FloorDto;
import com.seatbooking.dto.RoomDto;
import com.seatbooking.dto.DeskDto;
import com.seatbooking.dto.SeatDto;
import com.seatbooking.exception.ResourceNotFoundException;
import com.seatbooking.model.*;
import com.seatbooking.model.enums.DeskShape;
import com.seatbooking.model.enums.SeatArrangement;
import com.seatbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final DeskRepository deskRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final MeetingRepository meetingRepository;

    public AdminService(FloorRepository floorRepository, 
                        RoomRepository roomRepository, 
                        DeskRepository deskRepository, 
                        SeatRepository seatRepository, 
                        BookingRepository bookingRepository, 
                        MeetingRepository meetingRepository) {
        this.floorRepository = floorRepository;
        this.roomRepository = roomRepository;
        this.deskRepository = deskRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.meetingRepository = meetingRepository;
    }

    // FLOOR operations
    public FloorDto.Summary createFloor(FloorDto.CreateRequest request) {
        Floor floor = Floor.builder()
                .floorNumber(request.getFloorNumber())
                .name(request.getName())
                .build();
        floor = floorRepository.save(floor);
        return toFloorSummary(floor);
    }

    public List<FloorDto.Summary> getAllFloors() {
        return floorRepository.findAll().stream()
                .map(this::toFloorSummary)
                .collect(Collectors.toList());
    }

    public FloorDto.Response getFloorDetails(Long floorId) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found"));
        return toFloorResponse(floor);
    }

    public void deleteFloor(Long floorId) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found"));
        // Delete bookings and meetings referencing this floor's seats/desks
        for (Room room : floor.getRooms()) {
            deleteRoomDependencies(room);
        }
        floorRepository.deleteById(floorId);
    }

    // ROOM operations
    public RoomDto.Response createRoom(RoomDto.CreateRequest request) {
        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found"));

        Room room = Room.builder()
                .floor(floor)
                .name(request.getName())
                .roomNumber(request.getRoomNumber())
                .width(request.getWidth() != null ? request.getWidth() : 800)
                .height(request.getHeight() != null ? request.getHeight() : 600)
                .build();
        room = roomRepository.save(room);
        return toRoomResponse(room);
    }

    public List<RoomDto.Response> getRoomsByFloor(Long floorId) {
        return roomRepository.findByFloorId(floorId).stream()
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    public RoomDto.Response getRoomDetails(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        return toRoomResponse(room);
    }

    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        deleteRoomDependencies(room);
        roomRepository.deleteById(roomId);
    }

    private void deleteRoomDependencies(Room room) {
        if (room.getDesks() == null) return;

        List<Long> deskIds = room.getDesks().stream()
                .map(Desk::getId)
                .collect(Collectors.toList());

        List<Long> seatIds = room.getDesks().stream()
                .filter(d -> d.getSeats() != null)
                .flatMap(d -> d.getSeats().stream())
                .map(Seat::getId)
                .collect(Collectors.toList());

        if (!deskIds.isEmpty()) {
            meetingRepository.deleteByDeskIdIn(deskIds);
        }
        if (!seatIds.isEmpty()) {
            bookingRepository.deleteBySeatIdIn(seatIds);
        }
    }

    // DESK operations
    public DeskDto.Response createDesk(DeskDto.CreateRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        Desk desk = Desk.builder()
                .room(room)
                .shape(request.getShape())
                .seatArrangement(request.getSeatArrangement())
                .color(request.getColor() != null ? request.getColor() : "#884513")
                .positionX(request.getPositionX())
                .positionY(request.getPositionY())
                .width(request.getWidth())
                .height(request.getHeight())
                .rotation(request.getRotation() != null ? request.getRotation() : 0.0)
                .build();
        desk = deskRepository.save(desk);

        // Auto-generate seats around the desk
        int numSeats = request.getNumberOfSeats() != null ? request.getNumberOfSeats() : 4;
        List<Seat> seats = generateSeats(desk, numSeats);
        seatRepository.saveAll(seats);
        desk.setSeats(seats);

        return toDeskResponse(desk);
    }

    public void deleteDesk(Long deskId) {
        Desk desk = deskRepository.findById(deskId)
                .orElseThrow(() -> new ResourceNotFoundException("Desk not found"));
        
        meetingRepository.deleteByDeskId(deskId);
        
        if (desk.getSeats() != null) {
            List<Long> seatIds = desk.getSeats().stream()
                    .map(Seat::getId)
                    .collect(Collectors.toList());
            if (!seatIds.isEmpty()) {
                bookingRepository.deleteBySeatIdIn(seatIds);
            }
        }
        deskRepository.deleteById(deskId);
    }

    public DeskDto.Response updateDeskGeometry(Long deskId, DeskDto.UpdateRequest request) {
        Desk desk = deskRepository.findById(deskId)
                .orElseThrow(() -> new ResourceNotFoundException("Desk not found"));

        double dx = (request.getPositionX() != null ? request.getPositionX() : desk.getPositionX()) - desk.getPositionX();
        double dy = (request.getPositionY() != null ? request.getPositionY() : desk.getPositionY()) - desk.getPositionY();

        boolean sizeChanged = (request.getWidth() != null && !request.getWidth().equals(desk.getWidth()))
                || (request.getHeight() != null && !request.getHeight().equals(desk.getHeight()));

        if (request.getPositionX() != null) {
            desk.setPositionX(request.getPositionX());
        }
        if (request.getPositionY() != null) {
            desk.setPositionY(request.getPositionY());
        }
        if (request.getWidth() != null) {
            desk.setWidth(request.getWidth());
        }
        if (request.getHeight() != null) {
            desk.setHeight(request.getHeight());
        }
        if (request.getRotation() != null) {
            desk.setRotation(request.getRotation());
        }

        desk = deskRepository.save(desk);

        List<Seat> seats = desk.getSeats();
        if (seats != null && !seats.isEmpty()) {
            if (sizeChanged) {
                List<Seat> newPositions = generateSeats(desk, seats.size());
                for (int i = 0; i < Math.min(seats.size(), newPositions.size()); i++) {
                    seats.get(i).setPositionX(newPositions.get(i).getPositionX());
                    seats.get(i).setPositionY(newPositions.get(i).getPositionY());
                }
            } else if (dx != 0 || dy != 0) {
                for (Seat s : seats) {
                    s.setPositionX(s.getPositionX() + dx);
                    s.setPositionY(s.getPositionY() + dy);
                }
            }
            seatRepository.saveAll(seats);
            desk.setSeats(seats);
        }

        return toDeskResponse(desk);
    }

    public RoomDto.Response updateRoomWalls(Long roomId, RoomDto.UpdateWallsRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        room.setWalls(request.getWalls());
        room = roomRepository.save(room);
        return toRoomResponse(room);
    }

    // Seat position generation
    private List<Seat> generateSeats(Desk desk, int numSeats) {
        List<Seat> seats = new ArrayList<>();

        double centerX = desk.getPositionX() + desk.getWidth() / 2;
        double centerY = desk.getPositionY() + desk.getHeight() / 2;

        if (desk.getSeatArrangement() == SeatArrangement.CIRCLE) {
            // Place seats in a circle around the desk
            double radius = Math.max(desk.getWidth(), desk.getHeight()) / 2 + 30;
            for (int i = 0; i < numSeats; i++) {
                double angle = (2 * Math.PI * i) / numSeats;
                double seatX = centerX + radius * Math.cos(angle);
                double seatY = centerY + radius * Math.sin(angle);
                seats.add(Seat.builder()
                        .desk(desk)
                        .label("S" + (i + 1))
                        .seatIndex(i)
                        .positionX(seatX)
                        .positionY(seatY)
                        .build());
            }
        } else {
            // RECTANGLE arrangement seats around all 4 sides
            double deskX = desk.getPositionX();
            double deskY = desk.getPositionY();
            double w = desk.getWidth();
            double h = desk.getHeight();
            double offset = 30;

            int seatsPerSide = Math.max(1, numSeats / 4);
            int remaining = numSeats;
            int seatIdx = 0;

            // Top side
            int topSeats = Math.min(seatsPerSide, remaining);
            for (int i = 0; i < topSeats; i++) {
                double seatX = deskX + (w / (topSeats + 1)) * (i + 1);
                double seatY = deskY - offset;
                seats.add(Seat.builder()
                        .desk(desk)
                        .label("S" + (seatIdx + 1))
                        .seatIndex(seatIdx)
                        .positionX(seatX)
                        .positionY(seatY)
                        .build());
                seatIdx++;
            }
            remaining -= topSeats;

            // Right side
            int rightSeats = Math.min(seatsPerSide, remaining);
            for (int i = 0; i < rightSeats; i++) {
                double seatX = deskX + w + offset;
                double seatY = deskY + (h / (rightSeats + 1)) * (i + 1);
                seats.add(Seat.builder()
                        .desk(desk)
                        .label("S" + (seatIdx + 1))
                        .seatIndex(seatIdx)
                        .positionX(seatX)
                        .positionY(seatY)
                        .build());
                seatIdx++;
            }
            remaining -= rightSeats;

            // Bottom side
            int bottomSeats = Math.min(seatsPerSide, remaining);
            for (int i = 0; i < bottomSeats; i++) {
                double seatX = deskX + (w / (bottomSeats + 1)) * (i + 1);
                double seatY = deskY + h + offset;
                seats.add(Seat.builder()
                        .desk(desk)
                        .label("S" + (seatIdx + 1))
                        .seatIndex(seatIdx)
                        .positionX(seatX)
                        .positionY(seatY)
                        .build());
                seatIdx++;
            }
            remaining -= bottomSeats;

            // Left side
            for (int i = 0; i < remaining; i++) {
                double seatX = deskX - offset;
                double seatY = deskY + (h / (remaining + 1)) * (i + 1);
                seats.add(Seat.builder()
                        .desk(desk)
                        .label("S" + (seatIdx + 1))
                        .seatIndex(seatIdx)
                        .positionX(seatX)
                        .positionY(seatY)
                        .build());
                seatIdx++;
            }
        }

        return seats;
    }

    // Mapping helpers
    private FloorDto.Summary toFloorSummary(Floor floor) {
        return FloorDto.Summary.builder()
                .id(floor.getId())
                .floorNumber(floor.getFloorNumber())
                .name(floor.getName())
                .roomCount(floor.getRooms() != null ? floor.getRooms().size() : 0)
                .build();
    }

    private FloorDto.Response toFloorResponse(Floor floor) {
        return FloorDto.Response.builder()
                .id(floor.getId())
                .floorNumber(floor.getFloorNumber())
                .name(floor.getName())
                .rooms(floor.getRooms() != null
                        ? floor.getRooms().stream().map(this::toRoomResponse).collect(Collectors.toList())
                        : List.of())
                .build();
    }

    private RoomDto.Response toRoomResponse(Room room) {
        return RoomDto.Response.builder()
                .id(room.getId())
                .floorId(room.getFloor().getId())
                .floorNumber(room.getFloor().getFloorNumber())
                .name(room.getName())
                .roomNumber(room.getRoomNumber())
                .width(room.getWidth())
                .height(room.getHeight())
                .walls(room.getWalls())
                .desks(room.getDesks() != null
                        ? room.getDesks().stream().map(this::toDeskResponse).collect(Collectors.toList())
                        : List.of())
                .build();
    }

    private DeskDto.Response toDeskResponse(Desk desk) {
        return DeskDto.Response.builder()
                .id(desk.getId())
                .roomId(desk.getRoom().getId())
                .shape(desk.getShape())
                .seatArrangement(desk.getSeatArrangement())
                .color(desk.getColor())
                .positionX(desk.getPositionX())
                .positionY(desk.getPositionY())
                .width(desk.getWidth())
                .height(desk.getHeight())
                .rotation(desk.getRotation() != null ? desk.getRotation() : 0.0)
                .seats(desk.getSeats() != null
                        ? desk.getSeats().stream().map(this::toSeatResponse).collect(Collectors.toList())
                        : List.of())
                .build();
    }

    private SeatDto.Response toSeatResponse(Seat seat) {
        return SeatDto.Response.builder()
                .id(seat.getId())
                .deskId(seat.getDesk().getId())
                .label(seat.getLabel())
                .seatIndex(seat.getSeatIndex())
                .positionX(seat.getPositionX())
                .positionY(seat.getPositionY())
                .booked(false)
                .build();
    }
}