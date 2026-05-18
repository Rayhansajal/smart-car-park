package com.example.smart_car_park.models.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParkingLotResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer totalFloors;
    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private boolean enabled;
    private int totalSlots;
    private int availableSlots;
    private LocalDateTime createdAt;
}
