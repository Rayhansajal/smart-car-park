package com.example.smart_car_park.repository;

import com.example.smart_car_park.models.entity.ParkingSlot;
import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.models.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByLotId(Long lotId);

    Page<ParkingSlot> findByLotId(Long lotId, Pageable pageable);

    List<ParkingSlot> findByLotIdAndStatus(Long lotId, SlotStatus status);

    List<ParkingSlot> findByLotIdAndSlotTypeAndStatus(Long lotId, VehicleType slotType, SlotStatus status);

    Optional<ParkingSlot> findByLotIdAndSlotNo(Long lotId, String slotNo);

    boolean existsByLotIdAndSlotNo(Long lotId, String slotNo);

    long countByLotIdAndStatus(Long lotId, SlotStatus status);

    long countByStatus(SlotStatus status);

    long countByLotId(Long lotId);

    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.lot.id = :lotId " +
            "AND ps.slotType = :slotType AND ps.status = 'AVAILABLE'")
    List<ParkingSlot> findAvailableSlots(Long lotId, VehicleType slotType);
}
