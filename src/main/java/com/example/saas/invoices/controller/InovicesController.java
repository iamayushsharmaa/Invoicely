package com.example.saas.invoices.controller;

import com.example.saas.invoices.dto.InvoiceRequestDto;
import com.example.saas.invoices.dto.InvoiceResponseDto;
import com.example.saas.invoices.service.InvoiceService;
import com.example.saas.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController("/api/v1")
@RequiredArgsConstructor
public class InovicesController {

    private final InvoiceService invoiceService;

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponseDto> addInvoice(
            @Valid @RequestBody InvoiceRequestDto invoiceRequestDto,
            @AuthenticationPrincipal User user
    ) {
        InvoiceResponseDto response = invoiceService.createInvoice(invoiceRequestDto, user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponseDto>> getAllInvoices(
            @AuthenticationPrincipal User user
    ) {
        List<InvoiceResponseDto> invoices = invoiceService.getInvoicesByUser(user.getId());
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/invoice/search")
    public ResponseEntity<Page<InvoiceResponseDto>> searchInvoice(
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        Page<InvoiceResponseDto> result = invoiceService.searchInvoices(
                user.getId(), invoiceNumber, clientName, page, size
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/invoices/client/{clientId}")
    public ResponseEntity<List<InvoiceResponseDto>> getInvoicesByClient(
            @PathVariable UUID clientId,
            @AuthenticationPrincipal User user
    ) {
        List<InvoiceResponseDto> clientInvoices = invoiceService.getInvoicesByClient(clientId, user.getId());
        return new ResponseEntity<>(clientInvoices, HttpStatus.OK);
    }

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoiceResponseDto> getInvoice(
            @PathVariable UUID invoiceId,
            @AuthenticationPrincipal User user
    ) {
        InvoiceResponseDto invoice = invoiceService.getInvoiceById(invoiceId, user.getId());
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @DeleteMapping("/invoices/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(
            @PathVariable UUID invoiceId,
            @AuthenticationPrincipal User user
    ) {
        invoiceService.deleteInvoice(invoiceId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoiceResponseDto> updateInvoice(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody InvoiceRequestDto dto,
            @AuthenticationPrincipal User user
    ) {
        InvoiceResponseDto updated = invoiceService.updateInvoice(invoiceId, dto, user.getId());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/invoices/{invoiceId}/mark-paid")
    public ResponseEntity<InvoiceResponseDto> markInvoiceAsPaid(
            @PathVariable UUID invoiceId,
            @AuthenticationPrincipal User user
    ) {
        InvoiceResponseDto updatedInvoice = invoiceService.markAsPaid(invoiceId, user.getId());
        return ResponseEntity.ok(updatedInvoice);
    }

}
