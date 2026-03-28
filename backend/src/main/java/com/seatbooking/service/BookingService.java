package com.seatbooking.service;

import com.seatbooking.dto.BookingDto;
import com.seatbooking.dto.SeatDto;
import com.seatbooking.exception.BadRequestException;
import com.seatbooking.exception.ConflictException;
import com.seatbooking.exception.ResourceNotFoundException;
import com.seatbooking.model.Booking;
import com.seatbooking.model.Seat;
import com.seatbooking.model.User;
import com.seatbooking.model.enums.BookingStatus;
import com.seatbooking.repository.BookingRepository;
import com.seatbooking.repository.SeatRepository;
import com.seatbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {

    private static final long MAX_BOOKING_HOURS = 6;

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, 
                          SeatRepository seatRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    public BookingDto.Response createBooking(String username, BookingDto.CreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        // Validate booking duration (max 6 hours)
        long durationHours = Duration.between(request.getStartTime(), request.getEndTime()).toHours();
        if (durationHours <= 0 || durationHours > MAX_BOOKING_HOURS) {
            throw new BadRequestException("Booking duration must be between 1 and " + MAX_BOOKING_HOURS + " hours");
        }

        // Validate booking date is not in the past
        if (request.getBookingDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book seats in the past");
        }

        // Check for conflicting bookings (handles concurrent access via DB query optimistic locking)
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.getSeatId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new ConflictException("This seat is already booked for the selected time slot");
        }

        Booking booking = Booking.builder()
                .user(user)
                .seat(seat)
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.CONFIRMED)
                .build();

        booking = bookingRepository.save(booking);
        return toBookingResponse(booking);
    }

    public BookingDto.Response cancelBooking(String username, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new BadRequestException("You can only cancel your own bookings");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        return toBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto.Response> getUserBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(this::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeatDto.Response> getAvailableSeats(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Get all seats in the room through desks
        List<Seat> allSeats = seatRepository.findAll().stream()
                .filter(s -> s.getDesk().getRoom().getId().equals(roomId))
                .collect(Collectors.toList());

        List<Long> seatIds = allSeats.stream()
                .map(Seat::getId)
                .collect(Collectors.toList());

        // Find which seats are booked
        Set<Long> bookedSeatIds = bookingRepository.findConflictingBookingsForSeats(seatIds, date, startTime, endTime)
                .stream()
                .map(b -> b.getSeat().getId())
                .collect(Collectors.toSet());

        return allSeats.stream()
                .map(seat -> SeatDto.Response.builder()
                        .id(seat.getId())
                        .deskId(seat.getDesk().getId())
                        .label(seat.getLabel())
                        .seatIndex(seat.getSeatIndex())
                        .positionX(seat.getPositionX())
                        .positionY(seat.getPositionY())
                        .booked(bookedSeatIds.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    private BookingDto.Response toBookingResponse(Booking booking) {
        return BookingDto.Response.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getFullName())
                .seatId(booking.getSeat().getId())
                .seatLabel(booking.getSeat().getLabel())
                .roomName(booking.getSeat().getDesk().getRoom().getName())
                .floorNumber(booking.getSeat().getDesk().getRoom().getFloor().getFloorNumber())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .build();
    }
}