package com.example.saas.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String currentPassword;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}