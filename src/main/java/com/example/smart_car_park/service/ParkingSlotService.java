package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.request.ParkingSlotRequestDTO;
import com.example.smart_car_park.models.dto.response.ParkingSlotResponseDTO;
import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.models.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingSlotService {
    ParkingSlotResponseDTO createSlot(ParkingSlotRequestDTO request);
    ParkingSlotResponseDTO updateSlot(Long id, ParkingSlotRequestDTO request);
    void deleteSlot(Long id);
    ParkingSlotResponseDTO getSlotById(Long id);
    Page<ParkingSlotResponseDTO> getSlotsByLot(Long lotId, Pageable pageable);
    List<ParkingSlotResponseDTO> getAvailableSlots(Long lotId, VehicleType type);
    void updateSlotStatus(Long id, SlotStatus status);
    void bulkCreateSlots(Long lotId, int count, VehicleType type, int floor, String zone);
}
