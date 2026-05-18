package com.example.smart_car_park.service.impl;

import com.example.smart_car_park.exception.BadRequestException;
import com.example.smart_car_park.exception.ResourceNotFoundException;
import com.example.smart_car_park.models.dto.request.AuthRequestDTO;
import com.example.smart_car_park.models.dto.response.AuthResponseDTO;
import com.example.smart_car_park.models.entity.RefreshToken;
import com.example.smart_car_park.models.entity.User;
import com.example.smart_car_park.models.enums.Role;
import com.example.smart_car_park.repository.RefreshTokenRepository;
import com.example.smart_car_park.repository.UserRepository;
import com.example.smart_car_park.security.JwtUtils;
import com.example.smart_car_park.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    @Transactional
    public AuthResponseDTO.TokenResponseDTO register(AuthRequestDTO.Register request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole() != null ? request.getRole() : Role.DRIVER)
                .enabled(true)
                .build();
        userRepository.save(user);
        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = createRefreshToken(user);
        return buildTokenResponse(accessToken, refreshToken, user);
    }

    @Override
    @Transactional
    public AuthResponseDTO.TokenResponseDTO login(AuthRequestDTO.Login request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = createRefreshToken(user);
        return buildTokenResponse(accessToken, refreshToken, user);
    }

    @Override
    @Transactional
    public AuthResponseDTO.TokenResponseDTO refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new BadRequestException("Refresh token not found"));
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token has expired. Please login again.");
        }
        User user = refreshToken.getUser();
        String newAccessToken = jwtUtils.generateToken(user);
        String newRefreshToken = createRefreshToken(user);
        return buildTokenResponse(newAccessToken, newRefreshToken, user);
    }

    @Override
    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(refreshTokenRepository::delete);
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional
    public void changePassword(Long userId, AuthRequestDTO.ChangePassword request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenRepository.deleteByUser(user);
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();
        return refreshTokenRepository.save(refreshToken).getToken();
    }

    private AuthResponseDTO.TokenResponseDTO buildTokenResponse(String access, String refresh, User user) {
        return AuthResponseDTO.TokenResponseDTO.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .user(AuthResponseDTO.UserInfoDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .build())
                .build();
    }
}
