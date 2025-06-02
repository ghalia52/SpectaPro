package com.example.spectapro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "programme")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Programme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lieu_id", nullable = false)
    private Lieu lieu;

    @Column(name = "date_programme", nullable = false)
    private LocalDate dateProgramme;

    @Column(name = "heure_depart", nullable = false)
    private LocalTime heureDepart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spectacle_id", nullable = false)
    @JsonBackReference
    private Spectacle spectacle;

    // ==== Constructeurs ====

    public Programme() {
        // Constructeur vide pour JPA
    }

    public Programme(Lieu lieu, LocalDate dateProgramme, LocalTime heureDepart, Spectacle spectacle) {
        this.lieu = lieu;
        this.dateProgramme = dateProgramme;
        this.heureDepart = heureDepart;
        this.spectacle = spectacle;
    }

    // ==== Getters et Setters ====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public LocalDate getDateProgramme() {
        return dateProgramme;
    }

    public void setDateProgramme(LocalDate dateProgramme) {
        this.dateProgramme = dateProgramme;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public Spectacle getSpectacle() {
        return spectacle;
    }

    public void setSpectacle(Spectacle spectacle) {
        this.spectacle = spectacle;
    }
}
