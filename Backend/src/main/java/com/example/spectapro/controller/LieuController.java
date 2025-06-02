package com.example.spectapro.controller;

import com.example.spectapro.model.Lieu;
import com.example.spectapro.repository.LieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LieuController {
    @Autowired
    private LieuRepository lieuRepository;

    @GetMapping("/villes")
    public List<String> getVilles() {
        return lieuRepository.findAllVilles();
    }

}

