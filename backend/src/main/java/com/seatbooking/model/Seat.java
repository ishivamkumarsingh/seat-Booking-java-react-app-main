package com.seatbooking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private Integer seatIndex;

    @Column(nullable = false)
    private Double positionX;

    @Column(nullable = false)
    private Double positionY;

    public Seat() {}

    public Seat(Long id, Desk desk, String label, Integer seatIndex, Double positionX, Double positionY) {
        this.id = id;
        this.desk = desk;
        this.label = label;
        this.seatIndex = seatIndex;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Desk getDesk() { return desk; }
    public void setDesk(Desk desk) { this.desk = desk; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Integer getSeatIndex() { return seatIndex; }
    public void setSeatIndex(Integer seatIndex) { this.seatIndex = seatIndex; }

    public Double getPositionX() { return positionX; }
    public void setPositionX(Double positionX) { this.positionX = positionX; }

    public Double getPositionY() { return positionY; }
    public void setPositionY(Double positionY) { this.positionY = positionY; }

    public static SeatBuilder builder() {
        return new SeatBuilder();
    }

    public static class SeatBuilder {
        private Long id;
        private Desk desk;
        private String label;
        private Integer seatIndex;
        private Double positionX;
        private Double positionY;

        public SeatBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SeatBuilder desk(Desk desk) {
            this.desk = desk;
            return this;
        }

        public SeatBuilder label(String label) {
            this.label = label;
            return this;
        }

        public SeatBuilder seatIndex(Integer seatIndex) {
            this.seatIndex = seatIndex;
            return this;
        }

        public SeatBuilder positionX(Double positionX) {
            this.positionX = positionX;
            return this;
        }

        public SeatBuilder positionY(Double positionY) {
            this.positionY = positionY;
            return this;
        }

        public Seat build() {
            return new Seat(id, desk, label, seatIndex, positionX, positionY);
        }
    }
}