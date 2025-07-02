package com.example.saas.addinvoices.dto;

import com.example.saas.client.dto.ClientRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class InvoiceRequestDto {
    private UUID clientId;
    private ClientRequestDto client;

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;

    private String billingFrom;
    private String billingTo;
    private String notes;
    private String status;

    private BigDecimal discount;
    private BigDecimal tax;
    private String currency;

    private List<InvoiceItemDto> items;

    private BigDecimal subTotal;
    private BigDecimal totalAmount;
}

