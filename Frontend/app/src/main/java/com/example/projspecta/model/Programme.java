package com.example.projspecta.model;

public class Programme {
    private Long id;
    private Lieu lieu;
    private Spectacle spectacle;
    private String dateProgramme; // Format yyyy-MM-dd
    private String heureDepart;   // Format HH:mm:

    // Getter et Setter pour id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter et Setter pour lieu
    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    // Getter et Setter pour spectacle
    public Spectacle getSpectacle() {
        return spectacle;
    }

    public void setSpectacle(Spectacle spectacle) {
        this.spectacle = spectacle;
    }

    // Getter et Setter pour dateProgramme
    public String getDateProgramme() {
        return dateProgramme;
    }

    public void setDateProgramme(String dateProgramme) {
        this.dateProgramme = dateProgramme;
    }

    // Getter et Setter pour heureDepart
    public String getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(String heureDepart) {
        this.heureDepart = heureDepart;
    }
}
