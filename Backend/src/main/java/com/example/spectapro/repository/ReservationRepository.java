package com.example.spectapro.repository;

import com.example.spectapro.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Récupère les réservations créées avant une certaine date et dont le paiement n'est pas effectué
    List<Reservation> findByDateCreationBeforeAndPayeFalse(LocalDateTime limitTime);
}