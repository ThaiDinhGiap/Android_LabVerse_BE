package com.mss.prm_project.config;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.NotificationPayload;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.FcmService;
import com.mss.prm_project.service.RecommendationService;
import com.mss.prm_project.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DigestScheduler {

    private final UserRepository userRepo;
    private final PaperRepository paperRepo;
    private final RecommendationService rec;
    private final FcmService fcm;
    private final RedisService redisService;

    @Value("${labverse.digest.cron}")
    private String cron;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Scheduled(cron = "${labverse.digest.cron}", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyDigest() throws FirebaseMessagingException {
        log.info("Starting digest scheduler");

        LocalTime now = LocalTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        String currentTime = now.format(TIME_FORMATTER);

        Set<String> userIds = redisService.getUsersAtScheduledTime(currentTime);

        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        log.info("Starting digest for {} users at {}", userIds.size(), currentTime);

        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<Paper> recent = paperRepo.findByPublishDateAfter(twentyFourHoursAgo);

        if (recent.isEmpty()) {
            log.info("Không có paper mới, bỏ qua gửi digest.");
            return;
        }

        List<User> users = userRepo.findAllById(userIds.stream().map(Long::parseLong).collect(Collectors.toList()))
                .stream()
                .filter(User::isScheduledPushNotification)
                .toList();

        for (User u : users) {
            if (recent.isEmpty()) continue;

            String title = "Daily Paper Digest";
            String body = recent.size() == 1
                    ? "1 paper for you: " + recent.get(0).getTitle()
                    : recent.size() + " new papers you may like. Tap to view.";

            Map<String, String> data = new HashMap<>();
            data.put("type", "scheduled");
            data.put("papersCsv", recent.stream().map(p -> String.valueOf(p.getTitle()))
                    .collect(Collectors.joining(",")));

            if (u.getFcmToken() == null || u.getFcmToken().isBlank()) {
                log.info("User {} không có FCM token, đang xếp hàng thông báo.", u.getUserId());
                NotificationPayload payload = new NotificationPayload(title, body, data);
                redisService.queueMissedNotification(u.getUserId(), payload);
            } else {
                fcm.sendNotificationToToken(u.getFcmToken(), title, body, data);
            }
        }
    }
}
