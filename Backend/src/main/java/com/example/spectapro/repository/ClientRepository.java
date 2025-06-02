package com.example.spectapro.repository;

import com.example.spectapro.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmailAndMotP(String email, String motP);
    Optional<Client> findByEmail(String email);
}
