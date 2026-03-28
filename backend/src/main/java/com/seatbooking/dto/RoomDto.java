package com.seatbooking.dto;

import java.util.List;

public class RoomDto {

    public static class CreateRequest {
        private Long floorId;
        private String name;
        private Integer roomNumber;
        private Integer width;
        private Integer height;

        public CreateRequest() {}

        public CreateRequest(Long floorId, String name, Integer roomNumber, Integer width, Integer height) {
            this.floorId = floorId;
            this.name = name;
            this.roomNumber = roomNumber;
            this.width = width;
            this.height = height;
        }

        public Long getFloorId() { return floorId; }
        public void setFloorId(Long floorId) { this.floorId = floorId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getRoomNumber() { return roomNumber; }
        public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }

        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }

        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
    }

    public static class Response {
        private Long id;
        private Long floorId;
        private Integer floorNumber;
        private String name;
        private Integer roomNumber;
        private Integer width;
        private Integer height;
        private String walls;
        private List<DeskDto.Response> desks;

        public Response() {}

        public Response(Long id, Long floorId, Integer floorNumber, String name, Integer roomNumber,
                        Integer width, Integer height, String walls, List<DeskDto.Response> desks) {
            this.id = id;
            this.floorId = floorId;
            this.floorNumber = floorNumber;
            this.name = name;
            this.roomNumber = roomNumber;
            this.width = width;
            this.height = height;
            this.walls = walls;
            this.desks = desks;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getFloorId() { return floorId; }
        public void setFloorId(Long floorId) { this.floorId = floorId; }

        public Integer getFloorNumber() { return floorNumber; }
        public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getRoomNumber() { return roomNumber; }
        public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }

        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }

        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }

        public String getWalls() { return walls; }
        public void setWalls(String walls) { this.walls = walls; }

        public List<DeskDto.Response> getDesks() { return desks; }
        public void setDesks(List<DeskDto.Response> desks) { this.desks = desks; }

        public static ResponseBuilder builder() {
            return new ResponseBuilder();
        }
    }

    public static class ResponseBuilder {
        private Long id;
        private Long floorId;
        private Integer floorNumber;
        private Integer roomNumber;
        private Integer width;
        private Integer height;
        private String name;
        private String walls;
        private List<DeskDto.Response> desks;

        public ResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResponseBuilder floorId(Long floorId) {
            this.floorId = floorId;
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

        public ResponseBuilder roomNumber(Integer roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }

        public ResponseBuilder width(Integer width) {
            this.width = width;
            return this;
        }

        public ResponseBuilder height(Integer height) {
            this.height = height;
            return this;
        }

        public ResponseBuilder walls(String walls) {
            this.walls = walls;
            return this;
        }

        public ResponseBuilder desks(List<DeskDto.Response> desks) {
            this.desks = desks;
            return this;
        }

        public Response build() {
            return new Response(id, floorId, floorNumber, name, roomNumber,
                               width, height, walls, desks);
        }
    }

    public static class UpdateWallsRequest {
        private String walls;

        public UpdateWallsRequest() {}

        public String getWalls() { return walls; }
        public void setWalls(String walls) { this.walls = walls; }
    }
}