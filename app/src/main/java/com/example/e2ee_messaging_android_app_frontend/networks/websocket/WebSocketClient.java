package com.example.e2ee_messaging_android_app_frontend.networks.websocket;

import androidx.annotation.NonNull;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient extends WebSocketListener {
    private final String WS_URL;
    private WebSocket webSocket;

    private SSLSocketFactory createSSLSocketFactory() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WebSocketClient(String webSocketUrl) {
        WS_URL = webSocketUrl;

    }

    public void start() {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }


        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();

        Request request = new Request.Builder()
                .url(this.WS_URL)
                .build();

        webSocket = client.newWebSocket(request, this);
    }

    public void stop() {
        webSocket.close(1000, "Closing WebSocket connection");
    }

    @Override
    public void onOpen(WebSocket webSocket, @NonNull Response response) {
        // WebSocket connection is established
        // You can send messages or perform any necessary actions here
        webSocket.send("Hello, Server!");
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        // Received a text message from the server
        // Handle the message as needed
        System.out.println("Received message: " + text);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        // WebSocket connection is closed
        // Clean up resources or perform any necessary actions here
        System.out.println("Closed: " + reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        // WebSocket connection failure
        // Handle the failure or error here
        System.out.println("Error: " + t.getMessage());
    }
}