package com.example.saas.invoices.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InvoiceItemResponseDto {
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total;
}