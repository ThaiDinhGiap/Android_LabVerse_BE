package com.mss.prm_project.model;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank
        String idToken
) {}
