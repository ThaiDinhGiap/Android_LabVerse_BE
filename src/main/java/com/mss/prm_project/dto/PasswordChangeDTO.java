package com.mss.prm_project.dto;

import lombok.Data;

@Data
public class PasswordChangeDTO {
    private String userName;
    private String oldPassword;
    private String newPassword;
}
