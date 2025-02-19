package com.example.dssjadespis;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class DssJadesPisApplication {

    public static void main(String[] args) throws IOException, IOException {
        // Retrieve payment details
        PaymentDetails paymentDetails = new PaymentDetails("Recipient Name", 100.00, "USD");
        String paymentDetailsJson = paymentDetails.toJson();

        // Generate digital signature
        byte[] signature = DigitalSignatureService.signPaymentDetails(
                paymentDetailsJson,
                "src/main/resources/privateKey.p12",
                ""  // Empty password if you used none
        );

        // Save the signed document
        Path signedFilePath = Paths.get("src/main/resources/signedDocument.json");
        Files.createDirectories(signedFilePath.getParent());  // Create directories if missing
        Files.write(signedFilePath, signature);

        // Validate signature
        DSSDocument signedDocument = new FileDocument(signedFilePath.toFile());
        DSSDocument publicKey = new FileDocument(new File("src/main/resources/publicKey.cer"));
        boolean isValid = SignatureValidator.validateSignature(signedDocument, publicKey);

        System.out.println("Signature is valid: " + isValid);
    }


}
