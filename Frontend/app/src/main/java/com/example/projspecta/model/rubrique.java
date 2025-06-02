package com.example.projspecta.model;

import com.example.projspecta.model.Artiste;
import com.example.projspecta.model.Spectacle;

public class rubrique {
    private int idrub;
    private Spectacle spectacle;
    private Artiste artiste;
    private float hDebutr;
    private float dureerub;
    private String type;

    public rubrique() {
    }

    public rubrique(int idrub, Spectacle spectacle, Artiste artiste, float hDebutr, float dureerub, String type) {
        this.idrub = idrub;
        this.spectacle = spectacle;
        this.artiste = artiste;
        this.hDebutr = hDebutr;
        this.dureerub = dureerub;
        this.type = type;
    }

    public int getIdrub() {
        return idrub;
    }

    public void setIdrub(int idrub) {
        this.idrub = idrub;
    }

    public Spectacle getSpectacle() {
        return spectacle;
    }

    public void setSpectacle(Spectacle spectacle) {
        this.spectacle = spectacle;
    }

    public Artiste getArtiste() {
        return artiste;
    }

    public void setArtiste(Artiste artiste) {
        this.artiste = artiste;
    }

    public float getHDebutr() {
        return hDebutr;
    }

    public void setHDebutr(float hDebutr) {
        this.hDebutr = hDebutr;
    }

    public float getDureerub() {
        return dureerub;
    }

    public void setDureerub(float dureerub) {
        this.dureerub = dureerub;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
