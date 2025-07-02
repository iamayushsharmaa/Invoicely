package com.example.saas.addinvoices.service;

import com.example.saas.addinvoices.dto.InvoiceRequestDto;
import com.example.saas.addinvoices.dto.InvoiceResponseDto;
import com.example.saas.addinvoices.mappers.InvoiceMapper;
import com.example.saas.addinvoices.models.Invoice;
import com.example.saas.addinvoices.models.InvoiceItem;
import com.example.saas.addinvoices.repository.InvoiceRepository;
import com.example.saas.client.models.Client;
import com.example.saas.client.repository.ClientRepository;
import com.example.saas.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                    .build()
            );
        } else {
            throw new IllegalArgumentException("Either clientId or client details must be provided");
        }

        Invoice invoice = invoiceMapper.toEntity(invoiceRequestDto, userId, client.getId());
        invoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponseDto(invoice);
    }

    public List<InvoiceResponseDto> getInvoicesByUser(UUID userId) {
        List<Invoice> invoices = invoiceRepository.findAllByUserId(userId);
        return invoices.stream()
                .map(invoiceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponseDto> getInvoicesByClient(UUID clientId, UUID userId) {
        List<Invoice> invoices = invoiceRepository.findByClientIdAndUserId(clientId, userId);

        return invoices.stream()
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

        // recalculate total
        BigDecimal totalAmount = updatedItems.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalAmount(totalAmount);

        return invoiceMapper.toResponseDto(invoiceRepository.save(invoice));
    }

}
