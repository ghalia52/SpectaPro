package com.example.projspecta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.projspecta.model.Client;
import com.example.projspecta.model.RegisterRequest;
import com.example.projspecta.network.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class signup extends AppCompatActivity {

    private TextInputEditText fullNameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private ApiService apiService;
    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize API service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/") // Assuming same backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Initialize UI components
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
    }

    private void setupListeners() {
        // Register button click listener
        MaterialButton registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> attemptRegistration());

        // Login text click listener
        TextView loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(v -> {
            // Navigate to login activity
            Intent intent = new Intent(signup.this, secondary_Activity.class);
            startActivity(intent);
            finish(); // Close this activity
        });
    }

    private void attemptRegistration() {
        // Get input values
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(fullName, email, phone, password, confirmPassword)) {
            return;
        }

        // Show loading indicator
        Toast.makeText(this, "Création du compte en cours...", Toast.LENGTH_SHORT).show();

        // Parse full name into first and last name
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Create register request
        RegisterRequest registerRequest = new RegisterRequest(
                lastName,
                firstName,
                email,
                phone,
                password
        );

        // Send registration request
        apiService.register(registerRequest).enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Client client = response.body();
                    // Save client info to session
                    SessionManager.getInstance(signup.this).setUserData(
                            client.getEmail()
                    );

                    // Show success message
                    Toast.makeText(signup.this, "Compte créé avec succès!", Toast.LENGTH_LONG).show();

                    // Redirect to home page
                    Intent intent = new Intent(signup.this, thirdActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle error response
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Registration error: " + errorBody);

                        if (errorBody.contains("email") || response.code() == 409) {
                            Toast.makeText(signup.this,
                                    "Cette adresse email est déjà utilisée", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(signup.this,
                                    "Erreur lors de la création du compte: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        Toast.makeText(signup.this,
                                "Erreur lors de la création du compte", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Log.e(TAG, "Network error during registration", t);
                Toast.makeText(signup.this,
                        "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs(String fullName, String email, String phone,
                                   String password, String confirmPassword) {
        // Check for empty fields
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
            return false;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Format d'email invalide", Toast.LENGTH_LONG).show();
            return false;
        }

        // Validate phone number
        if (!Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, "Format de numéro de téléphone invalide", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check password length
        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}