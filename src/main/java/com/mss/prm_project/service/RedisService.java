package com.mss.prm_project.service;

public interface RedisService {
    void saveRefreshToken(long userId, String refreshToken, long ttlMs);
    String getRefreshToken(long userId);
    void deleteRefreshToken(long userId);

    void revokeToken(String accessToken, long ttlSeconds);
    boolean isRevoked(String accessToken);
}

