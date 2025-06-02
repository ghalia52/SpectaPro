package com.example.spectapro.controller;

import com.example.spectapro.model.Programme;
import com.example.spectapro.model.Spectacle;
import com.example.spectapro.repository.ProgrammeRepository;
import com.example.spectapro.repository.SpectacleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spectacles")
@CrossOrigin(origins = "*")
public class SpectacleController {

    @Autowired
    private SpectacleRepository spectacleRepository;

    @Autowired
    private ProgrammeRepository programmeRepository;  // Remplacer DateSpectacle et HeureSpectacle par ProgrammeRepository

    @GetMapping
    public List<Spectacle> getSpectacles() {
        return spectacleRepository.findAll();
    }

    @GetMapping("/ville/{ville}")
    public List<Spectacle> getSpectaclesByVille(@PathVariable String ville) {
        return spectacleRepository.findSpectaclesByVille(ville);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Spectacle> getSpectacleById(@PathVariable Long id) {
        return spectacleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Spectacle> universalSearch(@RequestParam(required = false) String query) {
        return spectacleRepository.universalSearch(query);
    }

    @GetMapping("/{id}/programmes")
    public List<Programme> getProgrammesForSpectacle(@PathVariable Long id) {
        // Remplacer les anciennes méthodes qui récupéraient DateSpectacle et HeureSpectacle
        return programmeRepository.findBySpectacle_idSpec(id);  // Recherche des programmes par l'ID du spectacle
    }
}
