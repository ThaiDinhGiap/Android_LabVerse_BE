package com.mss.prm_project.controller;

import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.*;
import com.mss.prm_project.service.*;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthGoogleService googleService;
    private final JwtService jwtService;
    private final UserService userService;
    private final MailService mailService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest authenticationRequest) {
        ApiResponse<AuthenticationResponse> response = authenticationService.login(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String accessToken) {
        ApiResponse<String> response = authenticationService.logout(accessToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(@RequestHeader("Authorization") String refreshToken) throws Exception {
        ApiResponse<AuthenticationResponse> response = authenticationService.refresh(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<AuthenticationResponse> response = authenticationService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login-google")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> loginGoogle(@RequestBody @Valid GoogleLoginRequest request) throws Exception {
        ApiResponse<AuthenticationResponse> response = googleService.loginWithGoogle(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/link")
    public ResponseEntity<Void> link(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid GoogleLoginRequest req) throws Exception {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN");
        }

        String token = authHeader.substring(7);
        long userId = jwtService.extractUserId(token);

        googleService.linkGoogleAccount(userId, req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestBody VerifyEmailRequest request) throws Exception {
        boolean emailExists = userService.checkIfEmailExists(request.getEmail());
        if (!emailExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<String>builder().message("Email not found!").build());
        }

        String token = jwtService.generateEmailVerifyToken(request.getEmail());

        mailService.sentVerifyMail(request.getEmail(), token);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<String>builder().message("Email verify link has been sent to your email").build());
    }

    @PostMapping("/email-verified")
    public ResponseEntity<ApiResponse<String>> updateEmailVerified(@RequestBody ConfirmVerifyEmailRequest request) throws Exception {
        String email = jwtService.validateEmailVerifyToken(request.getToken());
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<String>builder().message("Invalid or expired token!").build());
        }
        userService.updateEmailVerified(email);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<String>builder().message("Email successfully verified").build());
    }



}
