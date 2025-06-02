package com.example.projspecta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Profile extends AppCompatActivity {

    private TextView profileNameText, profileEmailText;
    private MaterialButton logoutButton;
    private SessionManager sessionManager;
    ImageButton homeBtn, searchBtn, validBtn, accBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Bottom navigation buttons
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

        profileEmailText = findViewById(R.id.emailTextView);
        logoutButton = findViewById(R.id.logoutButton);
        profileNameText = findViewById(R.id.profileName);

        sessionManager = SessionManager.getInstance(this);

        if (sessionManager.isLoggedIn()) {
            String email = sessionManager.getUserEmail();
            profileEmailText.setText(email);
            String username = email.split("@")[0];
            profileNameText.setText(username);

            logoutButton.setText("Se déconnecter");
            logoutButton.setOnClickListener(view -> {
                sessionManager.logout();
                Intent intent = new Intent(Profile.this, secondary_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });

        } else {
            profileEmailText.setText("Non connecté");
            logoutButton.setText("Se connecter");
            logoutButton.setOnClickListener(view -> {
                startActivity(new Intent(Profile.this, secondary_Activity.class));
            });
        }

    }
}
