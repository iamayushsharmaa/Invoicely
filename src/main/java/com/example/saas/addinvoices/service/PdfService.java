package com.example.saas.addinvoices.service;

import com.example.saas.addinvoices.dto.InvoiceResponseDto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class PdfService {

    final TemplateEngine templateEngine;

    public byte[] generateInvoicePdf(InvoiceResponseDto invoice) {

        Context context = new Context();
        context.setVariable("invoice", invoice);

        String html = templateEngine.process("invoice-template", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        try {
            builder.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
    }

}
