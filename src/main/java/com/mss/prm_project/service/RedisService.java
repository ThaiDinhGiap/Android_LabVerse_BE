package com.mss.prm_project.service;

import com.mss.prm_project.model.NotificationPayload;

import java.util.List;
import java.util.Set;

public interface RedisService {
    void saveRefreshToken(long userId, String refreshToken, long ttlMs);
    String getRefreshToken(long userId);
    void deleteRefreshToken(long userId);

    void revokeToken(String accessToken, long ttlSeconds);
    boolean isRevoked(String accessToken);

    void queueMissedNotification(long userId, NotificationPayload payload);
    List<NotificationPayload> getAndClearMissedNotifications(long userId);

    void addUserToTimeSet(String time, long userId);
    void removeUserFromTimeSet(String time, long userId);
    Set<String> getUsersAtScheduledTime(String time);

    void saveUserScheduledTime(long userId, String time);
    String getUserScheduledTime(long userId);
    void deleteUserScheduledTime(long userId);
}

