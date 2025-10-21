package com.mss.prm_project.dto;

import lombok.Data;

@Data
public class ProfileDTO {
    private String userName;
    private String name;
    private String affiliation;
    private boolean pushNotifications;
    private boolean emailNotifications;
}
