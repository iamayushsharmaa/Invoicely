package com.example.saas.invoices.repository;

import com.example.saas.invoices.models.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    List<Invoice> findAllByUser_Id(UUID userId);

    List<Invoice> findByClient_IdAndUser_Id(UUID clientId, UUID userId);

    Optional<Invoice> findByIdAndUser_Id(UUID invoiceId, UUID userId);

    void deleteAllByUser_Id(UUID userId);

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
