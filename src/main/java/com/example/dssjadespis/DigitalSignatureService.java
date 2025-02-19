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
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.validation.CertificateVerifierBuilder; // Use the builder
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;

public class DigitalSignatureService {

    public static byte[] signPaymentDetails(String paymentDetails, String privateKeyPath, String keyPassword) {
        try {
            KeyStore.PasswordProtection passwordProtection =
                    new KeyStore.PasswordProtection("".toCharArray());

            try (KeyStoreSignatureTokenConnection token =
                         new KeyStoreSignatureTokenConnection(new File(privateKeyPath), "PKCS12", passwordProtection)) {

                // Document to sign
                DSSDocument document = new InMemoryDocument(paymentDetails.getBytes());

                // Signature parameters
                JAdESSignatureParameters parameters = new JAdESSignatureParameters();
                parameters.setSignatureLevel(SignatureLevel.JAdES_BASELINE_B);
                parameters.setSigningCertificate(token.getKeys().get(0).getCertificate());
                parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
                parameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);

                // Private key entry
                DSSPrivateKeyEntry privateKeyEntry = (DSSPrivateKeyEntry) token.getKeys().get(0);

                // Initialize CertificateVerifier using the builder
                CertificateVerifier certificateVerifier =
                        new CertificateVerifierBuilder(null).buildCompleteCopy(); // Build from null
                JAdESService service = new JAdESService(certificateVerifier);

                // Generate data to sign
                ToBeSigned dataToSign = service.getDataToSign(document, parameters);
                SignatureValue signatureValue = token.sign(dataToSign, parameters.getDigestAlgorithm(), privateKeyEntry);

                // Sign document
                DSSDocument signedDocument = service.signDocument(document, parameters, signatureValue);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                signedDocument.writeTo(outputStream);
                return outputStream.toByteArray();

            } catch (IOException e) {
                throw new RuntimeException("Error writing signed document:", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Signing failed:", e);
        }
    }
}