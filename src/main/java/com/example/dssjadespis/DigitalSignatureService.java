package com.example.dssjadespis;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

import javax.security.auth.DestroyFailedException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;

public class DigitalSignatureService {

    public static byte[] signPaymentDetails(String paymentDetails, String privateKeyPath, String keyPassword) {
        try {
            // Use an empty char array for no password
            KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(keyPassword.toCharArray());

            try (KeyStoreSignatureTokenConnection token = new KeyStoreSignatureTokenConnection(new File(privateKeyPath), "PKCS12", passwordProtection)) {
                // Prepare the document to be signed
                DSSDocument document = new InMemoryDocument(paymentDetails.getBytes());

                // Set signature parameters
                JAdESSignatureParameters parameters = new JAdESSignatureParameters();
                parameters.setSignatureLevel(SignatureLevel.JAdES_BASELINE_B);
                parameters.setSigningCertificate(token.getKeys().get(0).getCertificate());
                parameters.setDigestAlgorithm(DigestAlgorithm.SHA256); // Set the digest algorithm
                parameters.setSignaturePackaging(SignaturePackaging.ENVELOPING); // Set the signature packaging

                // Retrieve the DSSPrivateKeyEntry
                DSSPrivateKeyEntry privateKeyEntry = token.getKeys().get(0);

                // Sign the document
                JAdESService service = new JAdESService(new CommonCertificateVerifier());
                ToBeSigned dataToSign = service.getDataToSign(document, parameters);
                SignatureValue signatureValue = token.sign(dataToSign, parameters.getDigestAlgorithm(), privateKeyEntry);

                // Sign the document and convert it to a byte array
                DSSDocument signedDocument = service.signDocument(document, parameters, signatureValue);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                signedDocument.writeTo(outputStream);
                return outputStream.toByteArray();

            } catch (IOException e) {
                throw new RuntimeException("Error writing signed document to byte array:", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error signing payment details: ", e);
        }
    }
}