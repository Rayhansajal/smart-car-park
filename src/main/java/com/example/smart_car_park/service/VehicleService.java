package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.request.VehicleRequestDTO;
import com.example.smart_car_park.models.dto.response.VehicleResponseDTO;

import java.util.List;

public interface VehicleService {
    VehicleResponseDTO createVehicle(Long userId, VehicleRequestDTO request);
    VehicleResponseDTO updateVehicle(Long userId, Long vehicleId, VehicleRequestDTO request);
    void deleteVehicle(Long userId, Long vehicleId);
    List<VehicleResponseDTO> getVehiclesByUser(Long userId);
    VehicleResponseDTO getVehicleById(Long id);
}
