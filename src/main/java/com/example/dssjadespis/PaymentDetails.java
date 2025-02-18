package com.example.dssjadespis;

public class PaymentDetails {
    private String recipient;
    private double amount;
    private String currency;

    public PaymentDetails(String recipient, double amount, String currency) {
        this.recipient = recipient;
        this.amount = amount;
        this.currency = currency;
    }

    public String toJson() {
        return String.format("{\"recipient\":\"%s\",\"amount\":%.2f,\"currency\":\"%s\"}",
                recipient, amount, currency);
    }
}