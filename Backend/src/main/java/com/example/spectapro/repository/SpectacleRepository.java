package com.example.spectapro.repository;

import com.example.spectapro.model.Spectacle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpectacleRepository extends JpaRepository<Spectacle, Long> {

    // Trouver spectacles par ville
    @Query("SELECT DISTINCT s FROM Spectacle s " +
           "JOIN s.programmes p " +
           "JOIN p.lieu l " +
           "WHERE LOWER(l.ville) = LOWER(:ville)")
    List<Spectacle> findSpectaclesByVille(@Param("ville") String ville);
    @Query("SELECT DISTINCT s FROM Spectacle s " +
            "JOIN s.programmes p " +
            "JOIN p.lieu l " +
            "WHERE LOWER(s.titre) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR CAST(p.dateProgramme AS string) LIKE CONCAT('%', :query, '%') " +
            "OR STR(p.heureDepart) LIKE CONCAT('%', :query, '%') " +
            "OR CAST(s.duree AS string) LIKE CONCAT('%', :query, '%') " +
            "OR LOWER(l.nomLieu) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(l.ville) LIKE LOWER(CONCAT('%', :query, '%'))")
     List<Spectacle> universalSearch(@Param("query") String query);
}
