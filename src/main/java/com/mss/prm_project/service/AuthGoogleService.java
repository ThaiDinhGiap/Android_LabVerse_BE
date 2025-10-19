package com.mss.prm_project.service;

import com.mss.prm_project.model.ApiResponse;
import com.mss.prm_project.model.AuthenticationRequest;
import com.mss.prm_project.model.AuthenticationResponse;
import com.mss.prm_project.model.GoogleLoginRequest;

public interface AuthGoogleService {
    ApiResponse<AuthenticationResponse> loginWithGoogle(GoogleLoginRequest googleLoginRequest) throws Exception;

    void linkGoogleAccount(Long currentUserId, GoogleLoginRequest request) throws Exception;

}