package com.example.smart_car_park.models.dto.response;

import com.example.smart_car_park.models.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
