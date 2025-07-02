package com.example.saas.subscriptions.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IapVerificationRequest {
    private String productId;
    private String purchaseToken;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
}
