package com.example.spectapro.controller;

import com.example.spectapro.model.Billet;
import com.example.spectapro.model.Client;
import com.example.spectapro.model.Reservation;
import com.example.spectapro.repository.BilletRepository;
import com.example.spectapro.repository.ClientRepository;
import com.example.spectapro.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Réserve en batch une liste d'objets Reservation envoyés depuis le client.
     * Le JSON attendu est une liste de Reservation sérialisées :
     * chacun contient billet:{idBillet:...}, client:{idclt:...}, quantiteDemandee:...
     */
    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<Boolean> reserverBillets(@RequestBody List<Reservation> reservations) {
        // Collecte des IDs
        List<Long> billetIds = reservations.stream()
                .map(r -> r.getBillet().getIdBillet())
                .collect(Collectors.toList());
        List<Long> clientIds = reservations.stream()
                .map(r -> r.getClient() != null ? r.getClient().getIdclt() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Préfetch billets et clients
        Map<Long, Billet> billetsMap = billetRepository.findAllById(billetIds)
                .stream().collect(Collectors.toMap(Billet::getIdBillet, b -> b));
        Map<Long, Client> clientsMap = clientRepository.findAllById(clientIds)
                .stream().collect(Collectors.toMap(Client::getIdclt, c -> c));

        // Vérification de la disponibilité
        for (Reservation req : reservations) {
            Billet b = billetsMap.get(req.getBillet().getIdBillet());
            if (b == null) {
                logger.error("Billet ID {} not found.", req.getBillet().getIdBillet());
                return ResponseEntity.badRequest().body(false);
            }
            if (b.getQuantite() < req.getQuantiteDemandee()) {
                logger.error("Insufficient stock for Billet ID {}: Available {} | Requested {}",
                        b.getIdBillet(), b.getQuantite(), req.getQuantiteDemandee());
                return ResponseEntity.badRequest().body(false);
            }
        }

        // Traitement et sauvegarde
        LocalDateTime now = LocalDateTime.now();
        for (Reservation req : reservations) {
            Billet b = billetsMap.get(req.getBillet().getIdBillet());
            Client c = req.getClient() != null ? clientsMap.get(req.getClient().getIdclt()) : null;

            // Mise à jour du stock
            b.setQuantite(b.getQuantite() - req.getQuantiteDemandee());
            billetRepository.save(b);

            // Lie les entités gérées et sauvegarde la réservation
            req.setBillet(b);
            req.setClient(c);
            req.setDateCreation(now);
            req.setPaye(false);
            reservationRepository.save(req);

            logger.info("Reservation successful: Billet ID {} | Client ID {} | Quantity {}",
                    b.getIdBillet(), c != null ? c.getIdclt() : null, req.getQuantiteDemandee());
        }
        return ResponseEntity.ok(true);
    }

    /**
     * Tâche planifiée qui s'exécute toutes les heures pour supprimer les réservations
     * non payées qui ont plus de 24 heures
     */
    @Scheduled(fixedRate = 3600000) // Exécuté toutes les heures (3600000 ms)
    @Transactional
    public void nettoyerReservationsExpirees() {
        LocalDateTime limitTime = LocalDateTime.now().minusHours(24);
        
        List<Reservation> expiredReservations = reservationRepository.findByDateCreationBeforeAndPayeFalse(limitTime);
        
        if (!expiredReservations.isEmpty()) {
            logger.info("Found {} expired reservations to clean up", expiredReservations.size());
            
            // Restitution des quantités de billets
            for (Reservation reservation : expiredReservations) {
                Billet billet = reservation.getBillet();
                billet.setQuantite(billet.getQuantite() + reservation.getQuantiteDemandee());
                billetRepository.save(billet);
                
                logger.info("Restoring {} tickets to billet ID {} from expired reservation ID {}",
                        reservation.getQuantiteDemandee(), billet.getIdBillet(), reservation.getId());
            }
            
            // Suppression des réservations expirées
            reservationRepository.deleteAll(expiredReservations);
            logger.info("Successfully cleaned up {} expired reservations", expiredReservations.size());
        }
    }
    
    /**
     * Marque une réservation comme payée (généralement utilisée par l'administrateur ou le système de paiement)
     */
    @PostMapping("/{id}/payer")
    @Transactional
    public ResponseEntity<Boolean> marquerCommePaye(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            logger.error("Reservation ID {} not found for payment marking.", id);
            return ResponseEntity.notFound().build();
        }

        reservation.setPaye(true);
        reservationRepository.save(reservation);
        logger.info("Payment marked for reservation ID {}", id);
        
        return ResponseEntity.ok(true);
    }
}