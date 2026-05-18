package com.example.smart_car_park.models.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class DashboardResponseDTO {
    private long totalLots;
    private long totalSlots;
    private long availableSlots;
    private long occupiedSlots;
    private long totalBookings;
    private long activeBookings;
    private long todayBookings;
    private BigDecimal todayRevenue;
    private BigDecimal totalRevenue;
    private double occupancyRate;
}
