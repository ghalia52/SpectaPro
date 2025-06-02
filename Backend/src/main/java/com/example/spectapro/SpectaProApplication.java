package com.example.spectapro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpectaProApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpectaProApplication.class, args);
        System.out.println(" Serveur Spring Boot lancé avec succès !");
    }
}
