package com.example.saas.invoices.repository;

import com.example.saas.invoices.models.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findAllByUserId(UUID userId);

    List<Invoice> findByClientIdAndUserId(UUID clientId, UUID userId);

    Optional<Invoice> findByIdAndUserId(UUID invoiceId, UUID userId);

    void deleteAllByUserId(UUID userId);

    @Query("""
                SELECT i FROM Invoice i
                JOIN i.client c
                WHERE i.user.id = :userId
                AND (:invoiceNumber IS NULL OR i.invoiceNumber LIKE %:invoiceNumber%)
                AND (:clientName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :clientName, '%')))
            """)
    Page<Invoice> searchInvoices(
            @Param("userId") UUID userId,
            @Param("invoiceNumber") String invoiceNumber,
            @Param("clientName") String clientName,
            Pageable pageable
    );
}
