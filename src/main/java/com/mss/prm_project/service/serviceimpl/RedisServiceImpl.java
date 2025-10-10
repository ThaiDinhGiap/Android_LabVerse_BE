package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redis;

    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final String REVOKED_PREFIX = "auth:revoked:";

    @Override
    public void saveRefreshToken(long userId, String refreshToken, long ttlMs) {
        String key = REFRESH_PREFIX + userId;
        redis.opsForValue().set(key, refreshToken, Duration.ofMillis(ttlMs));
    }

    @Override
    public String getRefreshToken(long userId) {
        return redis.opsForValue().get(REFRESH_PREFIX + userId);
    }

    @Override
    public void deleteRefreshToken(long userId) {
        redis.delete(REFRESH_PREFIX + userId);
    }

    @Override
    public void revokeToken(String accessToken, long ttlSeconds) {
        // lưu cờ revoke với TTL = thời gian còn lại của access token (giây)
        String key = REVOKED_PREFIX + accessToken;
        redis.opsForValue().set(key, "1", Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean isRevoked(String accessToken) {
        return Boolean.TRUE.equals(redis.hasKey(REVOKED_PREFIX + accessToken));
    }
}

