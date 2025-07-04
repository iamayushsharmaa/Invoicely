package com.example.saas.user.controller;

import com.example.saas.user.dto.*;
import com.example.saas.user.entity.RefreshToken;
import com.example.saas.user.entity.User;
import com.example.saas.user.repository.PasswordResetTokenRepository;
import com.example.saas.user.service.*;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;
    private final GoogleAuthService googleAuthService;
    private final ForgetPasswordService forgetPasswordService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthenticationResponse response = service.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        try {
            AuthenticationResponse response = service.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> googleSignIn(@RequestBody GoogleSignInRequest request) {
        try {
            AuthenticationResponse jwt = googleAuthService.verifyTokenAndLogin(request.getToken());
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    AuthenticationResponse.builder()
                            .accessToken(null)
                            .refreshToken(null)
                            .build()
            );
        }
    }

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassoword(@RequestBody ForgetPasswordRequest request) {
        try {
            forgetPasswordService.initiateReset(request.getEmail());
            return ResponseEntity.ok("Password reset link sent to email.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(ResetPasswordRequest request) {
        try {
            forgetPasswordService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password successfully reset.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
            refreshTokenService.verifyExpiration(refreshToken);
            User user = refreshToken.getUser();
            String newAccessToken = jwtService.generateAccessToken(user);
            return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, refreshToken.getToken()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
