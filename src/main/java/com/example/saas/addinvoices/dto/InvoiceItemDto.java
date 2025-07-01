package com.example.saas.addinvoices.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceItemDto {
    private String description;
    private Integer quantity;
    private BigDecimal price;
}
