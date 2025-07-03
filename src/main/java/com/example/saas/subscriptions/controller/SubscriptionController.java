package com.example.saas.subscriptions.controller;

import com.example.saas.subscriptions.dto.IapVerificationRequest;
import com.example.saas.subscriptions.services.SubscriptionService;
import com.example.saas.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPurchase(
            @RequestBody IapVerificationRequest request,
            @AuthenticationPrincipal User user
    ) {
        subscriptionService.activateSubscription(user.getId(), request);
        return ResponseEntity.ok("Subscription verified and activated");
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> getSubscriptionStatus(@AuthenticationPrincipal User user) {
        boolean isPremium = subscriptionService.isActive(user.getId());
        return ResponseEntity.ok(isPremium);
    }
}
