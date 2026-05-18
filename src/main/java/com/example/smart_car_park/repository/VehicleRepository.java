package com.example.smart_car_park.repository;

import com.example.smart_car_park.models.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUserId(Long userId);

    Optional<Vehicle> findByPlateNo(String plateNo);

    boolean existsByPlateNo(String plateNo);

    boolean existsByPlateNoAndIdNot(String plateNo, Long id);
}
