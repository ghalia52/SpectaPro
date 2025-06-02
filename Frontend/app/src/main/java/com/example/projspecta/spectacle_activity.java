package com.example.projspecta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projspecta.model.Billet;
import com.example.projspecta.model.Programme;
import com.example.projspecta.model.Spectacle;
import com.example.projspecta.model.rubrique;
import com.example.projspecta.network.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class spectacle_activity extends AppCompatActivity {
    ImageButton homeBtn, searchBtn, validBtn, accBtn;
    private static final String TAG = "spectacle_activity";
    private TextView titreTextView, dureeTextView, nbreSpectTextView;
    private TableLayout tableRubriques;
    private ImageView spectacleImage;
    private SpectacleClass spectacleAdapter;
    private ApiService apiService;
    private RecyclerView billetRecyclerView;
    private Spinner spinnerDates, spinnerHeures, spinnerLieux;
    private long selectedSpectacleId = -1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectacle);

        // --- Initialisation des vues ---
        homeBtn           = findViewById(R.id.home_btn);
        searchBtn         = findViewById(R.id.search_btn);
        validBtn          = findViewById(R.id.valid_btn);
        accBtn            = findViewById(R.id.acc_btn);
        titreTextView     = findViewById(R.id.titre);
        dureeTextView     = findViewById(R.id.duree);
        nbreSpectTextView = findViewById(R.id.Nbre_spec);
        tableRubriques    = findViewById(R.id.tableRubriques);
        spectacleImage    = findViewById(R.id.imageView);

        spinnerDates  = findViewById(R.id.date);
        spinnerHeures = findViewById(R.id.Heure);
        spinnerLieux  = findViewById(R.id.LieuSpinner);

        billetRecyclerView = findViewById(R.id.billetRecyclerView);
        billetRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- Configuration de l'adapter SpectacleClass ---
        spectacleAdapter = new SpectacleClass(
                this,
                titreTextView,
                spinnerDates,
                spinnerHeures,
                spinnerLieux,
                dureeTextView,
                nbreSpectTextView,
                tableRubriques,
                spectacleImage
        );

        // --- Retrofit / ApiService ---
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // --- Navigation boutons ---
        homeBtn.setOnClickListener(v -> startActivity(new Intent(this, thirdActivity.class)));
        searchBtn.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        validBtn.setOnClickListener(v -> startActivity(new Intent(this, cartActivity.class)));
        accBtn.setOnClickListener(v -> startActivity(new Intent(this, Profile.class)));

        // --- Récupération de l'ID de spectacle passé en extra ---
        if (getIntent() != null && getIntent().hasExtra("spectacle_id")) {
            selectedSpectacleId = getIntent().getLongExtra("spectacle_id", -1);
            Log.d(TAG, "Received spectacle id: " + selectedSpectacleId);
        }

        if (selectedSpectacleId != -1) {
            fetchSpectacleById(selectedSpectacleId);
        } else {
            Toast.makeText(this, "No spectacle ID passed!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No spectacle ID passed!");
        }
    }

    private void fetchSpectacleById(long id) {
        apiService.getSpectacleById(id).enqueue(new Callback<Spectacle>() {
            @Override
            public void onResponse(Call<Spectacle> call, Response<Spectacle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Spectacle spectacle = response.body();
                    spectacleAdapter.populateSpectacle(spectacle);
                    fetchRubriquesForSpectacle(spectacle.getIdSpec());
                    fetchBilletsForSpectacle(spectacle.getIdSpec());
                    fetchProgrammesForSpectacle(spectacle.getIdSpec());
                } else {
                    Toast.makeText(spectacle_activity.this,
                            "Spectacle not found!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Spectacle not found. " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Spectacle> call, Throwable t) {
                Toast.makeText(spectacle_activity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching spectacle: ", t);
            }
        });
    }

    private void fetchProgrammesForSpectacle(long spectacleId) {
        apiService.getProgrammesBySpectacle(spectacleId)
                .enqueue(new Callback<List<Programme>>() {
                    @Override
                    public void onResponse(
                            Call<List<Programme>> call,
                            Response<List<Programme>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Programme> progs = response.body();
                            Log.d(TAG, "Programmes fetched: " + progs.size());
                            // Affichez-les via votre SpectacleClass
                            spectacleAdapter.setupProgrammes(progs);
                        } else {
                            Toast.makeText(spectacle_activity.this,
                                    "No programmes found.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "No programmes. " + response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Programme>> call, Throwable t) {
                        Toast.makeText(spectacle_activity.this,
                                "Error loading programmes: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error fetching programmes: ", t);
                    }
                });
    }

    private void fetchRubriquesForSpectacle(long spectacleId) {
        apiService.getrubriques(spectacleId)
                .enqueue(new Callback<List<rubrique>>() {
                    @Override
                    public void onResponse(
                            Call<List<rubrique>> call,
                            Response<List<rubrique>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            spectacleAdapter.populateRubriques(response.body());
                        } else {
                            Toast.makeText(spectacle_activity.this,
                                    "No rubriques found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<rubrique>> call, Throwable t) {
                        Toast.makeText(spectacle_activity.this,
                                "Error loading rubriques: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchBilletsForSpectacle(long spectacleId) {
        apiService.getAvailableBillets(spectacleId)
                .enqueue(new Callback<List<Billet>>() {
                    @Override
                    public void onResponse(
                            Call<List<Billet>> call,
                            Response<List<Billet>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            billetRecyclerView.setAdapter(
                                    new BilletAdapter(response.body())
                            );
                        } else {
                            Toast.makeText(spectacle_activity.this,
                                    "No billets found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Billet>> call, Throwable t) {
                        Toast.makeText(spectacle_activity.this,
                                "Error loading billets: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
