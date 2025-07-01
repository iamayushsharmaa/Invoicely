package com.example.saas.client.mapper;

import com.example.saas.client.dto.ClientRequestDto;
import com.example.saas.client.dto.ClientResponseDto;
import com.example.saas.client.models.Client;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequestDto dto, UUID userId) {
        return Client.builder()
                .userId(userId)
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateEntity(Client client, ClientRequestDto dto) {
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());
    }

    public ClientResponseDto toResponse(Client client) {
        return new ClientResponseDto(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress()
        );
    }
}
