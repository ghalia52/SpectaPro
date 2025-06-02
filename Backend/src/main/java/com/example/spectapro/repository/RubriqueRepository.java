package com.example.spectapro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spectapro.model.rubrique;

@Repository
public interface RubriqueRepository extends JpaRepository<rubrique, Long> {

	@Query("SELECT r FROM rubrique r JOIN r.artiste a WHERE r.spectacle.id = :spectacleId")
	List<rubrique> findBySpectacleId(@Param("spectacleId") Long spectacleId);

}
