package com.seatbooking.dto;

public class SeatDto {

    public static class Response {
        private Long id;
        private Long deskId;
        private String label;
        private Integer seatIndex;
        private Double positionX;
        private Double positionY;
        private boolean booked;

        public Response() {}

        public Response(Long id, Long deskId, String label, Integer seatIndex, 
                        Double positionX, Double positionY, boolean booked) {
            this.id = id;
            this.deskId = deskId;
            this.label = label;
            this.seatIndex = seatIndex;
            this.positionX = positionX;
            this.positionY = positionY;
            this.booked = booked;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getDeskId() { return deskId; }
        public void setDeskId(Long deskId) { this.deskId = deskId; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Integer getSeatIndex() { return seatIndex; }
        public void setSeatIndex(Integer seatIndex) { this.seatIndex = seatIndex; }

        public Double getPositionX() { return positionX; }
        public void setPositionX(Double positionX) { this.positionX = positionX; }

        public Double getPositionY() { return positionY; }
        public void setPositionY(Double positionY) { this.positionY = positionY; }

        public boolean isBooked() { return booked; }
        public void setBooked(boolean booked) { this.booked = booked; }

        public static ResponseBuilder builder() {
            return new ResponseBuilder();
        }
    }

    public static class ResponseBuilder {
        private Long id;
        private Long deskId;
        private String label;
        private Integer seatIndex;
        private Double positionX;
        private Double positionY;
        private boolean booked;

        public ResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResponseBuilder deskId(Long deskId) {
            this.deskId = deskId;
            return this;
        }

        public ResponseBuilder label(String label) {
            this.label = label;
            return this;
        }

        public ResponseBuilder seatIndex(Integer seatIndex) {
            this.seatIndex = seatIndex;
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

        public ResponseBuilder booked(boolean booked) {
            this.booked = booked;
            return this;
        }

        public Response build() {
            return new Response(id, deskId, label, seatIndex, positionX, positionY, booked);
        }
    }
}