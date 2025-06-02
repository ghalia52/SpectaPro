package com.example.spectapro.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference; // <-- ajouter
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spectacle")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Spectacle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSPEC")
    private Long idSpec;

    @Column(name = "TITRE", nullable = false, length = 500)
    private String titre;

    @Column(name = "DUREE", nullable = false)
    private Double duree;

    @OneToMany(mappedBy = "spectacle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // <-- ajouter ici
    private List<Programme> programmes;

    @Column(name = "ANNULEE", length = 1)
    private String annulee;

    @Column(name = "NOMBRE_RUBRIQUE")
    private Integer nombreRubrique;

    @Column(name = "IMAGE_PATH", length = 255)
    private String imagePath;

    @Column(name = "NBR_SPECTATEUR")
    private Long nbrSpectateur;
}
