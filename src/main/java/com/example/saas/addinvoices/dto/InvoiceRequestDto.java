package com.example.saas.addinvoices.dto;

import com.example.saas.client.dto.ClientRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.processing.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
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
    private String logoUrl;
    private List<InvoiceItemDto> items;

    private BigDecimal subTotal;
    private BigDecimal totalAmount;
}

