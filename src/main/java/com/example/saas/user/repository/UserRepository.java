package com.example.saas.user.repository;


import com.example.saas.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    void deleteAllByUserId(UUID userId);
}
