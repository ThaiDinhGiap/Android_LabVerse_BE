package com.mss.prm_project.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.Map;

public interface FcmService {
    public String sendNotificationToToken(String token, String title, String body) throws FirebaseMessagingException ;

    String sendNotificationToToken(String token, String title, String body, Map<String, String> data) throws FirebaseMessagingException;
}
