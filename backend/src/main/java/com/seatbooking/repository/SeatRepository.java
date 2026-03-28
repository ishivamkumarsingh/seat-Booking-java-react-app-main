package com.seatbooking.repository;

import com.seatbooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByDeskId(Long deskId);

    List<Seat> findByDeskIdIn(List<Long> deskIds);
}