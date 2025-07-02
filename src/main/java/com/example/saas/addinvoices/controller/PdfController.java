package com.example.saas.addinvoices.controller;

import com.example.saas.addinvoices.dto.InvoiceResponseDto;
import com.example.saas.addinvoices.service.InvoiceService;
import com.example.saas.addinvoices.service.PdfService;
import com.example.saas.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@AllArgsConstructor
public class PdfController {

    private final InvoiceService invoiceService;
    private final PdfService pdfService;

    @PostMapping("/invoices/{invoiceId}/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable UUID invoiceId,
            @RequestParam(defaultValue = "default") String template,
            @AuthenticationPrincipal User user
    ) throws IOException {
        InvoiceResponseDto invoice = invoiceService.getInvoiceById(invoiceId, user.getId());

        byte[] pdfBytes = pdfService.generateInvoicePdf(invoice, template);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-" + invoice.getInvoiceNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
