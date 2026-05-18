package com.example.smart_car_park.controller;

import com.example.smart_car_park.models.dto.request.VehicleRequestDTO;
import com.example.smart_car_park.models.dto.response.ApiResponseDTO;
import com.example.smart_car_park.models.dto.response.VehicleResponseDTO;
import com.example.smart_car_park.models.entity.User;
import com.example.smart_car_park.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Vehicles", description = "Vehicle registration and management")
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "Register a new vehicle for current user")
    public ResponseEntity<ApiResponseDTO<VehicleResponseDTO>> createVehicle(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody VehicleRequestDTO request) {
        VehicleResponseDTO response = vehicleService.createVehicle(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(response, "Vehicle registered successfully"));
    }

    @GetMapping("/my")
    @Operation(summary = "Get all vehicles of the current user")
    public ResponseEntity<ApiResponseDTO<List<VehicleResponseDTO>>> getMyVehicles(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                vehicleService.getVehiclesByUser(currentUser.getId())));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Get vehicles by user ID - Admin/Operator only")
    public ResponseEntity<ApiResponseDTO<List<VehicleResponseDTO>>> getVehiclesByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponseDTO.success(vehicleService.getVehiclesByUser(userId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<ApiResponseDTO<VehicleResponseDTO>> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(vehicleService.getVehicleById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle details")
    public ResponseEntity<ApiResponseDTO<VehicleResponseDTO>> updateVehicle(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                vehicleService.updateVehicle(currentUser.getId(), id, request), "Vehicle updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle")
    public ResponseEntity<ApiResponseDTO<Void>> deleteVehicle(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        vehicleService.deleteVehicle(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Vehicle deleted"));
    }
}
