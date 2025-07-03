package com.example.saas.addinvoices.controller;

import com.example.saas.addinvoices.dto.InvoiceResponseDto;
import com.example.saas.addinvoices.service.EmailService;
import com.example.saas.addinvoices.service.InvoiceService;
import com.example.saas.addinvoices.service.PdfService;
import com.example.saas.client.dto.ClientResponseDto;
import com.example.saas.client.service.ClientService;
import com.example.saas.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final InvoiceService invoiceService;
    private final PdfService pdfService;
    private final ClientService clientService;

    @PostMapping("/invoices/{invoiceId}/send-mail")
    public ResponseEntity<String> sendInvoiceEmail(
            @PathVariable UUID invoiceId,
            @AuthenticationPrincipal User user
    ) throws IOException {
        InvoiceResponseDto invoice = invoiceService.getInvoiceById(invoiceId, user.getId());

        byte[] pdf = pdfService.generateInvoicePdf(invoice, "default"); // or modern/custom
        UUID clientId = invoice.getClientId();
        ClientResponseDto client = clientService.getClientById(clientId, user.getId());
        String to = client.getEmail();
        String subject = "Invoice #" + invoice.getInvoiceNumber();
        String body = "Hi " + client.getName() + "," + "\n Please find attached your invoice.\nThank you!";

        emailService.sendInvoiceEmail(to, subject, body, pdf, "invoice-" + invoice.getInvoiceNumber() + ".pdf", user);

        return ResponseEntity.ok("Invoice sent to " + to);
    }
}
