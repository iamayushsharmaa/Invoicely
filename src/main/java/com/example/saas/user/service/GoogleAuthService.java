package com.example.saas.user.service;

import com.example.saas.user.entity.User;
import com.example.saas.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository repository;
    private final JwtService jwtService;

    public String verifyTokenAndLogin(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(List.of(""))
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

        return jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(), "", List.of(new SimpleGrantedAuthority(user.getName()))
                )
        );

    }
}
