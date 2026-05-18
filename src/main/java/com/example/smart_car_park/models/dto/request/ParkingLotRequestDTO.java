package com.example.smart_car_park.models.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParkingLotRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Min(value = 1, message = "At least 1 floor required")
    private Integer totalFloors = 1;

    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;

    private BigDecimal dailyRate;

    private boolean enabled = true;
}
