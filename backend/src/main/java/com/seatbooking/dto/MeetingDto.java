package com.seatbooking.dto;

import com.seatbooking.model.enums.BookingStatus;
import com.seatbooking.model.enums.DeskShape;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class MeetingDto {

    public static class CreateRequest {
        @NotNull
        private Long deskId;
        @NotNull
        private String title;
        @NotNull
        private LocalDate meetingDate;
        @NotNull
        private LocalTime startTime;
        @NotNull
        private LocalTime endTime;

        public CreateRequest() {}

        public CreateRequest(Long deskId, String title, LocalDate meetingDate, LocalTime startTime, LocalTime endTime) {
            this.deskId = deskId;
            this.title = title;
            this.meetingDate = meetingDate;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Long getDeskId() { return deskId; }
        public void setDeskId(Long deskId) { this.deskId = deskId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public LocalDate getMeetingDate() { return meetingDate; }
        public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }

        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    }

    public static class Response {
        private Long id;
        private Long organizerId;
        private String organizerName;
        private Long deskId;
        private DeskShape deskShape;
        private String roomName;
        private Integer floorNumber;
        private String title;
        private LocalDate meetingDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private BookingStatus status;
        private int seatCount;

        public Response() {}

        public Response(Long id, Long organizerId, String organizerName, Long deskId, DeskShape deskShape,
                        String roomName, Integer floorNumber, String title, LocalDate meetingDate,
                        LocalTime startTime, LocalTime endTime, BookingStatus status, int seatCount) {
            this.id = id;
            this.organizerId = organizerId;
            this.organizerName = organizerName;
            this.deskId = deskId;
            this.deskShape = deskShape;
            this.roomName = roomName;
            this.floorNumber = floorNumber;
            this.title = title;
            this.meetingDate = meetingDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.seatCount = seatCount;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getOrganizerId() { return organizerId; }
        public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }

        public String getOrganizerName() { return organizerName; }
        public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

        public Long getDeskId() { return deskId; }
        public void setDeskId(Long deskId) { this.deskId = deskId; }

        public DeskShape getDeskShape() { return deskShape; }
        public void setDeskShape(DeskShape deskShape) { this.deskShape = deskShape; }

        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }

        public Integer getFloorNumber() { return floorNumber; }
        public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public LocalDate getMeetingDate() { return meetingDate; }
        public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }

        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

        public BookingStatus getStatus() { return status; }
        public void setStatus(BookingStatus status) { this.status = status; }

        public int getSeatCount() { return seatCount; }
        public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

        public static ResponseBuilder builder() {
            return new ResponseBuilder();
        }
    }

    public static class ResponseBuilder {
        private Long id;
        private Long organizerId;
        private Long deskId;
        private String organizerName;
        private String roomName;
        private String title;
        private DeskShape deskShape;
        private Integer floorNumber;
        private LocalDate meetingDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private BookingStatus status;
        private int seatCount;

        public ResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResponseBuilder organizerId(Long organizerId) {
            this.organizerId = organizerId;
            return this;
        }

        public ResponseBuilder organizerName(String organizerName) {
            this.organizerName = organizerName;
            return this;
        }

        public ResponseBuilder deskId(Long deskId) {
            this.deskId = deskId;
            return this;
        }

        public ResponseBuilder deskShape(DeskShape deskShape) {
            this.deskShape = deskShape;
            return this;
        }

        public ResponseBuilder roomName(String roomName) {
            this.roomName = roomName;
            return this;
        }

        public ResponseBuilder floorNumber(Integer floorNumber) {
            this.floorNumber = floorNumber;
            return this;
        }

        public ResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ResponseBuilder meetingDate(LocalDate meetingDate) {
            this.meetingDate = meetingDate;
            return this;
        }

        public ResponseBuilder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ResponseBuilder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public ResponseBuilder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public ResponseBuilder seatCount(int seatCount) {
            this.seatCount = seatCount;
            return this;
        }

        public Response build() {
            return new Response(id, organizerId, organizerName, deskId, deskShape,
                               roomName, floorNumber, title, meetingDate,
                               startTime, endTime, status, seatCount);
        }
    }

    public static class AvailableCircularDesk {
        private Long deskId;
        private Long roomId;
        private String roomName;
        private Integer floorNumber;
        private String floorName;
        private int seatCount;
        private String color;
        private DeskShape shape;

        public AvailableCircularDesk() {}

        public AvailableCircularDesk(Long deskId, Long roomId, String roomName, Integer floorNumber,
                                      String floorName, int seatCount, String color, DeskShape shape) {
            this.deskId = deskId;
            this.roomId = roomId;
            this.roomName = roomName;
            this.floorNumber = floorNumber;
            this.floorName = floorName;
            this.seatCount = seatCount;
            this.color = color;
            this.shape = shape;
        }

        public Long getDeskId() { return deskId; }
        public void setDeskId(Long deskId) { this.deskId = deskId; }

        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }

        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }

        public Integer getFloorNumber() { return floorNumber; }
        public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

        public String getFloorName() { return floorName; }
        public void setFloorName(String floorName) { this.floorName = floorName; }

        public int getSeatCount() { return seatCount; }
        public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public DeskShape getShape() { return shape; }
        public void setShape(DeskShape shape) { this.shape = shape; }

        public static AvailableCircularDeskBuilder builder() {
            return new AvailableCircularDeskBuilder();
        }
    }

    public static class AvailableCircularDeskBuilder {
        private Long deskId;
        private Long roomId;
        private String roomName;
        private String floorName;
        private String color;
        private Integer floorNumber;
        private int seatCount;
        private DeskShape shape;

        public AvailableCircularDeskBuilder deskId(Long deskId) {
            this.deskId = deskId;
            return this;
        }

        public AvailableCircularDeskBuilder roomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public AvailableCircularDeskBuilder roomName(String roomName) {
            this.roomName = roomName;
            return this;
        }

        public AvailableCircularDeskBuilder floorNumber(Integer floorNumber) {
            this.floorNumber = floorNumber;
            return this;
        }

        public AvailableCircularDeskBuilder floorName(String floorName) {
            this.floorName = floorName;
            return this;
        }

        public AvailableCircularDeskBuilder seatCount(int seatCount) {
            this.seatCount = seatCount;
            return this;
        }

        public AvailableCircularDeskBuilder color(String color) {
            this.color = color;
            return this;
        }

        public AvailableCircularDeskBuilder shape(DeskShape shape) {
            this.shape = shape;
            return this;
        }

        public AvailableCircularDesk build() {
            return new AvailableCircularDesk(deskId, roomId, roomName, floorNumber,
                                             floorName, seatCount, color, shape);
        }
    }
}