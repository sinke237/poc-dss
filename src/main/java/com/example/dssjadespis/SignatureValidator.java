package com.example.dssjadespis;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;


public class SignatureValidator {

    public static boolean validateSignature(DSSDocument signedDocument, DSSDocument publicKey) {
        try {
            // Create a SignedDocumentValidator for the signed document
            SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signedDocument);

            // Set the certificate verifier
            CommonCertificateVerifier verifier = new CommonCertificateVerifier();
            validator.setCertificateVerifier(verifier);

            // Validate the document
            Reports reports = validator.validateDocument();

            // Get the SimpleReport to check the validation result
            SimpleReport simpleReport = reports.getSimpleReport();

            // Check if the signature is valid
            return simpleReport.isValid(simpleReport.getFirstSignatureId());
        } catch (Exception e) {
            throw new RuntimeException("Error validating signature", e);
        }
    }
}