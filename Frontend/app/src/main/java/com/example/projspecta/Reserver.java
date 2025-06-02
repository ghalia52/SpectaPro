package com.example.projspecta;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projspecta.model.Billet;
import com.example.projspecta.model.Client;
import com.example.projspecta.model.EmailRequest;
import com.example.projspecta.model.PanierItem;
import com.example.projspecta.model.Reservation;
import com.example.projspecta.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Reserver extends AppCompatActivity {

    private ApiService apiService;
    private TextInputEditText fullNameEditText, emailEditText, phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserver);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets s = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(s.left, s.top, s.right, s.bottom);
            return insets;
        });

        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> finish());

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/") // Backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        findViewById(R.id.reserverButton).setOnClickListener(v -> reserver());
    }

    private void reserver() {
        List<PanierItem> panier = PanierManager.getInstance(this).getPanierList();
        if (panier.isEmpty()) {
            Toast.makeText(this, "Le panier est vide", Toast.LENGTH_LONG).show();
            return;
        }

        // Récupérer les infos client
        String nom = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String telephone = phoneEditText.getText().toString().trim();

        // Validation des champs
        if (nom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email invalide.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Patterns.PHONE.matcher(telephone).matches()) {
            Toast.makeText(this, "Numéro de téléphone invalide.", Toast.LENGTH_LONG).show();
            return;
        }

        List<Reservation> demandes = new ArrayList<>();
        Long clientId = SessionManager.getInstance(this).getClientId();
        if (clientId == null) clientId = -1L;

        for (PanierItem it : panier) {
            Log.d("ReservationData", "BilletId: " + it.getBilletId() + ", Quantite: " + it.getQuantite());

            Billet billet = new Billet(it.getBilletId());

            Client client = new Client(clientId);
            client.setNomclt(nom);
            client.setEmail(email);
            client.setTel(telephone);

            Reservation reservation = new Reservation(billet, client, it.getQuantite());
            demandes.add(reservation);
        }

        apiService.reserver(demandes).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> resp) {
                if (resp.isSuccessful() && Boolean.TRUE.equals(resp.body())) {
                    Toast.makeText(Reserver.this, "Réservation confirmée!", Toast.LENGTH_LONG).show();

                    // Envoyer l'email de confirmation
                    sendConfirmationEmail(email, nom, panier);

                    PanierManager.getInstance(Reserver.this).viderPanier();
                    finish();
                } else {
                    Toast.makeText(Reserver.this,
                            "Quantité insuffisante pour un ou plusieurs billets", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(Reserver.this, "Erreur réseau, réessaye.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Envoie un email de confirmation à l'utilisateur
     */
    private void sendConfirmationEmail(String email, String nom, List<PanierItem> panier) {
        // Générer un code de confirmation
        String confirmationCode = generateConfirmationCode();

        // Calculer la date d'expiration (24h)
        long expirationTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy à HH:mm", java.util.Locale.FRANCE);
        String expirationDate = sdf.format(new java.util.Date(expirationTime));

        // Construire le contenu de l'email
        StringBuilder body = new StringBuilder();
        body.append("Bonjour ").append(nom).append(",\n\n");
        body.append("Votre code de confirmation: ").append(confirmationCode).append("\n");
        body.append("Ce code expire le ").append(expirationDate).append("\n\n");
        body.append("Voici le détail de votre réservation :\n\n");

        double total = 0;
        for (PanierItem item : panier) {
            String line = String.format(
                    "- %s (%s) x%d : %.2f DT\n",
                    item.getSpectacleName(),
                    item.getCategorieId(),
                    item.getQuantite(),
                    item.getPrix() * item.getQuantite()
            );
            body.append(line);
            total += item.getPrix() * item.getQuantite();
        }

        body.append(String.format("\nTotal : %.2f DT\n\n", total));
        body.append("Merci de nous présenter ce code à l'entrée.\n");
        body.append("Cette réservation expire le ").append(expirationDate).append(".\n\n");
        body.append("À bientôt !\nL'équipe SpectaPro");

        // Sauvegarder le code de confirmation dans les préférences
        saveConfirmationCode(confirmationCode, expirationTime);

        // Créer et envoyer la requête d'email
        EmailRequest emailRequest = new EmailRequest(email, body.toString());

        // Afficher un message de loading
        Toast.makeText(this, "Envoi de l'email de confirmation...", Toast.LENGTH_SHORT).show();

        apiService.sendEmail(emailRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Reserver.this,
                            "Email de confirmation envoyé à " + email,
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.e("EmailError", "Code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("EmailError", "Body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("EmailError", "Error parsing error body", e);
                    }

                    Toast.makeText(Reserver.this,
                            "Erreur lors de l'envoi de l'email",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EmailError", "Network failure", t);
                Toast.makeText(Reserver.this,
                        "Échec réseau: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Génère un code de confirmation unique
     */
    private String generateConfirmationCode() {
        // Récupérer un ID unique (timestamp ou autre)
        long timestamp = System.currentTimeMillis();
        // Format: SP-{timestamp_court}
        return "SP-" + String.format("%04d", timestamp % 10000);
    }

    /**
     * Sauvegarde le code de confirmation avec sa date d'expiration
     */
    private void saveConfirmationCode(String code, long expirationTime) {
        android.content.SharedPreferences prefs = getSharedPreferences("reservations", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();

        // Utiliser le timestamp actuel comme identifiant unique
        long timestamp = System.currentTimeMillis();
        String key = "reservation_" + timestamp;

        // Sauvegarder le code avec les infos associées
        editor.putString(key, code);
        editor.putString(key + "_email", emailEditText.getText().toString().trim());
        editor.putLong(key + "_time", timestamp);
        editor.putLong(key + "_expiration", expirationTime);
        editor.apply();
    }
}