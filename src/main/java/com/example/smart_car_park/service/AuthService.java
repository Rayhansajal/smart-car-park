package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.request.AuthRequestDTO;
import com.example.smart_car_park.models.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO.TokenResponseDTO register(AuthRequestDTO.Register request);
    AuthResponseDTO.TokenResponseDTO login(AuthRequestDTO.Login request);
    AuthResponseDTO.TokenResponseDTO refreshToken(String refreshToken);
    void logout(String refreshToken);
    void changePassword(Long userId, AuthRequestDTO.ChangePassword request);
}
