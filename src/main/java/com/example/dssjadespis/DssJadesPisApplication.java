package com.example.dssjadespis;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class DssJadesPisApplication {

    public static void main(String[] args) {
        // Retrieve payment details
        PaymentDetails paymentDetails = new PaymentDetails("Recipient Name", 100.00, "USD");
        String paymentDetailsJson = paymentDetails.toJson();

        // generate digital signature
        byte[] signature = DigitalSignatureService.signPaymentDetails(paymentDetailsJson, "src/main/resources/privateKey.p12", "password");

        //send payment request
        PaymentRequestSender.sendPaymentRequest("https://bank-api.com/payment", paymentDetailsJson, signature);

        // validate signature
        DSSDocument signedDocument = new FileDocument(new File("src/main/resources/signedDocument.json"));
        DSSDocument publicKey = new FileDocument(new File("src/main/resources/publicKey.cer"));
        boolean isValid = SignatureValidator.validateSignature(signedDocument, publicKey);

        System.out.println("Signature is valid: " + isValid);

    }

}
