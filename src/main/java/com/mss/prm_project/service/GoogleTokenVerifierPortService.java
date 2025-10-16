package com.mss.prm_project.service;

import java.util.Optional;

public interface GoogleTokenVerifierPortService {
    record GoogleProfile(
            String sub, String email, boolean emailVerified, String name, String picture
    ) {}
    Optional<GoogleProfile> verify(String idToken);
}
