package com.example.saas.client.repository;

import com.example.saas.client.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findAllByUser_Id(UUID userId);

    Optional<Client> findByIdAndUser_Id(UUID clientId, UUID userId);

    void deleteAllByUser_Id(UUID userId);
}