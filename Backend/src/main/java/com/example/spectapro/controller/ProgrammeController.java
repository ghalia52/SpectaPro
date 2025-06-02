package com.example.spectapro.controller;

import com.example.spectapro.model.Lieu;
import com.example.spectapro.model.Programme;
import com.example.spectapro.repository.ProgrammeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programmes")
public class ProgrammeController {

    @Autowired
    private ProgrammeRepository programmeRepository;

    // Récupérer tous les programmes
    @GetMapping
    public List<Programme> getAllProgrammes() {
        return programmeRepository.findAll();
    }

    // Récupérer les programmes par lieu
    @GetMapping("/lieu/{lieuId}")
    public List<Programme> getProgrammesByLieu(@PathVariable Long lieuId) {
        return programmeRepository.findByLieuId(lieuId);
    }

    // Récupérer les programmes par spectacle
    @GetMapping("/spectacle/{spectacleId}")
    public List<Programme> getProgrammesBySpectacle(@PathVariable Long spectacleId) {
        return programmeRepository.findBySpectacle_idSpec(spectacleId);
    }
    @GetMapping("/spectacle/{spectacleId}/venues")
    public List<Lieu> getVenuesForSpectacle(@PathVariable Long spectacleId) {
        return programmeRepository.findDistinctVenuesBySpectacleId(spectacleId);
    }
}
