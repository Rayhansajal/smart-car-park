package com.example.smart_car_park.controller;

import com.example.smart_car_park.models.dto.request.ParkingSlotRequestDTO;
import com.example.smart_car_park.models.dto.response.ApiResponseDTO;
import com.example.smart_car_park.models.dto.response.ParkingSlotResponseDTO;
import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.models.enums.VehicleType;
import com.example.smart_car_park.service.ParkingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Parking Slots", description = "Slot management and availability")
public class ParkingSlotController {
    private final ParkingSlotService parkingSlotService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Create a single parking slot")
    public ResponseEntity<ApiResponseDTO<ParkingSlotResponseDTO>> createSlot(
            @Valid @RequestBody ParkingSlotRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(parkingSlotService.createSlot(request), "Slot created"));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Bulk create slots in a parking lot")
    public ResponseEntity<ApiResponseDTO<Void>> bulkCreate(
            @RequestParam Long lotId,
            @RequestParam int count,
            @RequestParam VehicleType type,
            @RequestParam(defaultValue = "1") int floor,
            @RequestParam(required = false) String zone) {
        parkingSlotService.bulkCreateSlots(lotId, count, type, floor, zone);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(null, count + " slots created successfully"));
    }

    @GetMapping("/lot/{lotId}")
    @Operation(summary = "Get all slots in a parking lot")
    public ResponseEntity<ApiResponseDTO<Page<ParkingSlotResponseDTO>>> getSlotsByLot(
            @PathVariable Long lotId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                parkingSlotService.getSlotsByLot(lotId, pageable)));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available slots in a lot filtered by vehicle type")
    public ResponseEntity<ApiResponseDTO<List<ParkingSlotResponseDTO>>> getAvailableSlots(
            @RequestParam Long lotId,
            @RequestParam(required = false) VehicleType type) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                parkingSlotService.getAvailableSlots(lotId, type)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get slot by ID")
    public ResponseEntity<ApiResponseDTO<ParkingSlotResponseDTO>> getSlotById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(parkingSlotService.getSlotById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update a parking slot")
    public ResponseEntity<ApiResponseDTO<ParkingSlotResponseDTO>> updateSlot(
            @PathVariable Long id,
            @Valid @RequestBody ParkingSlotRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                parkingSlotService.updateSlot(id, request), "Slot updated"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update slot status (AVAILABLE, MAINTENANCE, etc.)")
    public ResponseEntity<ApiResponseDTO<Void>> updateStatus(
            @PathVariable Long id,
            @RequestParam SlotStatus status) {
        parkingSlotService.updateSlotStatus(id, status);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Slot status updated to " + status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a slot")
    public ResponseEntity<ApiResponseDTO<Void>> deleteSlot(@PathVariable Long id) {
        parkingSlotService.deleteSlot(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Slot deleted"));
    }
}
