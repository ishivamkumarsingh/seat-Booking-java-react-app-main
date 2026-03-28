package com.seatbooking.model;

import com.seatbooking.model.enums.DeskShape;
import com.seatbooking.model.enums.SeatArrangement;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "desks")
public class Desk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeskShape shape;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatArrangement seatArrangement;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Double positionX;

    @Column(nullable = false)
    private Double positionY;

    @Column(nullable = false)
    private Double width;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double rotation = 0.0;

    @OneToMany(mappedBy = "desk", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    public Desk() {}

    public Desk(Long id, Room room, DeskShape shape, SeatArrangement seatArrangement, 
                String color, Double positionX, Double positionY, Double width, 
                Double height, Double rotation, List<Seat> seats) {
        this.id = id;
        this.room = room;
        this.shape = shape;
        this.seatArrangement = seatArrangement;
        this.color = color;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.rotation = rotation != null ? rotation : 0.0;
        this.seats = seats != null ? seats : new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

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

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }

    public static DeskBuilder builder() {
        return new DeskBuilder();
    }

    public static class DeskBuilder {
        private Long id;
        private Room room;
        private DeskShape shape;
        private SeatArrangement seatArrangement;
        private String color;
        private Double positionX;
        private Double positionY;
        private Double width;
        private Double height;
        private Double rotation = 0.0;
        private List<Seat> seats = new ArrayList<>();

        public DeskBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DeskBuilder room(Room room) {
            this.room = room;
            return this;
        }

        public DeskBuilder shape(DeskShape shape) {
            this.shape = shape;
            return this;
        }

        public DeskBuilder seatArrangement(SeatArrangement seatArrangement) {
            this.seatArrangement = seatArrangement;
            return this;
        }

        public DeskBuilder color(String color) {
            this.color = color;
            return this;
        }

        public DeskBuilder positionX(Double positionX) {
            this.positionX = positionX;
            return this;
        }

        public DeskBuilder positionY(Double positionY) {
            this.positionY = positionY;
            return this;
        }

        public DeskBuilder width(Double width) {
            this.width = width;
            return this;
        }

        public DeskBuilder height(Double height) {
            this.height = height;
            return this;
        }

        public DeskBuilder rotation(Double rotation) {
            this.rotation = rotation;
            return this;
        }

        public DeskBuilder seats(List<Seat> seats) {
            this.seats = seats;
            return this;
        }

        public Desk build() {
            return new Desk(id, room, shape, seatArrangement, color, 
                           positionX, positionY, width, height, rotation, seats);
        }
    }
}