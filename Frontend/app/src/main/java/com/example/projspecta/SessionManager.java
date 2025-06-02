package com.example.projspecta;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "specta_pref";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_CLIENT_ID = "client_id";

    public void setClientId(long clientId) {
        prefs.edit().putLong(KEY_CLIENT_ID, clientId).apply();
    }

    public Long getClientId() {
        if (!prefs.contains(KEY_CLIENT_ID)) return null;
        return prefs.getLong(KEY_CLIENT_ID, -1L);
    }

    private static SessionManager instance;
    private SharedPreferences prefs;

    SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public void setUserData(String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public String getUserEmail() {
        return prefs.getString(KEY_EMAIL, "Email inconnu");
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
