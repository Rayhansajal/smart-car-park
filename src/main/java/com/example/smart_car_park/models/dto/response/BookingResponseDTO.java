package com.example.smart_car_park.models.dto.response;

import com.example.smart_car_park.models.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private String bookingRef;
    private Long userId;
    private String userName;
    private Long vehicleId;
    private String vehiclePlate;
    private Long slotId;
    private String slotNo;
    private String lotName;
    private LocalDateTime scheduledCheckIn;
    private LocalDateTime scheduledCheckOut;
    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private String notes;
    private LocalDateTime createdAt;
}
