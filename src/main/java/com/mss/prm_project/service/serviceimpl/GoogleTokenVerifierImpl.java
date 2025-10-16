package com.mss.prm_project.service.serviceimpl;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mss.prm_project.service.GoogleTokenVerifierPortService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoogleTokenVerifierImpl implements GoogleTokenVerifierPortService {

    private static final List<String> DEFAULT_ISSUERS = List.of(
            "accounts.google.com",
            "https://accounts.google.com"
    );

    private final GoogleIdTokenVerifier verifier;
    private final List<String> issuers;

    public GoogleTokenVerifierImpl(Environment env) {
        String audiencesCsv = env.getProperty("google.allowed-audiences"); // comma-separated
        if (audiencesCsv == null || audiencesCsv.isBlank()) {
            throw new IllegalStateException("Missing required property 'google.allowed-audiences' (Web Client ID).");
        }
        List<String> audiences = List.of(audiencesCsv.split("\\s*,\\s*"));

        this.issuers = List.of(
                env.getProperty("google.issuers[0]", DEFAULT_ISSUERS.get(0)),
                env.getProperty("google.issuers[1]", DEFAULT_ISSUERS.get(1))
        );

        // Cho phép lệch 60s
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(audiences)
                .setIssuer(issuers.size() == 1 ? issuers.get(0) : null)
                .build();
    }

    @Override
    public Optional<GoogleProfile> verify(String idTokenStr) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenStr);
            if (idToken == null) {
                return Optional.empty();
            }

            var payload = idToken.getPayload();
            String iss = String.valueOf(payload.getIssuer());
            if (!issuers.contains(iss)) {
                return Optional.empty();
            }

            Boolean emailVerified = payload.getEmailVerified();
            String email = payload.getEmail();
            String sub = payload.getSubject();

            return Optional.of(new GoogleProfile(
                    sub,
                    email,
                    Boolean.TRUE.equals(emailVerified),
                    (String) payload.get("name"),
                    (String) payload.get("picture")
            ));
        } catch (Exception e) {

            return Optional.empty();
        }
    }
}


