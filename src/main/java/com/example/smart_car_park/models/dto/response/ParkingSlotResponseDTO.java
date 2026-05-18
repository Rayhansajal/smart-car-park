package com.example.smart_car_park.models.dto.response;

import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.models.enums.VehicleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingSlotResponseDTO {
    private Long id;
    private Long lotId;
    private String lotName;
    private String slotNo;
    private Integer floor;
    private String zone;
    private VehicleType slotType;
    private SlotStatus status;
    private LocalDateTime updatedAt;
}
