package com.example.spectapro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    private final String fromEmail = "no-reply@tonapp.com"; // Adresse email de l'expéditeur

    public void sendConfirmationEmail(String clientEmail, String confirmationCode) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(clientEmail);
            helper.setSubject("Confirmation de Réservation");
            helper.setText("Votre code de confirmation est : " + confirmationCode + ". Il expire dans 24 heures.", true);

            emailSender.send(mimeMessage);
            System.out.println("Email envoyé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur d'envoi de l'email.");
        }
    }
}

