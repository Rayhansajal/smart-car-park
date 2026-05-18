package com.example.smart_car_park.models.dto.request;

import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.models.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkingSlotRequestDTO {
    @NotNull(message = "Lot ID is required")
    private Long lotId;

    @NotBlank(message = "Slot number is required")
    private String slotNo;

    private Integer floor = 1;

    private String zone;

    @NotNull(message = "Slot type is required")
    private VehicleType slotType;

    private SlotStatus status = SlotStatus.AVAILABLE;
}
