package com.example.saas.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgetPasswordRequest {
    String email;
}
