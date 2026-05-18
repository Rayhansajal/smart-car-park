package com.example.smart_car_park.repository;

import com.example.smart_car_park.models.entity.Payment;
import com.example.smart_car_park.models.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.status = 'COMPLETED' AND p.paidAt >= :from AND p.paidAt <= :to")
    BigDecimal getRevenueForPeriod(LocalDateTime from, LocalDateTime to);
}

