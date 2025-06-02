package com.example.projspecta;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.example.projspecta.model.Billet;
import com.example.projspecta.model.Client;
import com.example.projspecta.model.PanierItem;
import com.example.projspecta.model.Reservation;
import com.example.projspecta.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReservationHelper {
    private static final String TAG = "ReservationHelper";

    /**
     * Effectue la réservation en utilisant les informations déjà récupérées du client.
     * @param context  Contexte de l'appel
     * @param nom      Nom complet du client
     * @param email    Email du client
     * @param tel      Téléphone du client
     */
    public static void reserver(Context context, String nom, String email, String tel) {
        List<PanierItem> panier = PanierManager.getInstance(context).getPanierList();
        if (panier.isEmpty()) {
            Toast.makeText(context, "Le panier est vide", Toast.LENGTH_LONG).show();
            return;
        }

        if (nom == null || nom.isEmpty() || email == null || email.isEmpty() || tel == null || tel.isEmpty()) {
            Toast.makeText(context, "Veuillez remplir toutes les informations client.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Email invalide.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.PHONE.matcher(tel).matches()) {
            Toast.makeText(context, "Numéro de téléphone invalide.", Toast.LENGTH_LONG).show();
            return;
        }

        Long clientId = SessionManager.getInstance(context).getClientId();
        Log.d(TAG, "Client ID from SessionManager: " + clientId);

        // Vérifier si un client est connecté et a un ID valide
        boolean isLoggedIn = (clientId != null && clientId > 0);

        List<Reservation> demandes = new ArrayList<>();
        for (PanierItem it : panier) {
            Billet billet = new Billet(it.getBilletId());

            Client client = null;
            if (isLoggedIn) {
                // Si connecté, créer un client avec l'ID
                client = new Client(clientId);
                Log.d(TAG, "Creating reservation with client ID: " + client.getIdclt());
            }

            // Créer la réservation
            Reservation reservation = new Reservation(billet, client, it.getQuantite());
            demandes.add(reservation);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.reserver(demandes).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> resp) {
                if (resp.isSuccessful() && Boolean.TRUE.equals(resp.body())) {
                    Toast.makeText(context, "Réservation confirmée !", Toast.LENGTH_LONG).show();
                    PanierManager.getInstance(context).viderPanier();
                } else {
                    Toast.makeText(context, "Quantité insuffisante pour un ou plusieurs billets", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "API error: ", t);
                Toast.makeText(context, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}