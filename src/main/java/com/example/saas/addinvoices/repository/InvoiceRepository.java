package com.example.saas.addinvoices.repository;

import com.example.saas.addinvoices.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findAllByUserId(UUID userId);

    List<Invoice> findByClientIdAndUserId(UUID clientId, UUID userId);
}
