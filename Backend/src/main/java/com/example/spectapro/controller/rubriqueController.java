package com.example.spectapro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.spectapro.model.rubrique;
import com.example.spectapro.repository.RubriqueRepository;

@RestController
@RequestMapping("/api/rubriques")
public class rubriqueController {

    @Autowired
    private RubriqueRepository rubriqueRepository;

    @GetMapping("/bySpectacle/{id}")
    public List<rubrique> getRubriquesBySpectacle(@PathVariable Long id) {
        return rubriqueRepository.findBySpectacleId(id);
    }
}
