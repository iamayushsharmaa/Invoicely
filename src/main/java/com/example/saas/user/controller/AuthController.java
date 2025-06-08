package com.example.saas.user.controller;

import com.example.saas.user.dto.AuthenticationRequest;
import com.example.saas.user.dto.AuthenticationResponse;
import com.example.saas.user.dto.GoogleSignInRequest;
import com.example.saas.user.dto.RegisterRequest;
import com.example.saas.user.service.AuthenticationService;
import com.example.saas.user.service.GoogleAuthService;
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

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }


    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> googleSignIn(@RequestBody GoogleSignInRequest request) {
        try {
            String jwt = googleAuthService.verifyTokenAndLogin(request.getToken());
            return ResponseEntity.ok(new AuthenticationResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new AuthenticationResponse("Invalid Google token")
            );
        }
    }
}
