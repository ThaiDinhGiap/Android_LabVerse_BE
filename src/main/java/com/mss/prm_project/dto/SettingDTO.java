package com.mss.prm_project.dto;

import lombok.Data;

@Data
public class SettingDTO {
    private String userName;
    private boolean instantNotification;
    private boolean scheduledNotification;
}
