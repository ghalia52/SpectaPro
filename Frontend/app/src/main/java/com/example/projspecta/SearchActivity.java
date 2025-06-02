package com.example.projspecta;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projspecta.model.Spectacle;
import com.example.projspecta.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SpectacleAdapter adapter;
    private ApiService apiService;
    private Spinner citySpinner;
    ImageButton homeBtn, searchBtn, validBtn, accBtn;
    private boolean isSpinnerInitialized = false; // Add this at the top


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        EditText searchInput = findViewById(R.id.searchInput);
        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(v -> showDatePicker());

        citySpinner = findViewById(R.id.Loc_spinner);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        fetchCities();

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    if (isDate(query)) {
                        searchSpectaclesByDate(query);
                    } else {
                        searchSpectacles(query);
                    }
                } else {
                    Toast.makeText(this, "Veuillez entrer une recherche", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        ImageView searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                if (isDate(query)) {
                    searchSpectaclesByDate(query);
                } else {
                    searchSpectacles(query);
                }
            } else {
                Toast.makeText(this, "Veuillez entrer une recherche", Toast.LENGTH_SHORT).show();
            }
        });

        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        homeBtn = findViewById(R.id.home_btn);
        searchBtn = findViewById(R.id.search_btn);
        validBtn = findViewById(R.id.valid_btn);
        accBtn = findViewById(R.id.acc_btn);

        homeBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), thirdActivity.class));
            overridePendingTransition(0, 0);
        });

        searchBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            overridePendingTransition(0, 0);
        });

        validBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), cartActivity.class));
            overridePendingTransition(0, 0);
        });

        accBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Profile.class));
            overridePendingTransition(0, 0);
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        ContextThemeWrapper themedContext = new ContextThemeWrapper(this, R.style.YellowDatePickerDialog);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                themedContext,
                (view, year1, month1, dayOfMonth) -> {
                    String formattedDay = (dayOfMonth < 10 ? "0" : "") + dayOfMonth;
                    String formattedMonth = ((month1 + 1) < 10 ? "0" : "") + (month1 + 1);
                    String selectedDate = year1 + "-" + formattedMonth + "-" + formattedDay;

                    EditText searchInput = findViewById(R.id.searchInput);
                    searchInput.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }


    private void fetchCities() {
        apiService.getVilles().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> villes = response.body();
                    if (villes != null && !villes.isEmpty()) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchActivity.this,
                                android.R.layout.simple_spinner_item, villes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        citySpinner.setAdapter(adapter);

                        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (isSpinnerInitialized) { // Only fetch if user manually selected
                                    String selectedCity = parent.getItemAtPosition(position).toString();
                                    fetchSpectacles(selectedCity);
                                } else {
                                    isSpinnerInitialized = true; // First automatic trigger, ignore
                                }
                                ((TextView) view).setTextColor(Color.WHITE);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) { }
                        });

                    } else {
                        Toast.makeText(SearchActivity.this, "Aucune ville disponible", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Erreur lors de la récupération des villes: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Erreur réseau: ", t);
            }
        });
    }

    private void fetchSpectacles(String city) {
        apiService.getSpectaclesByVille(city).enqueue(new Callback<List<Spectacle>>() {
            @Override
            public void onResponse(Call<List<Spectacle>> call, Response<List<Spectacle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Spectacle> spectacles = response.body();
                    if (spectacles != null && !spectacles.isEmpty()) {

                        List<Spectacle> filteredSpectacles = new ArrayList<>();

                        for (Spectacle s : spectacles) {
                            // Vérifie que le spectacle a au moins un programme
                            if (s.getProgrammes() != null && !s.getProgrammes().isEmpty()) {
                                String dateString = s.getProgrammes().get(0).getDateProgramme(); // Prend la première date
                                if (dateString != null && !dateString.isEmpty()) {
                                    try {
                                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                        java.util.Date programmeDate = sdf.parse(dateString);
                                        java.util.Date currentDate = new java.util.Date();

                                        if (programmeDate != null && !programmeDate.before(currentDate)) {
                                            filteredSpectacles.add(s);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        // Trier par date de programme croissante
                        filteredSpectacles.sort((s1, s2) -> {
                            try {
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date d1 = sdf.parse(s1.getProgrammes().get(0).getDateProgramme());
                                java.util.Date d2 = sdf.parse(s2.getProgrammes().get(0).getDateProgramme());
                                return d1.compareTo(d2);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return 0;
                            }
                        });

                        if (!filteredSpectacles.isEmpty()) {
                            adapter = new SpectacleAdapter(SearchActivity.this, filteredSpectacles);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(SearchActivity.this, "Aucun spectacle trouvé pour " + city, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SearchActivity.this, "Aucun spectacle trouvé pour " + city, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Erreur lors de la récupération des spectacles: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Spectacle>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Erreur réseau: ", t);
            }
        });
    }

    private void searchSpectacles(String query) {
        apiService.universalSearch(query).enqueue(new Callback<List<Spectacle>>() {
            @Override
            public void onResponse(Call<List<Spectacle>> call, Response<List<Spectacle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Spectacle> spectacles = response.body();
                    adapter = new SpectacleAdapter(SearchActivity.this, spectacles);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(SearchActivity.this, "Aucun résultat trouvé.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Spectacle>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Erreur lors de la recherche: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Erreur lors de la recherche: ", t);
            }
        });
    }

    private void searchSpectaclesByDate(String date) {
        apiService.universalSearch(date).enqueue(new Callback<List<Spectacle>>() {
            @Override
            public void onResponse(Call<List<Spectacle>> call, Response<List<Spectacle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Spectacle> spectacles = response.body();
                    adapter = new SpectacleAdapter(SearchActivity.this, spectacles);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(SearchActivity.this, "Aucun résultat trouvé pour cette date.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Spectacle>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Erreur lors de la recherche: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Erreur lors de la recherche: ", t);
            }
        });
    }

    private boolean isDate(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
