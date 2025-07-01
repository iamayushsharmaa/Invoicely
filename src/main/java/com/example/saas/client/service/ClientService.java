package com.example.saas.client.service;

import com.example.saas.client.dto.ClientRequestDto;
import com.example.saas.client.dto.ClientResponseDto;
import com.example.saas.client.mapper.ClientMapper;
import com.example.saas.client.models.Client;
import com.example.saas.client.repository.ClientRepository;
import com.example.saas.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientResponseDto createClient(ClientRequestDto dto, UUID userId) {
        Client client = clientMapper.toEntity(dto, userId);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    public List<ClientResponseDto> getClientsByUser(UUID userId) {
        return clientRepository.findAllByUserId(userId)
                .stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ClientResponseDto getClientById(UUID clientId, UUID userId) {
        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        return clientMapper.toResponse(client);
    }

    public ClientResponseDto updateClient(UUID clientId, ClientRequestDto dto, UUID userId) {
        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        clientMapper.updateEntity(client, dto);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    public void deleteClient(UUID clientId, UUID userId) {
        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        clientRepository.delete(client);
    }
}
