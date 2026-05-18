package com.example.smart_car_park.service.impl;

import com.example.smart_car_park.exception.BadRequestException;
import com.example.smart_car_park.exception.ResourceNotFoundException;
import com.example.smart_car_park.exception.UnauthorizedException;
import com.example.smart_car_park.models.dto.request.VehicleRequestDTO;
import com.example.smart_car_park.models.dto.response.VehicleResponseDTO;
import com.example.smart_car_park.models.entity.User;
import com.example.smart_car_park.models.entity.Vehicle;
import com.example.smart_car_park.repository.UserRepository;
import com.example.smart_car_park.repository.VehicleRepository;
import com.example.smart_car_park.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public VehicleResponseDTO createVehicle(Long userId, VehicleRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (vehicleRepository.existsByPlateNo(request.getPlateNo())) {
            throw new BadRequestException("Vehicle with plate number already exists: " + request.getPlateNo());
        }

        Vehicle vehicle = Vehicle.builder()
                .user(user)
                .plateNo(request.getPlateNo().toUpperCase())
                .type(request.getType())
                .brand(request.getBrand())
                .model(request.getModel())
                .color(request.getColor())
                .build();

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public VehicleResponseDTO updateVehicle(Long userId, Long vehicleId, VehicleRequestDTO request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        if (!vehicle.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't own this vehicle");
        }

        if (vehicleRepository.existsByPlateNoAndIdNot(request.getPlateNo(), vehicleId)) {
            throw new BadRequestException("Plate number already in use: " + request.getPlateNo());
        }

        vehicle.setPlateNo(request.getPlateNo().toUpperCase());
        vehicle.setType(request.getType());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public void deleteVehicle(Long userId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        if (!vehicle.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't own this vehicle");
        }
        vehicleRepository.delete(vehicle);
    }

    @Override
    public List<VehicleResponseDTO> getVehiclesByUser(Long userId) {
        return vehicleRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public VehicleResponseDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return mapToResponse(vehicle);
    }

    private VehicleResponseDTO mapToResponse(Vehicle vehicle) {
        VehicleResponseDTO response = modelMapper.map(vehicle, VehicleResponseDTO.class);
        response.setUserId(vehicle.getUser().getId());
        response.setOwnerName(vehicle.getUser().getName());
        return response;
    }
}
