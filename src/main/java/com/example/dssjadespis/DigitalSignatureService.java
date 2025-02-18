package com.example.dssjadespis;

import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;

import java.io.File;

public class DigitalSignatureService {
    public static byte[] signPaymentDetails(String paymentDetails, String privateKeyPath, String keyPassword) {
        try (KeyStoreSignatureTokenConnection token = new KeyStoreSignatureTokenConnection(new File(privateKeyPath), keyPassword.toCharArray())) {
            // Prepare Document to be signed
            DSSDocument document = new FileDocument(new File(paymentDetails));

            // set signature parameters
            JAdESSignatureParameters parameters = new JAdESSignatureParameters();
            parameters.setSignatureLevel(SignatureLevel.JAdES_BASELINE_B);
            parameters.setSigningCertificate(token.getKeys().get(0).getCertificate());

            //sign the document
            JAdESService service = new JAdESService(new CommonCertificateVerifier());
            ToBeSigned dataToSign = service.getDataToSign(document, parameters);
            SignatureValue signatureValue = token.sign(dataToSign, parameters.getDigestAlgorithm(), token.getKeys().get(0));

            //return the doc
            return service.signDocument(document, parameters, signatureValue).getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error signing payment details: ", e);
        }
    }
}