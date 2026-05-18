package com.example.smart_car_park.repository;

import com.example.smart_car_park.models.entity.ParkingLot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    List<ParkingLot> findByEnabledTrue();

    Page<ParkingLot> findByEnabledTrue(Pageable pageable);

    @Query("SELECT pl FROM ParkingLot pl WHERE " +
            "LOWER(pl.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(pl.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ParkingLot> searchLots(String query, Pageable pageable);

    long countByEnabledTrue();
}
