package com.example.saas.invoices.mappers;

import com.example.saas.invoices.dto.InvoiceItemResponseDto;
import com.example.saas.invoices.dto.InvoiceRequestDto;
import com.example.saas.invoices.dto.InvoiceResponseDto;
import com.example.saas.invoices.models.Invoice;
import com.example.saas.invoices.models.InvoiceItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class InvoiceMapper {

    public Invoice toEntity(InvoiceRequestDto dto, UUID userId, UUID clientId) {
        List<InvoiceItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    BigDecimal total = itemDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
                    return InvoiceItem.builder()
                            .description(itemDto.getDescription())
                            .quantity(itemDto.getQuantity())
                            .price(itemDto.getPrice())
                            .total(total)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal subTotal = items.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
        BigDecimal tax = dto.getTax() != null ? dto.getTax() : BigDecimal.ZERO;
        BigDecimal totalAmount = subTotal.subtract(discount).add(tax);

        Invoice invoice = Invoice.builder()
                .userId(userId)
                .clientId(clientId)
                .invoiceNumber(dto.getInvoiceNumber())
                .invoiceDate(dto.getInvoiceDate())
                .dueDate(dto.getDueDate())
                .billingFrom(dto.getBillingFrom())
                .billingTo(dto.getBillingTo())
                .notes(dto.getNotes())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "INR")
                .discount(discount)
                .tax(tax)
                .subTotal(subTotal)
                .totalAmount(totalAmount)
                .status(dto.getStatus())
                .paid(false)
                .items(items)
                .logoUrl(dto.getLogoUrl())
                .build();

        items.forEach(item -> item.setInvoice(invoice));
        invoice.setItems(items);

        return invoice;
    }

    public InvoiceResponseDto toResponseDto(Invoice invoice) {
        List<InvoiceItemResponseDto> items = invoice.getItems().stream()
                .map(item -> InvoiceItemResponseDto.builder()
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .total(item.getTotal())
                        .build())
                .collect(Collectors.toList());

        return InvoiceResponseDto.builder()
                .id(invoice.getId())
                .clientId(invoice.getClientId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .billingFrom(invoice.getBillingFrom())
                .billingTo(invoice.getBillingTo())
                .notes(invoice.getNotes())
                .status(invoice.getStatus())
                .currency(invoice.getCurrency())
                .subTotal(invoice.getSubTotal())
                .discount(invoice.getDiscount())
                .tax(invoice.getTax())
                .totalAmount(invoice.getTotalAmount())
                .paid(invoice.getPaid())
                .logoUrl(invoice.getLogoUrl())
                .paymentDate(invoice.getPaymentDate())
                .items(items)
                .build();
    }
}
