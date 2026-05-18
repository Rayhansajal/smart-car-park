package com.example.smart_car_park.models.dto.response;

import com.example.smart_car_park.models.enums.VehicleType;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class VehicleResponseDTO {
    private Long id;
    private Long userId;
    private String ownerName;
    private String plateNo;
    private VehicleType type;
    private String brand;
    private String model;
    private String color;
    private LocalDateTime createdAt;
}
