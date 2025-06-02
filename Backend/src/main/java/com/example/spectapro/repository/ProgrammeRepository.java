package com.example.spectapro.repository;

import com.example.spectapro.model.Lieu;
import com.example.spectapro.model.Programme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgrammeRepository extends JpaRepository<Programme, Long> {
    // Chercher les programmes par lieu
    List<Programme> findByLieuId(Long lieuId);

    // Chercher les programmes par spectacle
    List<Programme> findBySpectacle_idSpec(Long spectacleId);
    @Query("SELECT DISTINCT p.lieu FROM Programme p WHERE p.spectacle.idSpec = :spectacleId")
    List<Lieu> findDistinctVenuesBySpectacleId(@Param("spectacleId") Long spectacleId);
}
