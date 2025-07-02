package com.example.saas.addinvoices.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InvoiceResponseDto {
    private UUID id;
    private UUID clientId;

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;

    private String billingFrom;
    private String billingTo;
    private String notes;
    private String status;

    private BigDecimal subTotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal totalAmount;

    private String logoUrl;
    private String currency;
    private Boolean paid;
    private LocalDateTime paymentDate;

    private List<InvoiceItemResponseDto> items;
}
