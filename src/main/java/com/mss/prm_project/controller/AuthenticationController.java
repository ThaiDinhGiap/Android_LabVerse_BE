package com.mss.prm_project.controller;

import com.mss.prm_project.model.ApiResponse;
import com.mss.prm_project.model.AuthenticationRequest;
import com.mss.prm_project.model.AuthenticationResponse;
import com.mss.prm_project.model.RegisterRequest;
import com.mss.prm_project.service.AuthenticationService;
import com.mss.prm_project.service.JwtService;
import com.mss.prm_project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


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
}
