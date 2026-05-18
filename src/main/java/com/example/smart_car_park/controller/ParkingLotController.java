package com.example.smart_car_park.controller;

import com.example.smart_car_park.models.dto.request.ParkingLotRequestDTO;
import com.example.smart_car_park.models.dto.response.ApiResponseDTO;
import com.example.smart_car_park.models.dto.response.ParkingLotResponseDTO;
import com.example.smart_car_park.service.ParkingLotService;
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
@RequestMapping("/api/lots")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Parking Lots", description = "Parking lot management")
public class ParkingLotController {
    private final ParkingLotService parkingLotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new parking lot")
    public ResponseEntity<ApiResponseDTO<ParkingLotResponseDTO>> createLot(
            @Valid @RequestBody ParkingLotRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(parkingLotService.createLot(request), "Parking lot created"));
    }

    @GetMapping
    @Operation(summary = "Get all parking lots (paginated)")
    public ResponseEntity<ApiResponseDTO<Page<ParkingLotResponseDTO>>> getAllLots(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String search) {
        Page<ParkingLotResponseDTO> lots = (search != null && !search.isBlank())
                ? parkingLotService.searchLots(search, pageable)
                : parkingLotService.getAllLots(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(lots));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active parking lots")
    public ResponseEntity<ApiResponseDTO<List<ParkingLotResponseDTO>>> getActiveLots() {
        return ResponseEntity.ok(ApiResponseDTO.success(parkingLotService.getActiveLots()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parking lot by ID")
    public ResponseEntity<ApiResponseDTO<ParkingLotResponseDTO>> getLotById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(parkingLotService.getLotById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update a parking lot")
    public ResponseEntity<ApiResponseDTO<ParkingLotResponseDTO>> updateLot(
            @PathVariable Long id,
            @Valid @RequestBody ParkingLotRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                parkingLotService.updateLot(id, request), "Parking lot updated"));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable or disable a parking lot")
    public ResponseEntity<ApiResponseDTO<Void>> toggleStatus(@PathVariable Long id) {
        parkingLotService.toggleLotStatus(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Parking lot status toggled"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a parking lot")
    public ResponseEntity<ApiResponseDTO<Void>> deleteLot(@PathVariable Long id) {
        parkingLotService.deleteLot(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Parking lot deleted"));
    }
}
