package com.example.saas.user.service;

import com.example.saas.user.dto.AuthenticationResponse;
import com.example.saas.user.entity.User;
import com.example.saas.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public AuthenticationResponse verifyTokenAndLogin(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new RuntimeException("Invalid Google token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");


        User user = repository.findByEmail(name).orElseGet(
                () -> {
                    User newUser = User.builder()
                            .email(email)
                            .password("")
                            .build();
                    return repository.save(newUser);
                }
        );
        String accessToken = jwtService.generateAccessToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
