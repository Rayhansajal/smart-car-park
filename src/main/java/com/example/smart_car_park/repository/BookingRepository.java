package com.example.smart_car_park.repository;

import com.example.smart_car_park.models.entity.Booking;
import com.example.smart_car_park.models.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingRef(String bookingRef);

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    List<Booking> findBySlotIdAndStatusIn(Long slotId, List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' " +
            "AND b.scheduledCheckIn < :expiryTime")
    List<Booking> findExpiredPendingBookings(LocalDateTime expiryTime);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    Page<Booking> findByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :from AND b.createdAt <= :to")
    long countBookingsInPeriod(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.status = 'COMPLETED' AND p.paidAt >= :from AND p.paidAt <= :to")
    java.math.BigDecimal sumRevenueInPeriod(LocalDateTime from, LocalDateTime to);

    long countByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.slot.lot.id = :lotId")
    Page<Booking> findByLotId(Long lotId, Pageable pageable);
}
