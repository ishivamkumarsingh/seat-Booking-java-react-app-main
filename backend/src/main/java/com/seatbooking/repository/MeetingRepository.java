package com.seatbooking.repository;

import com.seatbooking.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    void deleteByDeskId(Long deskId);

    void deleteByDeskIdIn(Collection<Long> deskIds);

    @Query("SELECT m FROM Meeting m WHERE m.desk.id = :deskId AND m.meetingDate = :date " +
           "AND m.status = 'CONFIRMED' AND m.startTime < :endTime AND m.endTime > :startTime")
    List<Meeting> findConflictingMeetings(@Param("deskId") Long deskId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime);

    List<Meeting> findByOrganizerId(Long organizerId);
}