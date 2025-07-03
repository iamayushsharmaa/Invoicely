package com.example.saas.user.service;

import com.example.saas.addinvoices.repository.InvoiceRepository;
import com.example.saas.client.repository.ClientRepository;
import com.example.saas.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public void deleteAccount(UUID userId) {
        invoiceRepository.deleteAllByUserId(userId);
        clientRepository.deleteAllByUserId(userId);

        userRepository.deleteById(userId);
    }
}
