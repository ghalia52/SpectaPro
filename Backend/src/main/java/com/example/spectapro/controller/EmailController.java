package com.example.spectapro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        emailService.sendConfirmationEmail(request.getClientEmail(), request.getConfirmationCode());
        return ResponseEntity.ok("Email envoyé avec succès !");
    }
}
