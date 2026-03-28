package com.seatbooking.controller;

import com.seatbooking.dto.BookingDto;
import com.seatbooking.dto.FloorDto;
import com.seatbooking.dto.RoomDto;
import com.seatbooking.dto.SeatDto;
import com.seatbooking.service.AdminService;
import com.seatbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final AdminService adminService;

    public BookingController(BookingService bookingService, AdminService adminService) {
        this.bookingService = bookingService;
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<BookingDto.Response> createBooking(
            Authentication auth, @Valid @RequestBody BookingDto.CreateRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(auth.getName(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingDto.Response> cancelBooking(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(auth.getName(), id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingDto.Response>> getMyBookings(Authentication auth) {
        return ResponseEntity.ok(bookingService.getUserBookings(auth.getName()));
    }

    @GetMapping("/floors")
    public ResponseEntity<List<FloorDto.Summary>> getFloors() {
        return ResponseEntity.ok(adminService.getAllFloors());
    }

    @GetMapping("/floors/{floorId}")
    public ResponseEntity<FloorDto.Response> getFloorDetails(@PathVariable Long floorId) {
        return ResponseEntity.ok(adminService.getFloorDetails(floorId));
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDto.Response> getRoomDetails(@PathVariable Long roomId) {
        return ResponseEntity.ok(adminService.getRoomDetails(roomId));
    }

    @GetMapping("/rooms/{roomId}/seats")
    public ResponseEntity<List<SeatDto.Response>> getAvailableSeats(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return ResponseEntity.ok(bookingService.getAvailableSeats(roomId, date, startTime, endTime));
    }
}