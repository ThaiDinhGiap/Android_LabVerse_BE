package com.mss.prm_project.service.serviceimpl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mss.prm_project.service.FcmService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmServiceImpl implements FcmService {
    @Override
    public String sendNotificationToToken(String token, String title, String body, Map<String, String> data) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(notification);

        if (data != null && !data.isEmpty()) {
            messageBuilder.putAllData(data);
        }

        return FirebaseMessaging.getInstance().send(messageBuilder.build());
    }
}
