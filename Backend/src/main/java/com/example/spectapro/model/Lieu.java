package com.example.spectapro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="LIEU")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDLIEU")
    private Long id;

    @Column(name = "NOMLIEU")
    private String nomLieu;

    @Column(name = "ADRESSE")
    private String adresse;

    @Column(name = "VILLE")
    private String ville;

    @Column(name = "CAPACITE")
    private Integer capacite;

    @Column(name = "TRAVAUX")
    private Boolean travaux;
}
