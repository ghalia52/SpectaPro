package com.example.projspecta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projspecta.model.Billet;
import com.example.projspecta.model.Client;
import com.example.projspecta.model.EmailRequest;
import com.example.projspecta.model.LoginRequest;
import com.example.projspecta.model.PanierItem;
import com.example.projspecta.model.Reservation;
import com.example.projspecta.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class cartActivity extends AppCompatActivity
        implements PanierAdapter.CartChangeListener {

    private TextView totalTextView;
    private PanierAdapter adapter;
    private List<PanierItem> panier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        // Apply inset padding for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        // Bottom nav buttons
        findViewById(R.id.home_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, thirdActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.search_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.valid_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, cartActivity.class));
        });
        findViewById(R.id.acc_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
            overridePendingTransition(0, 0);
        });
        // dans onCreate()
        findViewById(R.id.valider).setOnClickListener(v -> {
            if (SessionManager.getInstance(this).isLoggedIn()) {
                Long clientId = SessionManager.getInstance(this).getClientId();
                if (clientId != null && clientId != -1L) {
                    fetchClientAndReserve(clientId);
                    sendReservationByEmail();
                } else {
                    Toast.makeText(this, "Erreur : ID client introuvable", Toast.LENGTH_LONG).show();
                }
            } else {
                startActivity(new Intent(this, Reserver.class));
                overridePendingTransition(0, 0);
            }
        });

        // RecyclerView setup
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 1) Fetch the cart items via PanierManager
        panier = PanierManager.getInstance(this).getPanierList();

        // 2) Hook up adapter using the same list
        totalTextView = findViewById(R.id.textView5);
        adapter = new PanierAdapter(this, panier, this);
        rv.setAdapter(adapter);

        // Initial total
        updateTotal();
    }


    private void fetchClientAndReserve(long clientId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        api.getClientById(clientId).enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    Client c = resp.body();
                    // Appel à ReservationHelper avec les infos récupérées
                    ReservationHelper.reserver(
                            cartActivity.this,
                            c.getNomclt() + " " + c.getPrenomclt(),
                            c.getEmail(),
                            c.getTel()
                    );
                } else {
                    Toast.makeText(cartActivity.this,
                            "Impossible de récupérer les infos client", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Toast.makeText(cartActivity.this,
                        "Erreur réseau, réessaye", Toast.LENGTH_LONG).show();
            }
        });
    }




    @Override
    public void onCartChanged() {
        // Re-fetch the persisted cart (in case underlying JSON changed)
        panier.clear();
        panier.addAll(PanierManager.getInstance(this).getPanierList());
        adapter.notifyDataSetChanged();
        updateTotal();
    }

    /** Sum up price * quantity and display */
    private void updateTotal() {
        double total = 0;
        for (PanierItem it : panier) {
            total += it.getPrix() * it.getQuantite();
        }
        totalTextView.setText(String.format("%.2f DT", total));
    }


    /**
     * Generates a unique confirmation code for the reservation
     */
    private String generateConfirmationCode() {
        // Get client ID from session
        Long clientId = SessionManager.getInstance(this).getClientId();
        // Create a unique code using timestamp and client ID
        long timestamp = System.currentTimeMillis();
        // Format: SP-{clientId}-{last 4 digits of timestamp}
        return "SP-" + clientId + "-" + String.format("%04d", timestamp % 10000);
    }
    private void sendReservationByEmail() {
        // Generate a simple confirmation code
        String confirmationCode = generateConfirmationCode();

        // Calculate expiration time (24 hours from now)
        long expirationTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        // Format the expiration date/time
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy à HH:mm", java.util.Locale.FRANCE);
        String expirationDate = sdf.format(new java.util.Date(expirationTime));

        // 1) Build email subject/body
        String subject = "Votre réservation SpectaPro";
        StringBuilder body = new StringBuilder();
        body.append("Bonjour,\n\n");
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
        body.append("À bientôt !\nSpectaPro");

        // Save confirmation code to SharedPreferences with expiration
        saveConfirmationCode(confirmationCode, expirationTime);

        // Get client info from SessionManager
        Long clientId = SessionManager.getInstance(this).getClientId();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        // Fetch client details to get email
        api.getClientById(clientId).enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Client client = response.body();

                    EmailRequest emailRequest = new EmailRequest(client.getEmail(), confirmationCode);

                    api.sendEmail(emailRequest).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(cartActivity.this, "Confirmation envoyée à " + client.getEmail(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(cartActivity.this, "Erreur lors de l'envoi de l'email", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(cartActivity.this, "Échec réseau", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(cartActivity.this, "Impossible de récupérer les infos client", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Toast.makeText(cartActivity.this, "Erreur réseau, réessaye", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Save the confirmation code to SharedPreferences with expiration time
     */
    private void saveConfirmationCode(String code, long expirationTime) {
        SharedPreferences prefs = getSharedPreferences("reservations", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Use current timestamp as a unique identifier
        long timestamp = System.currentTimeMillis();
        String key = "reservation_" + timestamp;

        // Save confirmation code with client ID, timestamp and expiration
        editor.putString(key, code);
        editor.putLong(key + "_client", SessionManager.getInstance(this).getClientId());
        editor.putLong(key + "_time", timestamp);
        editor.putLong(key + "_expiration", expirationTime);
        editor.apply();
    }

}
