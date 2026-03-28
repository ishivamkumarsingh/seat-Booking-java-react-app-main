package com.seatbooking.dto;

import java.util.List;

public class FloorDto {

    public static class CreateRequest {
        private Integer floorNumber;
        private String name;

        public CreateRequest() {}

        public CreateRequest(Integer floorNumber, String name) {
            this.floorNumber = floorNumber;
            this.name = name;
        }

        public Integer getFloorNumber() { return floorNumber; }
        public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Response {
        private Long id;
        private Integer floorNumber;
        private String name;
        private List<RoomDto.Response> rooms;

        public Response() {}

        public Response(Long id, Integer floorNumber, String name, List<RoomDto.Response> rooms) {
            this.id = id;
            this.floorNumber = floorNumber;
            this.name = name;
            this.rooms = rooms;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Integer getFloorNumber() { return floorNumber; }
        public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<RoomDto.Response> getRooms() { return rooms; }
        public void setRooms(List<RoomDto.Response> rooms) { this.rooms = rooms; }

        public static ResponseBuilder builder() {
            return new ResponseBuilder();
        }
    }

    public static class ResponseBuilder {
        private Long id;
        private Integer floorNumber;
        private String name;
        private List<RoomDto.Response> rooms;

        public ResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResponseBuilder floorNumber(Integer floorNumber) {
            this.floorNumber = floorNumber;
            return this;
        }

        public ResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ResponseBuilder rooms(List<RoomDto.Response> rooms) {
            this.rooms = rooms;
            return this;
        }

        public Response build() {
            return new Response(id, floorNumber, name, rooms);
        }
    }

    public static class Summary {
        private Long id;
        private Integer floorNumber;
        private String name;
        private int roomCount;

        public Summary() {}

        public Summary(Long id, Integer floorNumber, String name, int roomCount) {
            this.id = id;
            this.floorNumber = floorNumber;
            this.name = name;
            this.roomCount = roomCount;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Integer getFloorNumber() { return floorNumber; }
        public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getRoomCount() { return roomCount; }
        public void setRoomCount(int roomCount) { this.roomCount = roomCount; }

        public static SummaryBuilder builder() {
            return new SummaryBuilder();
        }
    }

    public static class SummaryBuilder {
        private Long id;
        private Integer floorNumber;
        private String name;
        private int roomCount;

        public SummaryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SummaryBuilder floorNumber(Integer floorNumber) {
            this.floorNumber = floorNumber;
            return this;
        }

        public SummaryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SummaryBuilder roomCount(int roomCount) {
            this.roomCount = roomCount;
            return this;
        }

        public Summary build() {
            return new Summary(id, floorNumber, name, roomCount);
        }
    }
}