package com.seatbooking.repository;

import com.seatbooking.model.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {

    Optional<Floor> findByFloorNumber(Integer floorNumber);
}