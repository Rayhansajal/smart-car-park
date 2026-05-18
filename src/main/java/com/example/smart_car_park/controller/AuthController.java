package com.example.smart_car_park.controller;

import com.example.smart_car_park.models.dto.request.AuthRequestDTO;
import com.example.smart_car_park.models.dto.response.ApiResponseDTO;
import com.example.smart_car_park.models.dto.response.AuthResponseDTO;
import com.example.smart_car_park.models.entity.User;
import com.example.smart_car_park.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, token refresh")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO.TokenResponseDTO>> register(
            @Valid @RequestBody AuthRequestDTO.Register request) {
        AuthResponseDTO.TokenResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT tokens")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO.TokenResponseDTO>> login(
            @Valid @RequestBody AuthRequestDTO.Login request) {
        return ResponseEntity.ok(
                ApiResponseDTO.success(authService.login(request), "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO.TokenResponseDTO>> refresh(
            @Valid @RequestBody AuthRequestDTO.RefreshTokenRequest request) {
        return ResponseEntity.ok(
                ApiResponseDTO.success(authService.refreshToken(request.getRefreshToken()), "Token refreshed"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate refresh token")
    public ResponseEntity<ApiResponseDTO<Void>> logout(
            @Valid @RequestBody AuthRequestDTO.RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Logged out successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password for logged-in user")
    public ResponseEntity<ApiResponseDTO<Void>> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody AuthRequestDTO.ChangePassword request) {
        authService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Password changed successfully"));
    }
}
