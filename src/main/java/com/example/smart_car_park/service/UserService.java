package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.request.AuthRequestDTO;
import com.example.smart_car_park.models.dto.response.UserResponseDTO;
import com.example.smart_car_park.models.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserProfile(String email);
    UserResponseDTO updateUser(Long id, AuthRequestDTO.Register request);
    void deleteUser(Long id);
    void toggleUserStatus(Long id);
    User getCurrentUser(String email);
    Page<UserResponseDTO> searchUsers(String query, Pageable pageable);
}
