package com.example.saas.client.repository;

import com.example.saas.client.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientReposiotry extends JpaRepository<Client, Integer> {

    List<Client> findByUserId(UUID userId);

}
