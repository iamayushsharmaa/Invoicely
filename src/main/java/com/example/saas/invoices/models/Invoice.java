package com.example.saas.invoices.models;

import com.example.saas.client.models.Client;
import com.example.saas.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, insertable = false, updatable = false)
    private Client client;

    @Column(nullable = false, unique = true)
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

    private String currency;
    private Boolean paid;
    private LocalDateTime paymentDate;
    private String paymentMethod;

    private String logoUrl;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items;
}
