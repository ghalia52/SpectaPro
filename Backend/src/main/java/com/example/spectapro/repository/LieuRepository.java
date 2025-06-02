package com.example.spectapro.repository;

import com.example.spectapro.model.Lieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LieuRepository extends JpaRepository<Lieu, Long> {
    @Query("SELECT DISTINCT l.ville FROM Lieu l")
    List<String> findAllVilles();
}

