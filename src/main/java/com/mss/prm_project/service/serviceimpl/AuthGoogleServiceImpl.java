package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.ApiResponse;
import com.mss.prm_project.model.AuthenticationRequest;
import com.mss.prm_project.model.AuthenticationResponse;
import com.mss.prm_project.model.GoogleLoginRequest;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthGoogleServiceImpl implements AuthGoogleService {

    private final GoogleTokenVerifierPortService verifier;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final RedisService redisService;

    @Value("${jwt.refresh-token-expiration}")
    private long expirationOfRefreshToken;

    @Override
    public ApiResponse<AuthenticationResponse> loginWithGoogle(GoogleLoginRequest request) throws Exception {
        var gpOpt = verifier.verify(request.idToken());
        if (gpOpt.isEmpty() || !gpOpt.get().emailVerified()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token");
        }
        var gp = gpOpt.get();

        User user = userRepository.findByGoogleSub(gp.sub())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Need to link Google account"));

        if (user.getEmailVerifyAt() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED");
        }

        var dto = userService.getUserByGoogleSub(gp.sub());
        String accessToken  = jwtService.generateAcessToken(dto);
        String refreshToken = jwtService.generateRefreshToken(dto);
        redisService.saveRefreshToken(dto.getUserId(), refreshToken, expirationOfRefreshToken);

        // Log backend-side

        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login successfully!")
                .result(AuthenticationResponse.builder()
                        .userDTO(dto)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }



    @Override
    @Transactional
    public void linkGoogleAccount(Long currentUserId, GoogleLoginRequest request) throws Exception {
        var gp = verifier.verify(request.idToken())
                .filter(GoogleTokenVerifierPortService.GoogleProfile::emailVerified)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token"));



        User me = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        if (me.getEmailVerifyAt() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED");
        }

        // email Google phải trùng email tài khoản
//        if (!me.getEmail().equalsIgnoreCase(gp.email())) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "EMAIL_MISMATCH");
//        }

        userRepository.findByGoogleSub(gp.sub()).ifPresent(existing -> {
            if (existing.getUserId() != me.getUserId()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "GOOGLE_SUB_TAKEN");
            }
        });

        if (me.getGoogleSub() == null) {
            me.setGoogleSub(gp.sub());
            me.setGoogleLinkAt(LocalDateTime.now());
            if (me.getAvatarUrl() == null || me.getAvatarUrl().isBlank()) me.setAvatarUrl(gp.picture());
            userRepository.save(me);
        }
    }

}
