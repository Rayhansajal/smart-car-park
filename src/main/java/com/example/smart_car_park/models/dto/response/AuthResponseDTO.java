package com.example.smart_car_park.models.dto.response;

import com.example.smart_car_park.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthResponseDTO {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponseDTO {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private Long expiresIn;
        private UserInfoDTO user;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDTO {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private Role role;
    }
}
