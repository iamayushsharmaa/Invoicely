package com.example.saas.addinvoices.service;

import com.example.saas.addinvoices.dto.InvoiceRequestDto;
import com.example.saas.addinvoices.dto.InvoiceResponseDto;
import com.example.saas.addinvoices.mappers.InvoiceMapper;
import com.example.saas.addinvoices.models.Invoice;
import com.example.saas.addinvoices.repository.InvoiceRepository;
import com.example.saas.client.models.Client;
import com.example.saas.client.repository.ClientRepository;
import com.example.saas.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        } else if (invoiceRequestDto.getClientId() != null) {
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

    public List<InvoiceResponseDto> getInvoicesByUser(UUID id) {

    }
}
