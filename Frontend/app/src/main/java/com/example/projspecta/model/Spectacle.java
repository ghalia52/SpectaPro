package com.example.projspecta.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Spectacle {
    private Long idSpec;
    private String titre;
    private Double duree;
    private List<Programme> programmes; // Nouveau : liste des programmes
    private String annulee;
    private Integer nombreRubrique;
    private String imagePath;
    private Long nbrSpectateur;

    // Getters
    public Long getIdSpec() {
        return idSpec;
    }

    public String getTitre() {
        return titre;
    }

    public Double getDuree() {
        return duree;
    }

    public List<Programme> getProgrammes() {
        return programmes;
    }

    public String getAnnulee() {
        return annulee;
    }

    public Integer getNombreRubrique() {
        return nombreRubrique;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Long getNbrSpectateur() {
        return nbrSpectateur;
    }

    // Setters
    public void setIdSpec(Long idSpec) {
        this.idSpec = idSpec;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDuree(Double duree) {
        this.duree = duree;
    }

    public void setProgrammes(List<Programme> programmes) {
        this.programmes = programmes;
    }

    public void setAnnulee(String annulee) {
        this.annulee = annulee;
    }

    public void setNombreRubrique(Integer nombreRubrique) {
        this.nombreRubrique = nombreRubrique;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setNbrSpectateur(Long nbrSpectateur) {
        this.nbrSpectateur = nbrSpectateur;
    }
}
