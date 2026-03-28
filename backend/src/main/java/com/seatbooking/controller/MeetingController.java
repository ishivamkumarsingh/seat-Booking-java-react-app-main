
package com.seatbooking.controller;

import com.seatbooking.dto.MeetingDto;
import com.seatbooking.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/available-desks")
    public ResponseEntity<List<MeetingDto.AvailableCircularDesk>> getAvailableCircularDesks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return ResponseEntity.ok(meetingService.getAvailableCircularDesks(date, startTime, endTime));
    }

    @PostMapping
    public ResponseEntity<MeetingDto.Response> bookMeeting(
            Authentication auth, @Valid @RequestBody MeetingDto.CreateRequest request) {
        return ResponseEntity.ok(meetingService.bookMeeting(auth.getName(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MeetingDto.Response> cancelMeeting(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(meetingService.cancelMeeting(auth.getName(), id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<MeetingDto.Response>> getMyMeetings(Authentication auth) {
        return ResponseEntity.ok(meetingService.getUserMeetings(auth.getName()));
    }
}