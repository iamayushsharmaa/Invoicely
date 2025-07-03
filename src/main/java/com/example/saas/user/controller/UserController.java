package com.example.saas.user.controller;

import com.example.saas.user.entity.User;
import com.example.saas.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal User user){
        userService.deleteAccount(user.getId());
        return ResponseEntity.ok("Your account has been successfully deleted.");
    }
}
