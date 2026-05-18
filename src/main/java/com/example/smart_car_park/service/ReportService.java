package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.response.DashboardResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ReportService {
    DashboardResponseDTO getDashboard();
    BigDecimal getRevenueBetween(LocalDateTime from, LocalDateTime to);
    long getBookingCountBetween(LocalDateTime from, LocalDateTime to);
}
