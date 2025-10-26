package com.mss.prm_project.service;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface FcmService {
    public String sendNotificationToToken(String token, String title, String body) throws FirebaseMessagingException ;
}
