package com.example.saas.addinvoices.controller;

import com.example.saas.addinvoices.dto.InvoiceRequestDto;
import com.example.saas.addinvoices.dto.InvoiceResponseDto;
import com.example.saas.addinvoices.service.InvoiceService;
import com.example.saas.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/api/v1")
@RequiredArgsConstructor
public class InovicesController {

    private final InvoiceService invoiceService;

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponseDto> addInvoice(
            @RequestBody InvoiceRequestDto invoiceRequestDto,
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
            @RequestBody InvoiceRequestDto dto,
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
