package com.example.saas.client.controller;

import com.example.saas.client.dto.ClientDto;
import com.example.saas.client.models.Client;
import com.example.saas.client.service.ClientService;
import com.example.saas.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(
            @RequestBody ClientDto clientRequest,
            @AuthenticationPrincipal User user
    ) {
        Client client = clientService.createClient(clientRequest, user.getId());
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    @GetMapping
    public void fetchClient(@RequestBody ClientDto clientRequest, @AuthenticationPrincipal User user) {

    }
}
