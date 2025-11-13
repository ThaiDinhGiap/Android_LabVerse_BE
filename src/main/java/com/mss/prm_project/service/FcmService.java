package com.mss.prm_project.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.Map;

public interface FcmService {
    String sendNotificationToToken(String token, String title, String body, Map<String, String> data) throws FirebaseMessagingException;
}
