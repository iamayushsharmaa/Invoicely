package com.example.saas.subscriptions.services;

import com.example.saas.subscriptions.dto.IapVerificationRequest;
import com.example.saas.subscriptions.models.UserSubscription;
import com.example.saas.subscriptions.repository.UserSubscriptionRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;

    public void activateSubscription(UUID userId, IapVerificationRequest request) {

        UserSubscription subscription = subscriptionRepository.findByUserId(userId)
                .orElse(UserSubscription.builder()
                        .userId(userId)
                        .build());

        subscription.setProductId(request.getProductId());
        subscription.setPurchaseToken(request.getPurchaseToken());
        subscription.setPurchaseDate(request.getPurchaseDate());
        subscription.setExpiryDate(request.getExpiryDate());
        subscription.setActive(request.getExpiryDate().isAfter(LocalDateTime.now()));

        subscriptionRepository.save(subscription);
    }

    public boolean isActive(UUID userId) {
        return subscriptionRepository.findByUserId(userId)
                .filter(sub -> sub.isActive() && sub.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}
