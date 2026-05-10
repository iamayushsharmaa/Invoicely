package com.example.saas.invoices.models;

import com.example.saas.client.models.Client;
import com.example.saas.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private String billingFrom;

    private String billingTo;

    private String notes;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal discount;

    @Column(precision = 12, scale = 2)
    private BigDecimal tax;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    private String currency;

    @Column(nullable = false)
    private Boolean paid;

    private LocalDateTime paymentDate;

    private String paymentMethod;

    private String logoUrl;

    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<InvoiceItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();

        if (paid == null) {
            paid = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}