package com.seatbooking.model;

import com.seatbooking.model.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk;

    @Column(nullable = false)
    private String title;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Version
    private Long version;

    public Meeting() {}

    public Meeting(Long id, User organizer, Desk desk, String title, LocalDate meetingDate,
                   LocalTime startTime, LocalTime endTime, BookingStatus status, Long version) {
        this.id = id;
        this.organizer = organizer;
        this.desk = desk;
        this.title = title;
        this.meetingDate = meetingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.version = version;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOrganizer() { return organizer; }
    public void setOrganizer(User organizer) { this.organizer = organizer; }

    public Desk getDesk() { return desk; }
    public void setDesk(Desk desk) { this.desk = desk; }

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

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public static MeetingBuilder builder() {
        return new MeetingBuilder();
    }

    public static class MeetingBuilder {
        private Long id;
        private User organizer;
        private Desk desk;
        private String title;
        private LocalDate meetingDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private BookingStatus status;
        private Long version;

        public MeetingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MeetingBuilder organizer(User organizer) {
            this.organizer = organizer;
            return this;
        }

        public MeetingBuilder desk(Desk desk) {
            this.desk = desk;
            return this;
        }

        public MeetingBuilder title(String title) {
            this.title = title;
            return this;
        }

        public MeetingBuilder meetingDate(LocalDate meetingDate) {
            this.meetingDate = meetingDate;
            return this;
        }

        public MeetingBuilder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public MeetingBuilder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public MeetingBuilder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public MeetingBuilder version(Long version) {
            this.version = version;
            return this;
        }

        public Meeting build() {
            return new Meeting(id, organizer, desk, title, meetingDate, 
                              startTime, endTime, status, version);
        }
    }
}