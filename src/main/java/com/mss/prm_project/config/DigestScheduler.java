package com.mss.prm_project.config;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.FcmService;
import com.mss.prm_project.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
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

    @Value("${labverse.digest.cron}")
    private String cron;

    @Scheduled(cron = "${labverse.digest.cron}", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyDigest() throws FirebaseMessagingException {
        log.info("Starting digest scheduler");
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<Paper> recent = paperRepo.findByPublishDateAfter(twentyFourHoursAgo);

        List<User> users = userRepo.findAll().stream()
                .filter(User::isScheduledPushNotification)
                .toList();

        for (User u : users) {
            if (u.getFcmToken() == null || u.getFcmToken().isBlank()) continue;
            if (recent.isEmpty()) continue;

            String title = "Daily Paper Digest";
            String body = recent.size() == 1
                    ? "1 paper for you: " + recent.get(0).getTitle()
                    : recent.size() + " new papers you may like. Tap to view.";

            Map<String, String> data = new HashMap<>();
            data.put("type", "scheduled");
            data.put("papersCsv", recent.stream().map(p -> String.valueOf(p.getTitle()))
                    .collect(Collectors.joining(",")));

            fcm.sendNotificationToToken(u.getFcmToken(), title, body, data);
        }
    }
}
