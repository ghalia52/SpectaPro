package com.example.projspecta.model;

import com.google.gson.annotations.SerializedName;

public class Billet {

    @SerializedName("categorie")
    private String type;

    @SerializedName("prix")
    private double price;

    @SerializedName("vendu")
    private String vendu;

    @SerializedName("idBillet")
    private Long idBillet;

    @SerializedName("spectacle")
    private Spectacle spectacle;

    // 1) Constructeur sans-args pour Gson
    public Billet() { }

    // 2) Constructeur existant (id seul) — garde-le si tu l'utilises ailleurs
    public Billet(Long id) {
        this.idBillet = id;
    }

    // 3) Nouveau constructeur complet pour le groupement
    public Billet(Long idBillet, String type, double price, String vendu) {
        this.idBillet = idBillet;
        this.type      = type;
        this.price     = price;
        this.vendu     = vendu;
    }

    // Getters & Setters
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public String getVendu() {
        return vendu;
    }
    public void setVendu(String vendu) {
        this.vendu = vendu;
    }

    public Long getIdBillet() {
        return idBillet;
    }
    public void setIdBillet(Long idBillet) {
        this.idBillet = idBillet;
    }

    public Spectacle getSpectacle() {
        return spectacle;
    }
    public void setSpectacle(Spectacle spectacle) {
        this.spectacle = spectacle;
    }
    @SerializedName("quantite")  // Assure-toi que ce nom correspond à celui utilisé dans le backend
    private int quantite;

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantiteDisponible) {
        this.quantite = quantiteDisponible;
    }


    // Méthode utilitaire
    public boolean isAvailable() {
        return "Non".equalsIgnoreCase(vendu); // dispo si pas encore vendu
    }
}
