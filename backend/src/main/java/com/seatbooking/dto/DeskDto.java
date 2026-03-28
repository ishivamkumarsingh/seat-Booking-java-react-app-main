package com.seatbooking.dto;

import com.seatbooking.model.enums.DeskShape;
import com.seatbooking.model.enums.SeatArrangement;
import java.util.List;

public class DeskDto {

    public static class CreateRequest {
        private Long roomId;
        private DeskShape shape;
        private SeatArrangement seatArrangement;
        private String color;
        private Double positionX;
        private Double positionY;
        private Double width;
        private Double height;
        private Double rotation;
        private Integer numberOfSeats;

        public CreateRequest() {}

        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }

        public DeskShape getShape() { return shape; }
        public void setShape(DeskShape shape) { this.shape = shape; }

        public SeatArrangement getSeatArrangement() { return seatArrangement; }
        public void setSeatArrangement(SeatArrangement seatArrangement) { this.seatArrangement = seatArrangement; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Double getPositionX() { return positionX; }
        public void setPositionX(Double positionX) { this.positionX = positionX; }

        public Double getPositionY() { return positionY; }
        public void setPositionY(Double positionY) { this.positionY = positionY; }

        public Double getWidth() { return width; }
        public void setWidth(Double width) { this.width = width; }

        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }

        public Double getRotation() { return rotation; }
        public void setRotation(Double rotation) { this.rotation = rotation; }

        public Integer getNumberOfSeats() { return numberOfSeats; }
        public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    }

    public static class Response {
        private Long id;
        private Long roomId;
        private DeskShape shape;
        private SeatArrangement seatArrangement;
        private String color;
        private Double positionX;
        private Double positionY;
        private Double width;
        private Double height;
        private Double rotation;
        private List<SeatDto.Response> seats;

        public Response() {}

        public Response(Long id, Long roomId, DeskShape shape, SeatArrangement seatArrangement, 
                        String color, Double positionX, Double positionY, Double width, 
                        Double height, Double rotation, List<SeatDto.Response> seats) {
            this.id = id;
            this.roomId = roomId;
            this.shape = shape;
            this.seatArrangement = seatArrangement;
            this.color = color;
            this.positionX = positionX;
            this.positionY = positionY;
            this.width = width;
            this.height = height;
            this.rotation = rotation;
            this.seats = seats;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }

        public DeskShape getShape() { return shape; }
        public void setShape(DeskShape shape) { this.shape = shape; }

        public SeatArrangement getSeatArrangement() { return seatArrangement; }
        public void setSeatArrangement(SeatArrangement seatArrangement) { this.seatArrangement = seatArrangement; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Double getPositionX() { return positionX; }
        public void setPositionX(Double positionX) { this.positionX = positionX; }

        public Double getPositionY() { return positionY; }
        public void setPositionY(Double positionY) { this.positionY = positionY; }

        public Double getWidth() { return width; }
        public void setWidth(Double width) { this.width = width; }

        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }

        public Double getRotation() { return rotation; }
        public void setRotation(Double rotation) { this.rotation = rotation; }

        public List<SeatDto.Response> getSeats() { return seats; }
        public void setSeats(List<SeatDto.Response> seats) { this.seats = seats; }

        public static ResponseBuilder builder() {
            return new ResponseBuilder();
        }
    }

    public static class ResponseBuilder {
        private Long id;
        private Long roomId;
        private DeskShape shape;
        private SeatArrangement seatArrangement;
        private String color;
        private Double positionX;
        private Double positionY;
        private Double width;
        private Double height;
        private Double rotation;
        private List<SeatDto.Response> seats;

        public ResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResponseBuilder roomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public ResponseBuilder shape(DeskShape shape) {
            this.shape = shape;
            return this;
        }

        public ResponseBuilder seatArrangement(SeatArrangement seatArrangement) {
            this.seatArrangement = seatArrangement;
            return this;
        }

        public ResponseBuilder color(String color) {
            this.color = color;
            return this;
        }

        public ResponseBuilder positionX(Double positionX) {
            this.positionX = positionX;
            return this;
        }

        public ResponseBuilder positionY(Double positionY) {
            this.positionY = positionY;
            return this;
        }

        public ResponseBuilder width(Double width) {
            this.width = width;
            return this;
        }

        public ResponseBuilder height(Double height) {
            this.height = height;
            return this;
        }

        public ResponseBuilder rotation(Double rotation) {
            this.rotation = rotation;
            return this;
        }

        public ResponseBuilder seats(List<SeatDto.Response> seats) {
            this.seats = seats;
            return this;
        }

        public Response build() {
            return new Response(id, roomId, shape, seatArrangement, color, 
                               positionX, positionY, width, height, rotation, seats);
        }
    }

    public static class UpdateRequest {
        private Double positionX;
        private Double positionY;
        private Double width;
        private Double height;
        private Double rotation;

        public UpdateRequest() {}

        public Double getPositionX() { return positionX; }
        public void setPositionX(Double positionX) { this.positionX = positionX; }

        public Double getPositionY() { return positionY; }
        public void setPositionY(Double positionY) { this.positionY = positionY; }

        public Double getWidth() { return width; }
        public void setWidth(Double width) { this.width = width; }

        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }

        public Double getRotation() { return rotation; }
        public void setRotation(Double rotation) { this.rotation = rotation; }
    }
}