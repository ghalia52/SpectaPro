package com.example.spectapro.repository;

import com.example.spectapro.model.Billet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BilletRepository extends JpaRepository<Billet, Long> {
	@Query("SELECT b FROM Billet b WHERE b.spectacle.idSpec = :idSpec AND b.vendu = 'Non'")
	List<Billet> findAvailableBilletsBySpectacleId(@Param("idSpec") Long idSpec);
	

}
