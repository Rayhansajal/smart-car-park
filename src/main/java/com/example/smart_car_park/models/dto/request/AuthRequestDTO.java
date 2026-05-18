package com.example.smart_car_park.models.dto.request;

import com.example.smart_car_park.models.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthRequestDTO {
    @Data
    public static class Register {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 50, message = "Password must be 6-50 characters")
        private String password;

        @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$", message = "Invalid phone number")
        private String phone;

        private Role role = Role.DRIVER;
    }

    @Data
    public static class Login {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class RefreshTokenRequest {
        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    @Data
    public static class ChangePassword {
        @NotBlank
        private String currentPassword;

        @NotBlank
        @Size(min = 6, max = 50)
        private String newPassword;
    }

}
