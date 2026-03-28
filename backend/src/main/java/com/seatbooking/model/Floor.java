package com.seatbooking.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "floors")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer floorNumber;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    public Floor() {}

    public Floor(Long id, Integer floorNumber, String name, List<Room> rooms) {
        this.id = id;
        this.floorNumber = floorNumber;
        this.name = name;
        this.rooms = rooms != null ? rooms : new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }

    public static FloorBuilder builder() {
        return new FloorBuilder();
    }

    public static class FloorBuilder {
        private Long id;
        private Integer floorNumber;
        private String name;
        private List<Room> rooms = new ArrayList<>();

        public FloorBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FloorBuilder floorNumber(Integer floorNumber) {
            this.floorNumber = floorNumber;
            return this;
        }

        public FloorBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FloorBuilder rooms(List<Room> rooms) {
            this.rooms = rooms;
            return this;
        }

        public Floor build() {
            return new Floor(id, floorNumber, name, rooms);
        }
    }
}