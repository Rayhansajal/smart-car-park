package com.example.smart_car_park.service.impl;

import com.example.smart_car_park.exception.BadRequestException;
import com.example.smart_car_park.exception.ResourceNotFoundException;
import com.example.smart_car_park.models.dto.request.AuthRequestDTO;
import com.example.smart_car_park.models.dto.response.UserResponseDTO;
import com.example.smart_car_park.models.entity.User;
import com.example.smart_car_park.repository.UserRepository;
import com.example.smart_car_park.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> modelMapper.map(user, UserResponseDTO.class));
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = findUserById(id);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, AuthRequestDTO.Register request) {
        User user = findUserById(id);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        User user = findUserById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public Page<UserResponseDTO> searchUsers(String query, Pageable pageable) {
        return userRepository.searchUsers(query, pageable)
                .map(user -> modelMapper.map(user, UserResponseDTO.class));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
