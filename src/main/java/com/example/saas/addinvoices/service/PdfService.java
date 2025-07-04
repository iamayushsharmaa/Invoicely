package com.example.saas.addinvoices.service;

import com.example.saas.addinvoices.dto.InvoiceResponseDto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final TemplateEngine templateEngine;

    public byte[] generateInvoicePdf(InvoiceResponseDto invoice, String templateName) throws IOException {
        String templateToUse = "invoice-template-" + (templateName != null ? templateName : "default");

        log.info("Generating PDF for invoice ID: {}", invoice.getId());
        log.debug("Using template: {}", templateToUse);

        try {
            Context context = new Context();
            context.setVariable("invoice", invoice);

            String html = templateEngine.process(templateToUse, context);
            log.debug("Rendered HTML for invoice ID {}: {}", invoice.getId(), html);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            log.info("Successfully generated PDF for invoice ID: {}", invoice.getId());
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF for invoice ID: {}", invoice.getId(), e);
            throw new IOException("PDF generation failed", e);
        }
    }
}
