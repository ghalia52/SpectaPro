package com.example.projspecta.model;


import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    private String nomclt;      // Nom (last name)
    private String prenomclt;   // Prénom (first name)
    private String email;       // Email
    private String tel;         // Téléphone

    @SerializedName("motP")

    private String password;    // Mot de passe

    public RegisterRequest(String nomclt, String prenomclt, String email, String tel, String password) {
        this.nomclt = nomclt;
        this.prenomclt = prenomclt;
        this.email = email;
        this.tel = tel;
        this.password = password;
    }

    // Getters and setters
    public String getNomclt() {
        return nomclt;
    }

    public void setNomclt(String nomclt) {
        this.nomclt = nomclt;
    }

    public String getPrenomclt() {
        return prenomclt;
    }

    public void setPrenomclt(String prenomclt) {
        this.prenomclt = prenomclt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
