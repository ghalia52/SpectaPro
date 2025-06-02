package com.example.projspecta.model;

public class Lieu {
    private Long idLieu;
    private String ville;
    private String nomLieu;

    // Getters
    public Long getIdLieu() {
        return idLieu;
    }

    public String getVille() {
        return ville;
    }



    // Setters
    public void setIdLieu(Long idLieu) {
        this.idLieu = idLieu;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getNom() {
        return nomLieu;

    }
    public String toString() {
        return nomLieu;
    }
}