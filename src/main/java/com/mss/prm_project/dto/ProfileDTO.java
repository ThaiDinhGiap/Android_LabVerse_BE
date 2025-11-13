package com.mss.prm_project.dto;

import lombok.Data;

@Data
public class ProfileDTO {
    private String userName;
    private String email;
    private String name;
    private String affiliation;
    private boolean instantPushNotification;
    private boolean scheduledPushNotification;
    private String scheduledTime;
    private boolean isGoogleLinked;
}
