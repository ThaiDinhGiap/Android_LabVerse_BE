package com.mss.prm_project.model;

import java.util.Map;

public record NotificationPayload(
        String title,
        String body,
        Map<String, String> data
) {}
