package com.example.spectapro.controller;

import com.example.spectapro.model.Billet;
import com.example.spectapro.model.Reservation;
import com.example.spectapro.repository.BilletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/billets")
@CrossOrigin(origins = "*")
public class BilletController {

    private static final Logger logger = LoggerFactory.getLogger(BilletController.class);

    @Autowired
    private BilletRepository billetRepository;

    @GetMapping("/billets/available/{idSpec}")
    public List<Billet> getAvailableBillets(@PathVariable Long idSpec) {
        return billetRepository.findAvailableBilletsBySpectacleId(idSpec);
    }

    @PostMapping("/reserver")
    @Transactional
    public ResponseEntity<String> reserver(@RequestBody List<Reservation> demandes) {
        // Retrieve billets in advance to minimize DB calls
        List<Billet> billetsToUpdate = new ArrayList<>();

        // Check billet availability
        for (Reservation req : demandes) {
            Optional<Billet> opt = billetRepository.findById(req.getId());
            if (opt.isEmpty()) {
                logger.error("Billet ID {} not found.", req.getId());
                return ResponseEntity.status(404).body("Billet not found for ID: " + req.getId());
            }

            Billet b = opt.get();
            logger.info("Billet ID: {} | Quantity in DB: {} | Requested: {}", b.getIdBillet(), b.getQuantite(), req.getQuantiteDemandee());

            if (b.getQuantite() < req.getQuantiteDemandee()) {
                return ResponseEntity.status(400).body("Insufficient stock for Billet ID: " + req.getId());
            }

            // Store billets to update later
            billetsToUpdate.add(b);
        }

        // Update billet quantities in the database
        for (Reservation req : demandes) {
            Billet b = billetsToUpdate.stream()
                    .filter(billet -> billet.getIdBillet().equals(req.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Billet not found for ID: " + req.getId()));

            b.setQuantite(b.getQuantite() - req.getQuantiteDemandee());
            billetRepository.save(b);
        }

        return ResponseEntity.ok("Reservation successfully confirmed.");
    }
}
