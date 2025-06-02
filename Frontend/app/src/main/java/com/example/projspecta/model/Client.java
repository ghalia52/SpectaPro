package com.example.projspecta.model;

public class Client {
    private Long idclt;
    private String nomclt;
    private String prenomclt;
    private String tel;
    private String email;
    private String motP;

    // Default constructor needed for JSON deserialization
    public Client() {
    }

    // Fixed constructor that properly sets the ID
    public Client(Long clientId) {
        this.idclt = clientId;
    }

    public Long getIdclt() {
        return idclt;
    }

    public void setIdclt(Long idclt) {
        this.idclt = idclt;
    }

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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotP() {
        return motP;
    }

    public void setMotP(String motP) {
        this.motP = motP;
    }
}