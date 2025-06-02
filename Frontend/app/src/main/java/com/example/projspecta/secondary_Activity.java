package com.example.projspecta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projspecta.model.Client;
import com.example.projspecta.model.LoginRequest;
import com.example.projspecta.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class secondary_Activity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button btnLogin;
    private ApiService apiService;

    // Variables to hold billet info
    private long idBillet;
    private long spectacleId;
    private String categorieId;
    private int quantite;
    private double prix;
    private String description;
    private String spectacleName;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_cnx);

        // Setup Retrofit
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/") // Modifier si besoin
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiService = retrofit.create(ApiService.class);

        // Récupérer les données du billet passées via l'intent
        Intent intent = getIntent();
        idBillet = intent.getLongExtra("id_billet", -1);
        spectacleId = intent.getLongExtra("spectacle_id", -1);
        categorieId = intent.getStringExtra("categorie_id");
        quantite = intent.getIntExtra("quantite", 1);
        prix = intent.getDoubleExtra("prix", 0.0);
        description = intent.getStringExtra("description");
        spectacleName = intent.getStringExtra("spectacle_name");
        imageUrl = intent.getStringExtra("image_url");

        btnLogin.setOnClickListener(view -> loginUser());

        TextView registerTextView = findViewById(R.id.registerTextView);
        registerTextView.setOnClickListener(view -> {
            Intent i = new Intent(secondary_Activity.this, signup.class);
            startActivity(i);
        });
    }

    private void loginUser() {
        String userEmail = emailEditText.getText().toString().trim();
        String userPassword = passwordEditText.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer vos informations.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(userEmail, userPassword);

        apiService.login(loginRequest).enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Client client = response.body();

                    // Sauvegarde session
                    SessionManager session = SessionManager.getInstance(secondary_Activity.this);
                    session.setClientId(client.getIdclt());
                    session.setUserData(client.getEmail());
                    session.setLoggedIn(true);

                    Toast.makeText(secondary_Activity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();

                    // Ajouter au panier si les infos du billet sont disponibles
                    if (idBillet != -1 && spectacleId != -1 && categorieId != null && !categorieId.isEmpty()
                            && description != null && !description.isEmpty()
                            && spectacleName != null && !spectacleName.isEmpty()
                            && imageUrl != null && !imageUrl.isEmpty()) {

                        PanierManager.getInstance(getApplicationContext()).ajouterAuPanier(
                                idBillet,
                                spectacleId,
                                categorieId,
                                prix,
                                description,
                                quantite,
                                spectacleName,
                                imageUrl
                        );

                        Toast.makeText(secondary_Activity.this, "Billet ajouté après connexion", Toast.LENGTH_SHORT).show();
                    }

                    startActivity(new Intent(secondary_Activity.this, cartActivity.class));
                    finish();
                } else {
                    Toast.makeText(secondary_Activity.this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Toast.makeText(secondary_Activity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
