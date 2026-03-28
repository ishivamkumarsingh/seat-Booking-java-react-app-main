package com.seatbooking.dto;

import com.seatbooking.model.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDto {

    public static class CreateRequest {
        @NotNull
        private Long seatId;

        @NotNull
        private LocalDate bookingDate;

        @NotNull
        private LocalTime startTime;

        @NotNull
        private LocalTime endTime;

        public CreateRequest() {}

        public CreateRequest(Long seatId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime) {
            this.seatId = seatId;
            this.bookingDate = bookingDate;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }

        public LocalDate getBookingDate() { return bookingDate; }
        public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    }

    public static class Response {
        private Long id;
        private Long userId;
        private String userName;
        private Long seatId;
        private String seatLabel;
        private String roomName;
        private Integer floorNumber;
        private LocalDate bookingDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private BookingStatus status;

        public Response() {}

        public Response(Long id, Long userId, String userName, Long seatId, String seatLabel, 
                        String roomName, Integer floorNumber, LocalDate bookingDate, 
                        LocalTime startTime, LocalTime endTime, BookingStatus status) {
            this.id = id;
            this.userId = userId;
            this.userName = userName;
            this.seatId = seatId;
            this.seatLabel = seatLabel;
            this.roomName = roomName;
            this.floorNumber = floorNumber;
            this.bookingDate = bookingDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }

        public String getSeatLabel() { return seatLabel; }
        public void setSeatLabel(String seatLabel) { this.seatLabel = seatLabel; }

        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }

        public Integer getFloorNumber() { return floorNumber; }
        public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

        public LocalDate getBookingDate() { return bookingDate; }
        public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

        public BookingStatus getStatus() { return status; }
        public void setStatus(BookingStatus status) { this.status = status; }

        public static ResponseBuilder builder() {
            return new ResponseBuilder();
        }
    }

    public static class ResponseBuilder {
        private Long id;
        private Long userId;
        private Long seatId;
        private String userName;
        private String seatLabel;
        private String roomName;
        private Integer floorNumber;
        private LocalDate bookingDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private BookingStatus status;

        public ResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ResponseBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public ResponseBuilder seatId(Long seatId) {
            this.seatId = seatId;
            return this;
        }

        public ResponseBuilder seatLabel(String seatLabel) {
            this.seatLabel = seatLabel;
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

        public ResponseBuilder bookingDate(LocalDate bookingDate) {
            this.bookingDate = bookingDate;
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

        public Response build() {
            return new Response(id, userId, userName, seatId, seatLabel, roomName, 
                               floorNumber, bookingDate, startTime, endTime, status);
        }
    }
}