package com.example.saas.invoices.service;

import com.example.saas.invoices.dto.InvoiceRequestDto;
import com.example.saas.invoices.dto.InvoiceResponseDto;
import com.example.saas.invoices.mappers.InvoiceMapper;
import com.example.saas.invoices.models.Invoice;
import com.example.saas.invoices.models.InvoiceItem;
import com.example.saas.invoices.repository.InvoiceRepository;
import com.example.saas.client.models.Client;
import com.example.saas.client.repository.ClientRepository;
import com.example.saas.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientReposiotry;
    private final InvoiceMapper invoiceMapper;

    public InvoiceResponseDto createInvoice(InvoiceRequestDto invoiceRequestDto, UUID userId) {
        UUID clientId = invoiceRequestDto.getClientId();
        Client client;

        if (clientId != null) {
            client = clientReposiotry.findByIdAndUserId(clientId, userId)
                    .orElseThrow(() -> new NotFoundException("Client not found"));
        } else if (invoiceRequestDto.getClient() != null) {
            client = clientReposiotry.save(Client.builder()
                    .name(invoiceRequestDto.getClient().getName())
                    .email(invoiceRequestDto.getClient().getEmail())
                    .phone(invoiceRequestDto.getClient().getPhone())
                    .address(invoiceRequestDto.getClient().getAddress())
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build());
        } else {
            throw new IllegalArgumentException("Either clientId or client details must be provided");
        }

        Invoice invoice = invoiceMapper.toEntity(invoiceRequestDto, userId, client.getId());
        invoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponseDto(invoice);
    }

    public Page<InvoiceResponseDto> searchInvoices(UUID userId, String invoiceNumber, String clientName, int page, int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Invoice> invoicePage = invoiceRepository.searchInvoices(userId, invoiceNumber, clientName, pageable);

        return invoicePage.map(invoiceMapper::toResponseDto);
    }

    public List<InvoiceResponseDto> getInvoicesByUser(UUID userId) {
        return invoiceRepository.findAllByUserId(userId).stream()
                .map(invoiceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponseDto> getInvoicesByClient(UUID clientId, UUID userId) {
        return invoiceRepository.findByClientIdAndUserId(clientId, userId).stream()
                .map(invoiceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public InvoiceResponseDto getInvoiceById(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new NotFoundException("Invoice not found or unauthorized"));
        return invoiceMapper.toResponseDto(invoice);
    }

    public void deleteInvoice(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new NotFoundException("Invoice not found or unauthorized"));
        invoiceRepository.delete(invoice);
    }

    public InvoiceResponseDto updateInvoice(UUID invoiceId, InvoiceRequestDto dto, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new NotFoundException("Invoice not found or unauthorized"));

        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setDueDate(dto.getDueDate());
        invoice.setBillingFrom(dto.getBillingFrom());
        invoice.setBillingTo(dto.getBillingTo());
        invoice.setNotes(dto.getNotes());
        invoice.setStatus(dto.getStatus());
        invoice.setCurrency(dto.getCurrency());
        invoice.setLogoUrl(dto.getLogoUrl());
        invoice.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        invoice.setTax(dto.getTax() != null ? dto.getTax() : BigDecimal.ZERO);

        invoice.getItems().clear();
        List<InvoiceItem> updatedItems = dto.getItems().stream()
                .map(itemDto -> InvoiceItem.builder()
                        .description(itemDto.getDescription())
                        .quantity(itemDto.getQuantity())
                        .price(itemDto.getPrice())
                        .total(itemDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())))
                        .invoice(invoice)
                        .build())
                .toList();
        invoice.getItems().addAll(updatedItems);

        BigDecimal subTotal = updatedItems.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setSubTotal(subTotal);

        BigDecimal totalAmount = subTotal
                .subtract(invoice.getDiscount())
                .add(invoice.getTax());
        invoice.setTotalAmount(totalAmount);

        invoice.setUpdatedAt(LocalDateTime.now());

        return invoiceMapper.toResponseDto(invoiceRepository.save(invoice));
    }

    public InvoiceResponseDto markAsPaid(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                .orElseThrow(() -> new NotFoundException("Invoice not found or unauthorized"));

        if (invoice.getPaid()) {
            throw new IllegalStateException("Invoice is already marked as paid.");
        }

        invoice.setPaid(true);
        invoice.setPaymentDate(LocalDateTime.now());
        invoice.setStatus("PAID"); // Optional if you're using status field
        invoice.setUpdatedAt(LocalDateTime.now());

        invoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponseDto(invoice);
    }
}
