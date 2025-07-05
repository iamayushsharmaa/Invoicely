package com.example.saas.invoices.dto;

import com.example.saas.client.dto.ClientRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InvoiceRequestDto {
    @NotNull(message = "Client ID is required")
    private UUID clientId;

    @Valid
    private ClientRequestDto client;

    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;

    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotBlank(message = "Billing from is required")
    private String billingFrom;

    @NotBlank(message = "Billing to is required")
    private String billingTo;

    private String notes;

    @NotBlank(message = "Invoice status is required")
    private String status;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be non-negative")
    private BigDecimal discount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax must be non-negative")
    private BigDecimal tax;

    @NotBlank(message = "Currency is required")
    private String currency;

    private String logoUrl;

    @NotEmpty(message = "Invoice must contain at least one item")
    @Valid
    private List<InvoiceItemDto> items;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Subtotal must be non-negative")
    private BigDecimal subTotal;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be non-negative")
    private BigDecimal totalAmount;
}

