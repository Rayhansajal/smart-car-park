package com.example.smart_car_park.controller;

import com.example.smart_car_park.models.dto.request.BookingRequestDTO;
import com.example.smart_car_park.models.dto.response.ApiResponseDTO;
import com.example.smart_car_park.models.dto.response.BookingResponseDTO;
import com.example.smart_car_park.models.entity.User;
import com.example.smart_car_park.service.BookingService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bookings", description = "Parking booking lifecycle management")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new parking booking")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> createBooking(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody BookingRequestDTO request) {
        BookingResponseDTO response = bookingService.createBooking(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(response, "Booking created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Get all bookings - Admin/Operator only")
    public ResponseEntity<ApiResponseDTO<Page<BookingResponseDTO>>> getAllBookings(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(bookingService.getAllBookings(pageable)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get bookings of the current logged-in user")
    public ResponseEntity<ApiResponseDTO<Page<BookingResponseDTO>>> getMyBookings(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                bookingService.getBookingsByUser(currentUser.getId(), pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(bookingService.getBookingById(id)));
    }

    @GetMapping("/ref/{ref}")
    @Operation(summary = "Get booking by booking reference number")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> getBookingByRef(@PathVariable String ref) {
        return ResponseEntity.ok(ApiResponseDTO.success(bookingService.getBookingByRef(ref)));
    }

    @PatchMapping("/{id}/checkin")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Record vehicle check-in for a booking")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(bookingService.checkIn(id), "Check-in recorded"));
    }

    @PatchMapping("/{id}/checkout")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Record vehicle check-out and calculate fee")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(bookingService.checkOut(id), "Check-out recorded"));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> cancelBooking(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                bookingService.cancelBooking(currentUser.getId(), id), "Booking cancelled"));
    }
}
