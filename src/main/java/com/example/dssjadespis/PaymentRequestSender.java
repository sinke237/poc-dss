package com.example.dssjadespis;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class PaymentRequestSender {

    public static void sendPaymentRequest(String apiUrl, String paymentDetails, byte[] signature) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            //attach the signature in the request header
            httpPost.setHeader("X-Signature", new String(signature));

            //attach the payment details in the request body
            httpPost.setEntity(new StringEntity(paymentDetails));

            //send the request
            httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException("Error sending payment request: ",e);
        }
    }
}