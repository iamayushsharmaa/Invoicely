package com.example.saas.client.service;

import com.example.saas.client.dto.ClientDto;
import com.example.saas.client.models.Client;
import com.example.saas.client.repository.ClientReposiotry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientReposiotry clientReposiotry;

    public Client createClient(ClientDto clientRequest, UUID userId) {
        Client client = Client.builder()
                .userId(userId)
                .name(clientRequest.getName())
                .email(clientRequest.getEmail())
                .phone(clientRequest.getPhone())
                .address(clientRequest.getAddress())
                .createdAt(LocalDateTime.now())
                .build();

        return clientReposiotry.save(client);
    }
}
