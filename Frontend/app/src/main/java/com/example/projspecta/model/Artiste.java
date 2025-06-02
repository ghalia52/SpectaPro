package com.example.projspecta.model;

public class Artiste {
    private int idart;
    private String nomart;
    private String prenomart;
    private String specialite;

    public Artiste() {
    }

    public Artiste(int idart, String nomart, String prenomart, String specialite) {
        this.idart = idart;
        this.nomart = nomart;
        this.prenomart = prenomart;
        this.specialite = specialite;
    }

    public int getIdart() {
        return idart;
    }

    public void setIdart(int idart) {
        this.idart = idart;
    }

    public String getNomart() {
        return nomart;
    }

    public void setNomart(String nomart) {
        this.nomart = nomart;
    }

    public String getPrenomart() {
        return prenomart;
    }

    public void setPrenomart(String prenomart) {
        this.prenomart = prenomart;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}
