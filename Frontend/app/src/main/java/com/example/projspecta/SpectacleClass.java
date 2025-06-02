package com.example.projspecta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.projspecta.model.Programme;
import com.example.projspecta.model.Spectacle;
import com.example.projspecta.model.rubrique;
import com.example.projspecta.network.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpectacleClass {
    private static final String TAG = "SpectacleClass";

    private final Context context;
    private final TextView titreTextView;
    private final Spinner spinnerDates;
    private final Spinner spinnerHeures;
    private final Spinner spinnerLieux;
    private final TextView dureeTextView;
    private final TextView nbreSpectTextView;
    private final TableLayout tableLayout;
    private final ImageView spectacleImage;
    private final ApiService apiService;

    public SpectacleClass(Context context,
                          TextView titreTextView,
                          Spinner spinnerDates,
                          Spinner spinnerHeures,
                          Spinner spinnerLieux,
                          TextView dureeTextView,
                          TextView nbreSpectTextView,
                          TableLayout tableLayout,
                          ImageView spectacleImage) {
        this.context           = context;
        this.titreTextView     = titreTextView;
        this.spinnerDates      = spinnerDates;
        this.spinnerHeures     = spinnerHeures;
        this.spinnerLieux      = spinnerLieux;
        this.dureeTextView     = dureeTextView;
        this.nbreSpectTextView = nbreSpectTextView;
        this.tableLayout       = tableLayout;
        this.spectacleImage    = spectacleImage;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.apiService = retrofit.create(ApiService.class);
    }

    public void populateSpectacle(Spectacle spectacle) {
        if (spectacle == null) return;

        String titre = spectacle.getTitre();
        String lieu = spectacle.getProgrammes().get(0).getLieu().getVille();
        String combined = titre + " " + lieu;
        String encodedCombined = Uri.encode(combined);
        String wikipediaUrl = "https://www.google.com/search?q=" + encodedCombined;

        SpannableString spannableString = new SpannableString(titre);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl));
                context.startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, 0, titre.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Apply the clickable text to the TextView
        titreTextView.setText(spannableString);
        titreTextView.setMovementMethod(LinkMovementMethod.getInstance());


        dureeTextView.setText(String.valueOf(spectacle.getDuree()));
        nbreSpectTextView.setText(String.valueOf(spectacle.getNbrSpectateur()));

        String imageUrl = "http://10.0.2.2:9090" + spectacle.getImagePath();
        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .transform(new RoundedCorners(50))
                .into(spectacleImage);

        fetchProgrammesForSpectacle(spectacle.getIdSpec());
    }

    private void fetchProgrammesForSpectacle(long spectacleId) {
        apiService.getProgrammesBySpectacle(spectacleId)
                .enqueue(new Callback<List<Programme>>() {
                    @Override
                    public void onResponse(Call<List<Programme>> call,
                                           Response<List<Programme>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Programme> progs = response.body();
                            if (progs.isEmpty()) {
                                Toast.makeText(context,
                                        "Aucun programme trouvé", Toast.LENGTH_SHORT).show();
                            } else {
                                setupProgrammes(progs);
                            }
                        } else {
                            Log.e(TAG, "Erreur code " + response.code());
                            Toast.makeText(context,
                                    "Erreur chargement programmes", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Programme>> call, Throwable t) {
                        Log.e(TAG, "fetchProgrammes error", t);
                        Toast.makeText(context,
                                "Erreur réseau programmes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setupProgrammes(List<Programme> programmes) {
        // 1) extraire la liste unique des lieux
        Set<String> lieuNames = new LinkedHashSet<>();
        // 2) pour chaque lieu, map date -> list heures
        Map<String, Map<String, List<String>>> map = new HashMap<>();

        for (Programme p : programmes) {
            String lieu  = p.getLieu() != null
                    ? p.getLieu().getNom()
                    : "Lieu inconnu";
            String date  = p.getDateProgramme();
            String heure = p.getHeureDepart();

            lieuNames.add(lieu);
            map
                    .computeIfAbsent(lieu, k -> new HashMap<>())
                    .computeIfAbsent(date, k -> new ArrayList<>())
                    .add(heure);
        }

        // Spinner Lieux
        List<String> lieuxList = new ArrayList<>(lieuNames);
        ArrayAdapter<String> lieuxAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                lieuxList
        );
        lieuxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLieux.setAdapter(lieuxAdapter);

        spinnerLieux.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent,
                                                 View view,
                                                 int pos,
                                                 long id) {
                String selectedLieu = lieuxList.get(pos);
                Map<String, List<String>> dateMap = map.get(selectedLieu);

                // Spinner Dates
                List<String> dates = new ArrayList<>(dateMap.keySet());
                ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_item,
                        dates
                );
                dateAdapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                );
                spinnerDates.setAdapter(dateAdapter);

                spinnerDates.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override public void onItemSelected(
                                    AdapterView<?> p2,
                                    View v2,
                                    int p2pos,
                                    long id2
                            ) {
                                String selDate = dates.get(p2pos);
                                List<String> heures = dateMap.get(selDate);
                                ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(
                                        context,
                                        android.R.layout.simple_spinner_item,
                                        heures
                                );
                                hourAdapter.setDropDownViewResource(
                                        android.R.layout.simple_spinner_dropdown_item
                                );
                                spinnerHeures.setAdapter(hourAdapter);
                            }
                            @Override public void onNothingSelected(
                                    AdapterView<?> p2
                            ) { }
                        }
                );

                if (!dates.isEmpty()) spinnerDates.setSelection(0);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        if (!lieuxList.isEmpty()) spinnerLieux.setSelection(0);
    }

    public void populateRubriques(List<rubrique> rubriques) {
        if (tableLayout.getChildCount() > 1) {
            tableLayout.removeViews(1,
                    tableLayout.getChildCount() - 1
            );
        }
        for (rubrique r : rubriques) {
            TableRow row = new TableRow(context);
            TextView type   = createCell(r.getType());
            TextView hDeb   = createCell(String.valueOf(r.getHDebutr()));
            TextView duree  = createCell(String.valueOf(r.getDureerub()));
            String artiste  = r.getArtiste() != null
                    ? r.getArtiste().getNomart()
                    + " " + r.getArtiste().getPrenomart()
                    : "";
            artiste = artiste.replace("null","").trim();
            TextView nomArt = createCell(artiste);

            row.addView(type);
            row.addView(hDeb);
            row.addView(duree);
            row.addView(nomArt);
            tableLayout.addView(row);
        }
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setPadding(8,8,8,8);
        tv.setGravity(Gravity.CENTER);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        );
        tv.setLayoutParams(params);
        return tv;
    }
}