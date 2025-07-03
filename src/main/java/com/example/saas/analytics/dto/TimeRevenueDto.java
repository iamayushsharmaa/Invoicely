package com.example.saas.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TimeRevenueDto {
    private String label;
    private BigDecimal revenue;
}