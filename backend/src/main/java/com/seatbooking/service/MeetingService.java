package com.seatbooking.service;

import com.seatbooking.dto.MeetingDto;
import com.seatbooking.exception.BadRequestException;
import com.seatbooking.exception.ConflictException;
import com.seatbooking.exception.ResourceNotFoundException;
import com.seatbooking.model.*;
import com.seatbooking.model.enums.BookingStatus;
import com.seatbooking.model.enums.DeskShape;
import com.seatbooking.model.enums.SeatArrangement;
import com.seatbooking.model.enums.UserRole;
import com.seatbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MeetingService {

    private static final long MAX_MEETING_HOURS = 6;

    private final MeetingRepository meetingRepository;
    private final BookingRepository bookingRepository;
    private final DeskRepository deskRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public MeetingService(MeetingRepository meetingRepository,
                          BookingRepository bookingRepository,
                          DeskRepository deskRepository,
                          SeatRepository seatRepository,
                          UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.bookingRepository = bookingRepository;
        this.deskRepository = deskRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all circular desks that are available for a meeting at the given time.
     * Eligible desks are either circular in shape OR have circular seat arrangement.
     */
    @Transactional(readOnly = true)
    public List<MeetingDto.AvailableCircularDesk> getAvailableCircularDesks(LocalDate date, 
                                                                             LocalTime startTime, 
                                                                             LocalTime endTime) {
        List<Desk> eligibleDesks = deskRepository.findAll().stream()
                .filter(desk -> desk.getShape() == DeskShape.CIRCLE || desk.getSeatArrangement() == SeatArrangement.CIRCLE)
                .filter(desk -> {
                    List<Long> seatIds = seatRepository.findByDeskId(desk.getId()).stream()
                            .map(Seat::getId)
                            .collect(Collectors.toList());
                    return !seatIds.isEmpty();
                })
                .collect(Collectors.toList());

        List<MeetingDto.AvailableCircularDesk> strictMatches = eligibleDesks.stream()
                .filter(desk -> {
                    // Check no meeting conflicts
                    List<Meeting> meetingConflicts = meetingRepository.findConflictingMeetings(
                            desk.getId(), date, startTime, endTime);
                    if (!meetingConflicts.isEmpty()) {
                        return false;
                    }

                    // Meeting is possible if at least one seat at the desk is still free.
                    List<Long> seatIds = seatRepository.findByDeskId(desk.getId()).stream()
                            .map(Seat::getId)
                            .collect(Collectors.toList());

                    if (seatIds.isEmpty()) {
                        return false;
                    }

                    List<Booking> seatConflicts = bookingRepository.findConflictingBookingsForSeats(
                            seatIds, date, startTime, endTime);

                    long distinctConflictingSeatIds = seatConflicts.stream()
                            .map(b -> b.getSeat().getId())
                            .distinct()
                            .count();

                    return distinctConflictingSeatIds < seatIds.size();
                })
                .map(desk -> MeetingDto.AvailableCircularDesk.builder()
                        .deskId(desk.getId())
                        .roomId(desk.getRoom().getId())
                        .roomName(desk.getRoom().getName())
                        .floorNumber(desk.getRoom().getFloor().getFloorNumber())
                        .floorName(desk.getRoom().getFloor().getName())
                        .seatCount(desk.getSeats().size())
                        .color(desk.getColor())
                        .shape(desk.getShape())
                        .build())
                .sorted((a, b) -> {
                    int floorCmp = Integer.compare(a.getFloorNumber(), b.getFloorNumber());
                    if (floorCmp != 0) return floorCmp;
                    return a.getRoomName().compareToIgnoreCase(b.getRoomName());
                })
                .collect(Collectors.toList());

        if (!strictMatches.isEmpty()) {
            return strictMatches;
        }

        // Fallback for UX: show circular-eligible desks even when strict filters remove all.
        // Final availability is still enforced by bookMeeting() at booking time.
        return eligibleDesks.stream()
                .map(desk -> MeetingDto.AvailableCircularDesk.builder()
                        .deskId(desk.getId())
                        .roomId(desk.getRoom().getId())
                        .roomName(desk.getRoom().getName())
                        .floorNumber(desk.getRoom().getFloor().getFloorNumber())
                        .floorName(desk.getRoom().getFloor().getName())
                        .seatCount(desk.getSeats().size())
                        .color(desk.getColor())
                        .shape(desk.getShape())
                        .build())
                .sorted((a, b) -> {
                    int floorCmp = Integer.compare(a.getFloorNumber(), b.getFloorNumber());
                    if (floorCmp != 0) return floorCmp;
                    return a.getRoomName().compareToIgnoreCase(b.getRoomName());
                })
                .collect(Collectors.toList());
    }

    public MeetingDto.Response createMeeting(String username, MeetingDto.CreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Desk desk = deskRepository.findById(request.getDeskId())
                .orElseThrow(() -> new ResourceNotFoundException("Desk not found"));

        // Validate meeting duration (max 6 hours)
        long durationHours = Duration.between(request.getStartTime(), request.getEndTime()).toHours();
        if (durationHours <= 0 || durationHours > MAX_MEETING_HOURS) {
            throw new BadRequestException("Meeting duration must be between 1 and " + MAX_MEETING_HOURS + " hours");
        }

        // Validate meeting date is not in the past
        if (request.getMeetingDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book meetings in the past");
        }

        // Check for conflicting meetings
        List<Meeting> meetingConflicts = meetingRepository.findConflictingMeetings(
                desk.getId(),
                request.getMeetingDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!meetingConflicts.isEmpty()) {
            throw new ConflictException("This circular desk is already booked for the selected time slot");
        }

        // Check for individual seat booking conflicts
        List<Long> seatIds = seatRepository.findByDeskId(request.getDeskId()).stream()
                .map(Seat::getId)
                .collect(Collectors.toList());

        if (!seatIds.isEmpty()) {
            List<Booking> seatConflicts = bookingRepository.findConflictingBookingsForSeats(
                    seatIds,
                    request.getMeetingDate(),
                    request.getStartTime(),
                    request.getEndTime()
            );
            long distinctConflictingSeatIds = seatConflicts.stream()
                    .map(b -> b.getSeat().getId())
                    .distinct()
                    .count();

            if (distinctConflictingSeatIds == seatIds.size()) {
                throw new ConflictException("No seats available in this circular desk for the selected time");
            }
        }

        Meeting meeting = Meeting.builder()
                .organizer(user)
                .desk(desk)
                .title(request.getTitle())
                .meetingDate(request.getMeetingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.CONFIRMED)
                .build();

        meeting = meetingRepository.save(meeting);
        return toMeetingResponse(meeting);
    }

    public MeetingDto.Response cancelMeeting(String username, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));

        if (!meeting.getOrganizer().getUsername().equals(username)) {
            throw new BadRequestException("You can only cancel your own meetings");
        }

        meeting.setStatus(BookingStatus.CANCELLED);
        meeting = meetingRepository.save(meeting);
        return toMeetingResponse(meeting);
    }

    @Transactional(readOnly = true)
    public List<MeetingDto.Response> getUserMeetings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return meetingRepository.findByOrganizerId(user.getId()).stream()
                .map(this::toMeetingResponse)
                .collect(Collectors.toList());
    }
    public MeetingDto.Response bookMeeting(String username, MeetingDto.CreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != UserRole.MANAGER && user.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Only managers can book meetings");
        }

        Desk desk = deskRepository.findById(request.getDeskId())
                .orElseThrow(() -> new ResourceNotFoundException("Desk not found"));

        boolean isMeetingEligible = desk.getShape() == DeskShape.CIRCLE || desk.getSeatArrangement() == SeatArrangement.CIRCLE;
        if (!isMeetingEligible) {
            throw new BadRequestException("Meetings can only be booked at circular desks or circular seat arrangements");
        }

        long durationHours = Duration.between(request.getStartTime(), request.getEndTime()).toHours();
        if (durationHours <= 0 || durationHours > MAX_MEETING_HOURS) {
            throw new BadRequestException("Meeting duration must be between 1 and " + MAX_MEETING_HOURS + " hours");
        }

        if (request.getMeetingDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book meetings in the past");
        }

        // Check for conflicting meetings
        List<Meeting> meetingConflicts = meetingRepository.findConflictingMeetings(
                desk.getId(),
                request.getMeetingDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!meetingConflicts.isEmpty()) {
            throw new ConflictException("This circular desk is already booked for the selected time slot");
        }

        // Check for individual seat booking conflicts
        List<Long> seatIds = seatRepository.findByDeskId(request.getDeskId()).stream()
                .map(Seat::getId)
                .collect(Collectors.toList());

        if (!seatIds.isEmpty()) {
            List<Booking> seatConflicts = bookingRepository.findConflictingBookingsForSeats(
                    seatIds,
                    request.getMeetingDate(),
                    request.getStartTime(),
                    request.getEndTime()
            );
            if (!seatConflicts.isEmpty()) {
                throw new ConflictException("Some seats at this desk are already booked for the selected time");
            }
        }

        Meeting meeting = Meeting.builder()
                .organizer(user)
                .desk(desk)
                .title(request.getTitle())
                .meetingDate(request.getMeetingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.CONFIRMED)
                .build();

        meeting = meetingRepository.save(meeting);
        return toMeetingResponse(meeting);
    }

    private MeetingDto.Response toMeetingResponse(Meeting meeting) {
        return MeetingDto.Response.builder()
                .id(meeting.getId())
                .organizerId(meeting.getOrganizer().getId())
                .organizerName(meeting.getOrganizer().getFullName())
                .deskId(meeting.getDesk().getId())
                .deskShape(meeting.getDesk().getShape())
                .roomName(meeting.getDesk().getRoom().getName())
                .floorNumber(meeting.getDesk().getRoom().getFloor().getFloorNumber())
                .title(meeting.getTitle())
                .meetingDate(meeting.getMeetingDate())
                .startTime(meeting.getStartTime())
                .endTime(meeting.getEndTime())
                .status(meeting.getStatus())
                .seatCount(meeting.getDesk().getSeats().size())
                .build();
    }
}