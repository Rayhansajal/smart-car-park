package com.example.smart_car_park.controller;

import com.example.smart_car_park.models.dto.response.ApiResponseDTO;
import com.example.smart_car_park.models.dto.response.DashboardResponseDTO;
import com.example.smart_car_park.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
@Tag(name = "Reports", description = "Dashboard and analytics")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get overall dashboard statistics")
    public ResponseEntity<ApiResponseDTO<DashboardResponseDTO>> getDashboard() {
        return ResponseEntity.ok(ApiResponseDTO.success(reportService.getDashboard()));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get total revenue between two dates")
    public ResponseEntity<ApiResponseDTO<BigDecimal>> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                reportService.getRevenueBetween(from, to)));
    }

    @GetMapping("/bookings/count")
    @Operation(summary = "Get total booking count between two dates")
    public ResponseEntity<ApiResponseDTO<Long>> getBookingCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                reportService.getBookingCountBetween(from, to)));
    }
}
