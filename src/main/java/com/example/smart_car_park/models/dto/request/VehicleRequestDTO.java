package com.example.smart_car_park.models.dto.request;

import com.example.smart_car_park.models.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequestDTO {

    @NotBlank(message = "Plate number is required")
    private String plateNo;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    private String brand;
    private String model;
    private String color;
}
