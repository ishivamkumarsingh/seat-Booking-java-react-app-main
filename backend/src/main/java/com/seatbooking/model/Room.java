package com.seatbooking.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer roomNumber;

    private Integer width;

    private Integer height;

    @Column(columnDefinition = "TEXT")
    private String walls;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Desk> desks = new ArrayList<>();

    public Room() {}

    public Room(Long id, Floor floor, String name, Integer roomNumber, Integer width,
                Integer height, String walls, List<Desk> desks) {
        this.id = id;
        this.floor = floor;
        this.name = name;
        this.roomNumber = roomNumber;
        this.width = width;
        this.height = height;
        this.walls = walls;
        this.desks = desks != null ? desks : new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Floor getFloor() { return floor; }
    public void setFloor(Floor floor) { this.floor = floor; }

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

    public List<Desk> getDesks() { return desks; }
    public void setDesks(List<Desk> desks) { this.desks = desks; }

    public static RoomBuilder builder() {
        return new RoomBuilder();
    }

    public static class RoomBuilder {
        private Long id;
        private Floor floor;
        private String name;
        private Integer roomNumber;
        private Integer width;
        private Integer height;
        private String walls;
        private List<Desk> desks = new ArrayList<>();

        public RoomBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoomBuilder floor(Floor floor) {
            this.floor = floor;
            return this;
        }

        public RoomBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RoomBuilder roomNumber(Integer roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }

        public RoomBuilder width(Integer width) {
            this.width = width;
            return this;
        }

        public RoomBuilder height(Integer height) {
            this.height = height;
            return this;
        }

        public RoomBuilder walls(String walls) {
            this.walls = walls;
            return this;
        }

        public RoomBuilder desks(List<Desk> desks) {
            this.desks = desks;
            return this;
        }

        public Room build() {
            return new Room(id, floor, name, roomNumber, width, height, walls, desks);
        }
    }
}