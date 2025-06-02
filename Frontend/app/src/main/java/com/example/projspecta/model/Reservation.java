package com.example.projspecta.model;

public class Reservation {
    private Billet billet;
    private Client client;
    private int quantiteDemandee;

    public Reservation(Billet billet, Client client, int quantiteDemandee) {
        this.billet = billet;
        this.client = client;
        this.quantiteDemandee = quantiteDemandee;
    }


    public Billet getBillet() {
        return billet;
    }

    public void setBillet(Billet billet) {
        this.billet = billet;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getQuantiteDemandee() {
        return quantiteDemandee;
    }

    public void setQuantiteDemandee(int quantiteDemandee) {
        this.quantiteDemandee = quantiteDemandee;
    }
}
