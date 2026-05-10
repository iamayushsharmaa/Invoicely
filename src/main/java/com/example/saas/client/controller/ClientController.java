package com.example.saas.client.controller;

import com.example.saas.client.dto.ClientRequestDto;
import com.example.saas.client.dto.ClientResponseDto;
import com.example.saas.client.service.ClientService;
import com.example.saas.invoices.dto.InvoiceResponseDto;
import com.example.saas.invoices.service.InvoiceService;
import com.example.saas.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(
            @Valid @RequestBody ClientRequestDto clientRequest,
            @AuthenticationPrincipal User user
    ) {
        ClientResponseDto client = clientService.createClient(clientRequest, user);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> fetchClients(@AuthenticationPrincipal User user) {
        List<ClientResponseDto> clients = clientService.getClientsByUser(user.getId());
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/invoices/client/{clientId}")
    public ResponseEntity<List<InvoiceResponseDto>> getInvoicesByClient(
            @PathVariable UUID clientId,
            @AuthenticationPrincipal User user
    ) {
        List<InvoiceResponseDto> invoices = invoiceService.getInvoicesByClient(clientId, user.getId());
        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable UUID clientId,
            @Valid @RequestBody ClientRequestDto clientRequest,
            @AuthenticationPrincipal User user
    ) {
        ClientResponseDto updatedClient = clientService.updateClient(clientId, clientRequest, user.getId());
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable UUID clientId,
            @AuthenticationPrincipal User user
    ) {
        clientService.deleteClient(clientId, user.getId());
        return ResponseEntity.noContent().build();
    }

}
