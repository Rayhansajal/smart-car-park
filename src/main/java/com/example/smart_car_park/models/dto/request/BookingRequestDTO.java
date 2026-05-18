package com.example.smart_car_park.models.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BookingRequestDTO {
    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @Future(message = "Check-in must be a future date")
    private LocalDateTime scheduledCheckIn;

    private LocalDateTime scheduledCheckOut;

    private String notes;
}
