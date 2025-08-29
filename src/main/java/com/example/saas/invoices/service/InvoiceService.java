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
import com.example.saas.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceResponseDto createInvoice(InvoiceRequestDto invoiceRequestDto, User user) {
        Client client;

        // ✅ Fetch existing client or create a new one
        if (invoiceRequestDto.getClientId() != null) {
            client = clientRepository.findByIdAndUser_Id(invoiceRequestDto.getClientId(), user.getId())
                    .orElseThrow(() -> new NotFoundException("Client not found"));
        } else if (invoiceRequestDto.getClient() != null) {
            client = clientRepository.save(Client.builder()
                    .name(invoiceRequestDto.getClient().getName())
                    .email(invoiceRequestDto.getClient().getEmail())
                    .phone(invoiceRequestDto.getClient().getPhone())
                    .address(invoiceRequestDto.getClient().getAddress())
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build());
        } else {
            throw new IllegalArgumentException("Either clientId or client details must be provided");
        }

        // ✅ Create and save invoice
        Invoice invoice = invoiceMapper.toEntity(invoiceRequestDto, user, client);
        invoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponseDto(invoice);
    }

    public Page<InvoiceResponseDto> searchInvoices(UUID userId, String invoiceNumber, String clientName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoicePage = invoiceRepository.searchInvoices(userId, invoiceNumber, clientName, pageable);
        return invoicePage.map(invoiceMapper::toResponseDto);
    }

    public List<InvoiceResponseDto> getInvoicesByUser(UUID userId) {
        return invoiceRepository.findAllByUser_Id(userId).stream()
                .map(invoiceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponseDto> getInvoicesByClient(UUID clientId, UUID userId) {
        return invoiceRepository.findByClient_IdAndUser_Id(clientId, userId).stream()
                .map(invoiceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public InvoiceResponseDto getInvoiceById(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUser_Id(invoiceId, userId)
                .orElseThrow(() -> new NotFoundException("Invoice not found or unauthorized"));
        return invoiceMapper.toResponseDto(invoice);
    }

    public void deleteInvoice(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUser_Id(invoiceId, userId)
                .orElseThrow(() -> new NotFoundException("Invoice not found or unauthorized"));
        invoiceRepository.delete(invoice);
    }

    public InvoiceResponseDto updateInvoice(UUID invoiceId, InvoiceRequestDto dto, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUser_Id(invoiceId, userId)
                .orElseThrow(() -> new NotFoundException("Invoice not found or unauthorized"));

        // ✅ Update fields
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

        // ✅ Replace items
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

        // ✅ Recalculate totals
        BigDecimal subTotal = updatedItems.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setSubTotal(subTotal);
        invoice.setTotalAmount(subTotal.subtract(invoice.getDiscount()).add(invoice.getTax()));
        invoice.setUpdatedAt(LocalDateTime.now());

        return invoiceMapper.toResponseDto(invoiceRepository.save(invoice));
    }

    public InvoiceResponseDto markAsPaid(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findByIdAndUser_Id(invoiceId, userId)
                .orElseThrow(() -> new NotFoundException("Invoice not found or unauthorized"));

        if (Boolean.TRUE.equals(invoice.getPaid())) {
            throw new IllegalStateException("Invoice is already marked as paid.");
        }

        invoice.setPaid(true);
        invoice.setPaymentDate(LocalDateTime.now());
        invoice.setStatus("PAID");
        invoice.setUpdatedAt(LocalDateTime.now());

        invoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponseDto(invoice);
    }
}
