package com.example.projspecta.network;

import com.example.projspecta.model.Billet;
import com.example.projspecta.model.Client;
import com.example.projspecta.model.EmailRequest;
import com.example.projspecta.model.Lieu;
import com.example.projspecta.model.LoginRequest;
import com.example.projspecta.model.Programme;
import com.example.projspecta.model.RegisterRequest;
import com.example.projspecta.model.Reservation;
import com.example.projspecta.model.Spectacle;
import com.example.projspecta.model.rubrique;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/clients/login")
    Call<Client> login(@Body LoginRequest loginRequest);
    // ← NOUVELLE MÉTHODE
    @GET("api/clients/{id}")
    Call<Client> getClientById(@Path("id") long clientId);

    // Spectacle endpoints
    @GET("api/spectacles")
    Call<List<Spectacle>> getAllSpectacles();

    @GET("api/spectacles/ville/{ville}")
    Call<List<Spectacle>> getSpectaclesByVille(@Path("ville") String ville);

    @GET("api/villes")
    Call<List<String>> getVilles();

    @GET("api/rubriques/bySpectacle/{id}")
    Call<List<rubrique>> getrubriques(@Path("id") long spectacleId);

    @GET("api/spectacles/{id}")
    Call<Spectacle> getSpectacleById(@Path("id") long id);

    @GET("/api/spectacles/search")
    Call<List<Spectacle>> universalSearch(@Query("query") String query);

    @GET("/api/billets/billets/available/{idSpec}")
    Call<List<Billet>> getAvailableBillets(@Path("idSpec") long idSpec);
    @POST("email/send")
    Call<Void> sendEmail(@Body EmailRequest request);

    @POST("api/clients/register")
    Call<Client> register(@Body RegisterRequest registerRequest);
    @POST("api/reservations/batch")
    Call<Boolean> reserver(@Body List<Reservation> reservations);

    @GET("api/programmes/spectacle/{spectacleId}")
    Call<List<Programme>> getProgrammesBySpectacle(@Path("spectacleId") Long spectacleId);


}









