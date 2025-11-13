package com.mss.prm_project.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mss.prm_project.dto.FcmTokenRequest;
import com.mss.prm_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final UserService userService;

    @PostMapping("/update-fcm-token")
    public ResponseEntity<String> updateFcmToken(@RequestBody FcmTokenRequest tokenRequest) throws FirebaseMessagingException {
        userService.updateFcmToken(tokenRequest.getUserName(), tokenRequest.getToken());
        return ResponseEntity.ok("FCM token updated successfully for user: " + tokenRequest.getUserName());
    }

    @GetMapping("/unread-notifications/{username}")
    public ResponseEntity<Void> getUnreadNotifications(@PathVariable("username") String username) throws FirebaseMessagingException {
        userService.sendUnreadNotificationsToUser(username);
        return ResponseEntity.ok().build();
    }
}
