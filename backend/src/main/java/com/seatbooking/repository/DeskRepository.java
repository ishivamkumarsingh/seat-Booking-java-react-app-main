package com.seatbooking.repository;

import com.seatbooking.model.Desk;
import com.seatbooking.model.enums.DeskShape;
import com.seatbooking.model.enums.SeatArrangement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeskRepository extends JpaRepository<Desk, Long> {

    List<Desk> findByRoomId(Long roomId);

    @Query("SELECT d FROM Desk d WHERE d.seatArrangement = :arrangement")
    List<Desk> findBySeatArrangement(@Param("arrangement") SeatArrangement arrangement);

    @Query("SELECT d FROM Desk d JOIN d.room r JOIN r.floor f WHERE d.seatArrangement = :arrangement ORDER BY f.floorNumber, r.roomNumber")
    List<Desk> findBySeatArrangementOrderByFloor(@Param("arrangement") SeatArrangement arrangement);

    @Query("SELECT d FROM Desk d JOIN d.room r JOIN r.floor f WHERE d.shape = :shape ORDER BY f.floorNumber, r.roomNumber")
    List<Desk> findByShapeOrderByFloor(@Param("shape") DeskShape shape);

    @Query("SELECT DISTINCT d FROM Desk d JOIN d.room r JOIN r.floor f WHERE d.shape = :shape OR d.seatArrangement = :arrangement ORDER BY f.floorNumber, r.roomNumber")
    List<Desk> findMeetingEligibleDesksOrderByFloor(@Param("shape") DeskShape shape,
                                                    @Param("arrangement") SeatArrangement arrangement);
}