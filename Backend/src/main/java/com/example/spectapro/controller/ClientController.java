package com.example.spectapro.controller;

import com.example.spectapro.model.Client;
import com.example.spectapro.model.LoginRequest;
import com.example.spectapro.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Authentifie l'utilisateur et renvoie l'objet Client complet si les identifiants sont valides.
     * Sinon renvoie 401 UNAUTHORIZED.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Client> client = clientRepository.findByEmailAndMotP(
                loginRequest.getEmail(), loginRequest.getPassword());

        if (client.isPresent()) {
            return ResponseEntity.ok(client.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FAIL");
        }
    }

    /**
     * Récupère les informations d'un client par son ID.
     * GET /api/clients/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return clientRepository.findById(id)
                .map(c -> ResponseEntity.ok(c))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Client client) {
        // Vérifier si l'email existe déjà
        if (clientRepository.findByEmail(client.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cet email est déjà utilisé");
        }

        // Sauvegarder le nouveau client
        Client savedClient = clientRepository.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }
}
