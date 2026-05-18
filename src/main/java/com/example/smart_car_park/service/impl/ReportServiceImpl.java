package com.example.smart_car_park.service.impl;

import com.example.smart_car_park.models.dto.response.DashboardResponseDTO;
import com.example.smart_car_park.models.enums.BookingStatus;
import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.repository.*;
import com.example.smart_car_park.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ParkingLotRepository lotRepository;
    private final ParkingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardResponseDTO getDashboard() {
        long totalSlots = slotRepository.count();
        long availableSlots = slotRepository.countByStatus(SlotStatus.AVAILABLE);
        long occupiedSlots = slotRepository.countByStatus(SlotStatus.OCCUPIED);
        long totalLots = lotRepository.countByEnabledTrue();
        long totalBookings = bookingRepository.count();
        long activeBookings = bookingRepository.countByStatus(BookingStatus.CHECKED_IN);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        long todayBookings = bookingRepository.countBookingsInPeriod(todayStart, todayEnd);

        BigDecimal todayRevenue = paymentRepository.getRevenueForPeriod(todayStart, todayEnd);
        if (todayRevenue == null) todayRevenue = BigDecimal.ZERO;

        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        double occupancyRate = totalSlots > 0
                ? (double) occupiedSlots / totalSlots * 100
                : 0.0;

        return DashboardResponseDTO.builder()
                .totalLots(totalLots)
                .totalSlots(totalSlots)
                .availableSlots(availableSlots)
                .occupiedSlots(occupiedSlots)
                .totalBookings(totalBookings)
                .activeBookings(activeBookings)
                .todayBookings(todayBookings)
                .todayRevenue(todayRevenue)
                .totalRevenue(totalRevenue)
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0)
                .build();
    }

    @Override
    public BigDecimal getRevenueBetween(LocalDateTime from, LocalDateTime to) {
        BigDecimal revenue = paymentRepository.getRevenueForPeriod(from, to);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public long getBookingCountBetween(LocalDateTime from, LocalDateTime to) {
        return bookingRepository.countBookingsInPeriod(from, to);
    }
}
