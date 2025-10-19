package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.ApiResponse;
import com.mss.prm_project.model.AuthenticationRequest;
import com.mss.prm_project.model.AuthenticationResponse;
import com.mss.prm_project.model.RegisterRequest;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.AuthenticationService;
import com.mss.prm_project.service.JwtService;
import com.mss.prm_project.service.RedisService;
import com.mss.prm_project.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long expirationOfRefreshToken;


    @Override
    public ApiResponse<AuthenticationResponse> login(AuthenticationRequest request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();

        var dtoOpt = userService.getUserByEmail(email);
        if (dtoOpt.isEmpty()) {
            return ApiResponse.<AuthenticationResponse>builder()
                    .code(401).message("Invalid username or password").build();
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );


        var dto = dtoOpt.get();
        String accessToken  = jwtService.generateAcessToken(dto);
        String refreshToken = jwtService.generateRefreshToken(dto);
        redisService.saveRefreshToken(dto.getUserId(), refreshToken, expirationOfRefreshToken);

        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login successfully!")
                .result(AuthenticationResponse.builder()
                        .userDTO(dto)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }


    @Override
    public ApiResponse<String> logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return ApiResponse.<String>builder()
                    .code(400)
                    .message("Missing token")
                    .build();
        }

        String raw = accessToken.trim();
        if (raw.regionMatches(true, 0, "Bearer ", 0, 7)) {
            raw = raw.substring(7);
        }
        try {
            long expMs = jwtService.extractExpiration(raw).getTime();
            long nowMs = System.currentTimeMillis();
            long ttlSec = Math.max(0, (expMs - nowMs) / 1000);
            if (ttlSec > 0) {
                redisService.revokeToken(raw, ttlSec);
            }

            long userId = jwtService.extractUserId(raw);
            redisService.deleteRefreshToken(userId);

            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Logged out successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(401)
                    .message("Invalid token")
                    .build();
        }
    }

    @Override
    public ApiResponse<AuthenticationResponse> refresh(String refreshToken) throws Exception {
        if (refreshToken.startsWith("Bearer")) {
            refreshToken = refreshToken.substring(7);
        }

        long user_id = jwtService.extractUserId(refreshToken);
        UserDTO dto = userService.getUserById(user_id);

        if (redisService.getRefreshToken(user_id).equals(refreshToken)) {
            String newAccessToken = jwtService.generateAcessToken(dto);
            String newRefreshToken = jwtService.generateRefreshToken(dto);

            redisService.deleteRefreshToken(user_id);
            redisService.saveRefreshToken(user_id, newRefreshToken, expirationOfRefreshToken);

            AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                    .userDTO(dto)
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
            return ApiResponse.<AuthenticationResponse>builder()
                    .message("Refresh token successfully!")
                    .result(authenticationResponse)
                    .build();
        }
        return null;
    }

    @Override
    public ApiResponse<AuthenticationResponse> register(RegisterRequest request) {
        User newUser = new User();
        newUser.setFullName(request.getFullname());
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setEnabled(true);

        UserDTO createdUser = userService.createUser(newUser);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .userDTO(createdUser)
                .build();
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Register successfully!")
                .result(authenticationResponse)
                .build();
    }
}
