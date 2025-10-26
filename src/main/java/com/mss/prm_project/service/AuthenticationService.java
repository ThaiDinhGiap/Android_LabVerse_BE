package com.mss.prm_project.service;

import com.mss.prm_project.model.ApiResponse;
import com.mss.prm_project.model.AuthenticationRequest;
import com.mss.prm_project.model.AuthenticationResponse;
import com.mss.prm_project.model.RegisterRequest;

public interface AuthenticationService {

    ApiResponse<AuthenticationResponse> login(AuthenticationRequest authenticationRequest);

    ApiResponse<String> logout(String accessToken);

    ApiResponse<AuthenticationResponse> refresh(String refreshToken) throws Exception;

    ApiResponse<AuthenticationResponse> register(RegisterRequest request);
}
