package com.example.saas.analytics.repository;

import com.example.saas.invoices.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AnalyticsRepository extends JpaRepository<Invoice, UUID> {
    long countByUser_Id(UUID userId);

    long countByUser_IdAndStatus(UUID userId, String status);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.user.id = :userId")
    BigDecimal getTotalRevenue(@Param("userId") UUID userId);

    @Query("SELECT MAX(i.invoiceDate) FROM Invoice i WHERE i.user.id = :userId")
    LocalDate getLastInvoiceDate(@Param("userId") UUID userId);

    @Query("SELECT MIN(i.dueDate) FROM Invoice i WHERE i.user.id = :userId AND i.status = 'UNPAID'")
    LocalDate getNextDueDate(@Param("userId") UUID userId);

    List<Invoice> findByUser_IdAndInvoiceDateBetween(UUID userId, LocalDate start, LocalDate end);

}
