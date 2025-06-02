package com.example.spectapro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "BILLET")
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDBILLET")
    private Long idBillet;

    @Column(name = "CATEGORIE")
    private String categorie;

    @Column(name = "PRIX")
    private double prix;

    @Column(name = "VENDU")
    private String vendu;

    // ← Nouveau champ quantité
    @Column(name = "QUANTITE", nullable = false)
    private int quantite;

    @ManyToOne
    @JoinColumn(name = "IDSPEC")
    private Spectacle spectacle;

    // Getters et Setters

    public Long getIdBillet() {
        return idBillet;
    }

    public void setIdBillet(Long idBillet) {
        this.idBillet = idBillet;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getVendu() {
        return vendu;
    }

    public void setVendu(String vendu) {
        this.vendu = vendu;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Spectacle getSpectacle() {
        return spectacle;
    }

    public void setSpectacle(Spectacle spectacle) {
        this.spectacle = spectacle;
    }
}
