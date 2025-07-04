package com.example.saas.subscriptions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class IapVerificationRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Purchase token is required")
    private String purchaseToken;

    @NotNull(message = "Purchase date is required")
    private LocalDateTime purchaseDate;

    @NotNull(message = "Expiry date is required")
    private LocalDateTime expiryDate;
}
