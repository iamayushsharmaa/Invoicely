package com.example.saas.addinvoices.service;

import com.example.saas.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendInvoiceEmail(
            String toEmail,
            String subject,
            String message,
            byte[] invoicePdf,
            String fileName,
            User user
    ) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            log.info("Sending email to {}", toEmail);
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(message);
            helper.addAttachment(fileName, new ByteArrayResource(invoicePdf));
            helper.setReplyTo(user.getEmail());

            javaMailSender.send(mimeMessage);
            log.debug("Email content: subject={}, body={}", subject, message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", toEmail, e);
            throw new RuntimeException(e);
        }
    }

}
