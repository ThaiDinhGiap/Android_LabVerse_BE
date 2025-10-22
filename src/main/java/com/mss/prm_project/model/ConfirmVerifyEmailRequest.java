package com.mss.prm_project.model;

import lombok.Data;

@Data
public class ConfirmVerifyEmailRequest {
    String email;
    String token;
}
