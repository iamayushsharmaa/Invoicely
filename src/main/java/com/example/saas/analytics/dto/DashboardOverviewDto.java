package com.example.saas.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DashboardOverviewDto {
    private long totalInvoices;
    private long paidInvoices;
    private long unpaidInvoices;
    private BigDecimal revenueThisMonth;
    private LocalDate lastInvoiceDate;
    private LocalDate nextDueDate;
}