package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.request.ParkingLotRequestDTO;
import com.example.smart_car_park.models.dto.response.ParkingLotResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingLotService {
    ParkingLotResponseDTO createLot(ParkingLotRequestDTO request);
    ParkingLotResponseDTO updateLot(Long id, ParkingLotRequestDTO request);
    void deleteLot(Long id);
    ParkingLotResponseDTO getLotById(Long id);
    Page<ParkingLotResponseDTO> getAllLots(Pageable pageable);
    List<ParkingLotResponseDTO> getActiveLots();
    Page<ParkingLotResponseDTO> searchLots(String query, Pageable pageable);
    void toggleLotStatus(Long id);
}
