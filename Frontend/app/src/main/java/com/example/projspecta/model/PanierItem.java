package com.example.projspecta.model;

public class PanierItem {
    // ← Nouvel attribut pour l’ID du billet
    private long billetId;

    private long spectacleId;
    private String categorieId;
    private double prix;
    private String description;
    private int quantite;
    private String spectacleName;
    private String spectacleImageUrl;

    // Getters & Setters

    public long getBilletId() {
        return billetId;
    }

    public void setBilletId(long billetId) {
        this.billetId = billetId;
    }

    public long getSpectacleId() {
        return spectacleId;
    }

    public void setSpectacleId(long spectacleId) {
        this.spectacleId = spectacleId;
    }

    public String getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(String categorieId) {
        this.categorieId = categorieId;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getSpectacleName() {
        return spectacleName;
    }

    public void setSpectacleName(String spectacleName) {
        this.spectacleName = spectacleName;
    }

    public String getSpectacleImageUrl() {
        return spectacleImageUrl;
    }

    public void setSpectacleImageUrl(String spectacleImageUrl) {
        this.spectacleImageUrl = spectacleImageUrl;
    }
}
