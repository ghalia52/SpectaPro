package com.example.projspecta.model;

public class EmailRequest {
    private String clientEmail;
    private String confirmationCode;

    public EmailRequest(String clientEmail, String confirmationCode) {
        this.clientEmail = clientEmail;
        this.confirmationCode = confirmationCode;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}
