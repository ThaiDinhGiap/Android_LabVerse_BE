package com.mss.prm_project.service;

import com.mss.prm_project.dto.UserDTO;

import java.util.Date;

public interface JwtService {
    String generateAcessToken(UserDTO dto);

    String generateRefreshToken(UserDTO dto);

    String extractUsername(String token);

    String extractEmail(String token);

    Date extractExpiration(String token);

    long extractUserId(String token);
}
