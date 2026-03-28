package com.seatbooking.repository;

import com.seatbooking.model.Booking;
import com.seatbooking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findBySeatIdAndBookingDateAndStatus(Long seatId, LocalDate date, BookingStatus status);

    void deleteBySeatIdIn(Collection<Long> seatIds);

    void deleteBySeatId(Long seatId);

    @Query("SELECT b FROM Booking b WHERE b.seat.id = :seatId AND b.bookingDate = :date " +
           "AND b.status = 'CONFIRMED' AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflictingBookings(@Param("seatId") Long seatId, 
                                          @Param("date") LocalDate date, 
                                          @Param("startTime") LocalTime startTime, 
                                          @Param("endTime") LocalTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.seat.id IN :seatIds AND b.bookingDate = :date " +
           "AND b.status = 'CONFIRMED' AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflictingBookingsForSeats(@Param("seatIds") List<Long> seatIds, 
                                                  @Param("date") LocalDate date, 
                                                  @Param("startTime") LocalTime startTime, 
                                                  @Param("endTime") LocalTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.seat.desk.room.id = :roomId AND b.bookingDate = :date")
    List<Booking> findByRoomAndDate(@Param("roomId") Long roomId, @Param("date") LocalDate date);

    List<Booking> findByUserId(Long userId);
}