package com.example.saas.user.service;

import com.example.saas.addinvoices.repository.InvoiceRepository;
import com.example.saas.client.repository.ClientRepository;
import com.example.saas.user.dto.ChangePasswordRequest;
import com.example.saas.user.entity.User;
import com.example.saas.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;


    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        invoiceRepository.deleteAllByUserId(userId);
        clientRepository.deleteAllByUserId(userId);

        userRepository.deleteById(userId);
    }
}
