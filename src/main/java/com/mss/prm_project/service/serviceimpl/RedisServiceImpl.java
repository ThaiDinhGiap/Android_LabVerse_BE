package com.mss.prm_project.service.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mss.prm_project.model.NotificationPayload;
import com.mss.prm_project.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final String REVOKED_PREFIX = "auth:revoked:";
    private static final String NOTIF_QUEUE_PREFIX = "notif:queue:";
    private static final String NOTIF_TIME_PREFIX = "notif:time:";
    private static final String NOTIF_USER_PREFIX = "notif:user:";

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

    @Override
    public void queueMissedNotification(long userId, NotificationPayload payload) {
        String key = NOTIF_QUEUE_PREFIX + userId;
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            redis.opsForList().rightPush(key, jsonPayload);
        } catch (JsonProcessingException e) {
            log.error("Không thể serialize notification payload cho user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public List<NotificationPayload> getAndClearMissedNotifications(long userId) {
        String key = NOTIF_QUEUE_PREFIX + userId;
        List<String> jsonPayloads = redis.opsForList().range(key, 0, -1);

        if (jsonPayloads == null || jsonPayloads.isEmpty()) {
            return Collections.emptyList();
        }

        redis.delete(key);

        return jsonPayloads.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, NotificationPayload.class);
                    } catch (JsonProcessingException e) {
                        log.error("Không thể deserialize notification payload cho user {}: {}", userId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void addUserToTimeSet(String time, long userId) {
        String key = NOTIF_TIME_PREFIX + time; // Ví dụ: "notif:time:09:30"
        redis.opsForSet().add(key, String.valueOf(userId));
    }

    @Override
    public void removeUserFromTimeSet(String time, long userId) {
        String key = NOTIF_TIME_PREFIX + time;
        redis.opsForSet().remove(key, String.valueOf(userId));
    }

    @Override
    public Set<String> getUsersAtScheduledTime(String time) {
        String key = NOTIF_TIME_PREFIX + time;
        return redis.opsForSet().members(key);
    }

    @Override
    public void saveUserScheduledTime(long userId, String time) {
        String key = NOTIF_USER_PREFIX + userId;
        redis.opsForValue().set(key, time);
    }

    @Override
    public String getUserScheduledTime(long userId) {
        String key = NOTIF_USER_PREFIX + userId;
        return redis.opsForValue().get(key);
    }

    @Override
    public void deleteUserScheduledTime(long userId) {
        String key = NOTIF_USER_PREFIX + userId;
        redis.delete(key);
    }
}

